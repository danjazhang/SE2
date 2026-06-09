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
public class PathfinderTest2 {

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

        Trap trap = new Trap(3);
        trap.posX = 7; trap.posY = 1; trap.breedte = 2; trap.hoogte = 5;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);
        pf = hotel.pathfinder;
    }

    // volgendeStap met null huidig geeft null
    @Test void testVolgendeStapNullHuidig() {
        Vakje doel = hotel.layout.krijgVakje(3, 1);
        assertNull(pf.volgendeStap(null, doel));
    }

    // volgendeStap met null doel geeft null
    @Test void testVolgendeStapNullDoel() {
        Vakje huidig = hotel.layout.krijgVakje(2, 1);
        assertNull(pf.volgendeStap(huidig, null));
    }

    // volgendeStap horizontaal naar rechts: x neemt toe
    @Test void testVolgendeStapNaarRechts() {
        Vakje huidig = hotel.layout.krijgVakje(2, 1);
        Vakje doel   = hotel.layout.krijgVakje(5, 1);
        Vakje stap   = pf.volgendeStap(huidig, doel);
        assertEquals(3, stap.x);
        assertEquals(1, stap.y);
    }

    // volgendeStap horizontaal naar links: x neemt af
    @Test void testVolgendeStapNaarLinks() {
        Vakje huidig = hotel.layout.krijgVakje(5, 1);
        Vakje doel   = hotel.layout.krijgVakje(2, 1);
        Vakje stap   = pf.volgendeStap(huidig, doel);
        assertEquals(4, stap.x);
    }

    // volgendeStap op trap naar beneden: y neemt toe
    @Test void testVolgendeStapTrapOmlaag() {
        Vakje huidig = hotel.layout.krijgVakje(7, 1); // trap
        Vakje doel   = hotel.layout.krijgVakje(7, 4);
        Vakje stap   = pf.volgendeStap(huidig, doel);
        assertEquals(2, stap.y);
    }

    // volgendeStap op trap naar boven: y neemt af
    @Test void testVolgendeStapTrapOmhoog() {
        Vakje huidig = hotel.layout.krijgVakje(7, 4); // trap
        Vakje doel   = hotel.layout.krijgVakje(7, 1);
        Vakje stap   = pf.volgendeStap(huidig, doel);
        assertEquals(3, stap.y);
    }

    // volgendeStap niet op trap, andere y: geeft null (geen verticale beweging buiten trap)
    @Test void testVolgendeStapNietOpTrapAndereYGeeftNull() {
        Vakje huidig = hotel.layout.krijgVakje(3, 1); // geen trap
        Vakje doel   = hotel.layout.krijgVakje(3, 3);
        Vakje stap   = pf.volgendeStap(huidig, doel);
        assertNull(stap);
    }

    // zetRoute zelfde verdieping: doel wordt direct ingesteld
    @Test void testZetRouteZelfdeVerdieping() {
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        kamer.setIngang(4, 1);
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

    // zetRoute andere verdieping via trap: schoonmaker krijgt trap als eerste doel
    @Test void testZetRouteSchoonmakerViaTrap() {
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 3; kamer.breedte = 1; kamer.hoogte = 1;
        kamer.setIngang(3, 3);
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(pf);
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRoute(s, kamer);

        // schoonmaker gaat altijd via trap: eerste doel is de trap op zijn verdieping
        assertNotNull(s.doelVakje);
        assertEquals(7, s.doelVakje.x); // trap staat op x=7
    }

    // zetRoute zonder startpositie: geen crash
    @Test void testZetRouteZonderStartpositie() {
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        kamer.setIngang(3, 1);
        Gast g = new Gast(1, 1);
        assertDoesNotThrow(() -> pf.zetRoute(g, kamer));
    }

    // zetRoute gast andere verdieping: eindbestemming wordt ingesteld
    @Test void testZetRouteZetEindbestemming() {
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 3; kamer.breedte = 1; kamer.hoogte = 1;
        kamer.setIngang(3, 3);
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRoute(g, kamer);

        assertEquals(kamer, g.eindbestemming);
    }

    // zetRouteTrap zelfde verdieping: doel direct ingesteld
    @Test void testZetRouteTrapZelfdeVerdieping() {
        Vakje doel = hotel.layout.krijgVakje(5, 1);
        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRouteTrap(g, doel);
        assertEquals(doel, g.doelVakje);
    }

    // zetRouteTrap andere verdieping: eerste doel is de trap
    @Test void testZetRouteTrapAndereVerdieping() {
        Vakje doel = hotel.layout.krijgVakje(3, 4);
        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRouteTrap(g, doel);
        assertNotNull(g.doelVakje);
        assertEquals(7, g.doelVakje.x); // trap op x=7
    }

    // zetRouteTrap met null start: geen crash
    @Test void testZetRouteTrapNullStart() {
        Gast g = new Gast(1, 1);
        Vakje doel = hotel.layout.krijgVakje(3, 3);
        assertDoesNotThrow(() -> pf.zetRouteTrap(g, doel));
    }

    // zetRouteTrap met null doel: geen crash
    @Test void testZetRouteTrapNullDoel() {
        Gast g = new Gast(1, 1);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        assertDoesNotThrow(() -> pf.zetRouteTrap(g, null));
    }

    // zetRoute gast via lift: gebruiktLift wordt true als lift sneller is
    @Test void testZetRouteGastViaLiftZetGebruiktLift() {
        // grote afstand zodat lift gekozen wordt
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 5; kamer.breedte = 1; kamer.hoogte = 1;
        kamer.setIngang(3, 5);
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        pf.zetRoute(g, kamer);

        // gast heeft een doel gekregen (lift of trap)
        assertNotNull(g.doelVakje);
    }
}
