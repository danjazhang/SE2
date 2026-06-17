import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Pathfinder: volgendeStap, zetRoute, zetRouteTrap
public class PathfinderTest {

    private Hotel hotel;
    private Pathfinder pf;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(8, 5);
        hotel.breedte = 8;
        hotel.hoogte = 5;

        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 5;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);
        trap.posX = 7; trap.posY = 1; trap.breedte = 2; trap.hoogte = 5;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);
        pf = hotel.pathfinder;
    }

    // constructor: aanmaken lukt
    @Test void testConstructor() {
        assertNotNull(pf);
    }

    // volgendeStap: null huidig → null
    @Test void testVolgendeStapNullHuidig() {
        assertNull(pf.volgendeStap(null, hotel.layout.krijgVakje(3, 1)));
    }

    // volgendeStap: null doel → null
    @Test void testVolgendeStapNullDoel() {
        assertNull(pf.volgendeStap(hotel.layout.krijgVakje(2, 1), null));
    }

    // volgendeStap: null beide → null
    @Test void testVolgendeStapNullBeide() {
        assertNull(pf.volgendeStap(null, null));
    }

    // volgendeStap: horizontaal naar rechts
    @Test void testVolgendeStapRechts() {
        Vakje stap = pf.volgendeStap(hotel.layout.krijgVakje(2, 1), hotel.layout.krijgVakje(5, 1));
        assertEquals(3, stap.x);
        assertEquals(1, stap.y);
    }

    // volgendeStap: horizontaal naar links
    @Test void testVolgendeStapLinks() {
        Vakje stap = pf.volgendeStap(hotel.layout.krijgVakje(5, 1), hotel.layout.krijgVakje(2, 1));
        assertEquals(4, stap.x);
    }

    // volgendeStap: zelfde positie → zelfde vakje terug
    @Test void testVolgendeStapGeenBeweging() {
        Vakje v = hotel.layout.krijgVakje(2, 1);
        Vakje stap = pf.volgendeStap(v, v);
        assertEquals(2, stap.x);
        assertEquals(1, stap.y);
    }

    // volgendeStap: op trap omhoog
    @Test void testVolgendeStapTrapOmhoog() {
        Vakje huidig = hotel.layout.krijgVakje(7, 1); // trap
        Vakje doel   = hotel.layout.krijgVakje(7, 4);
        assertEquals(2, pf.volgendeStap(huidig, doel).y);
    }

    // volgendeStap: op trap omlaag
    @Test void testVolgendeStapTrapOmlaag() {
        Vakje huidig = hotel.layout.krijgVakje(7, 4); // trap
        Vakje doel   = hotel.layout.krijgVakje(7, 1);
        assertEquals(3, pf.volgendeStap(huidig, doel).y);
    }

    // volgendeStap: niet op trap, andere y → pathfinder beweegt horizontaal richting doel-x
    @Test void testVolgendeStapNietOpTrapAndereY() {
        // Vakje (3,1) heeft geen trap, doel op (5,3): zelfde x want x!=doel.x → beweegt horizontaal
        Vakje stap = pf.volgendeStap(hotel.layout.krijgVakje(3, 1), hotel.layout.krijgVakje(5, 3));
        // pathfinder stuurt horizontaal richting doel.x als y verschilt maar vakje geen trap is
        assertNotNull(stap);
        assertEquals(4, stap.x); // één stap richting x=5
    }

    // zetRoute: zelfde verdieping → doel direct ingesteld
    @Test void testZetRouteZelfdeVerdieping() {
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRoute(g, kamer);

        assertNotNull(g.doelVakje);
        assertEquals(4, g.doelVakje.x);
        assertEquals(1, g.doelVakje.y);
    }

    // zetRoute: andere verdieping → eindbestemming ingesteld
    @Test void testZetRouteZetEindbestemming() {
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 3; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRoute(g, kamer);

        assertEquals(kamer, g.eindbestemming);
    }

    // zetRoute: gast gebruikt lift bij grote verticale afstand (5 verdiepingen)
    @Test void testZetRouteViaLift() {
        // layout is 8x5, dus we hebben maximaal 5 verdiepingen
        // Trap tijdperverdieping=2, afstand y=1→y=5 = 4 * 2 = 8 ticks via trap
        // Lift schatting: wacht=0 + rit=4 + queue=0 + 2 = 6 ticks → lift wint
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 5; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRoute(g, kamer);

        assertTrue(g.gebruiktLift);
    }

    // zetRoute: schoonmaker gaat altijd via trap
    @Test void testZetRouteSchoonmakerViaTrap() {
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 3; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(pf);
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRoute(s, kamer);

        assertNotNull(s.doelVakje);
        assertEquals(7, s.doelVakje.x); // trap op x=7
    }

    // zetRoute: geen crash zonder startpositie
    @Test void testZetRouteZonderStart() {
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        assertDoesNotThrow(() -> pf.zetRoute(new Gast(1, 1), kamer));
    }

    // zetRouteTrap: zelfde verdieping → doel direct
    @Test void testZetRouteTrapZelfdeVerdieping() {
        Vakje doel = hotel.layout.krijgVakje(5, 1);
        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRouteTrap(g, doel);
        assertEquals(doel, g.doelVakje);
    }

    // zetRouteTrap: andere verdieping → eerste doel is de trap
    @Test void testZetRouteTrapAndereVerdieping() {
        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRouteTrap(g, hotel.layout.krijgVakje(3, 4));
        assertNotNull(g.doelVakje);
        assertEquals(7, g.doelVakje.x); // trap op x=7
    }

    // zetRouteTrap: geen crash met null start
    @Test void testZetRouteTrapNullStart() {
        assertDoesNotThrow(() -> pf.zetRouteTrap(new Gast(1, 1), hotel.layout.krijgVakje(3, 3)));
    }

    // zetRouteTrap: geen crash met null doel
    @Test void testZetRouteTrapNullDoel() {
        Gast g = new Gast(1, 1);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        assertDoesNotThrow(() -> pf.zetRouteTrap(g, null));
    }
}
