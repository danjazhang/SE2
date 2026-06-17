import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Persoon (via Gast/Schoonmaker): beweeg, wisRoute, tussendoelen, trapvertraging, evacueer
public class PersoonTest {

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
        lift.initWachtrijen(5);

        Trap trap = new Trap(2);
        trap.posX = 7; trap.posY = 1; trap.breedte = 2; trap.hoogte = 5;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);
        pf = hotel.pathfinder;
    }

    // beweeg: persoon zonder doel beweegt niet
    @Test void testBeweegZonderDoel() {
        Gast g = maakGast(1, 3, 1);
        Vakje voor = g.huidigVakje;
        g.beweeg();
        assertEquals(voor, g.huidigVakje);
    }

    // beweeg: persoon beweegt één stap richting doel
    @Test void testBeweegEenStap() {
        Gast g = maakGast(1, 2, 1);
        g.zetDoel(hotel.layout.krijgVakje(5, 1));
        g.beweeg();
        assertEquals(3, g.huidigVakje.x);
    }

    // beweeg: persoon bereikt doel en stopt
    @Test void testBeweegBereiktDoel() {
        Gast g = maakGast(1, 4, 1);
        g.zetDoel(hotel.layout.krijgVakje(5, 1));
        g.beweeg();
        assertEquals(5, g.huidigVakje.x);
        g.beweeg(); // doel bereikt, tussendoel queue leeg → stopt
        assertEquals(5, g.huidigVakje.x);
    }

    // beweeg: persoon met tussendoel werkt het doel af en gaat dan naar tussendoel
    @Test void testBeweegTussendoel() {
        Gast g = maakGast(1, 2, 1);
        g.zetDoel(hotel.layout.krijgVakje(3, 1));
        g.voegTussendoelToe(hotel.layout.krijgVakje(5, 1));
        g.beweeg(); // stap richting (3,1)
        assertEquals(3, g.huidigVakje.x);
        g.beweeg(); // op doel, pak tussendoel (5,1)
        // nu heeft g doelVakje=(5,1) en beweegt één stap
        assertEquals(4, g.huidigVakje.x);
    }

    // wisRoute: wist doel en tussendoelen
    @Test void testWisRoute() {
        Gast g = maakGast(1, 2, 1);
        g.zetDoel(hotel.layout.krijgVakje(5, 1));
        g.voegTussendoelToe(hotel.layout.krijgVakje(7, 1));
        g.wisRoute();
        assertNull(g.doelVakje);
        g.beweeg(); // geen beweging want geen doel
        assertEquals(2, g.huidigVakje.x);
    }

    // zetStartPositie: persoon staat op het vakje, vakje bevat persoon
    @Test void testZetStartPositie() {
        Gast g = new Gast(1, 1);
        Vakje v = hotel.layout.krijgVakje(3, 2);
        g.zetStartPositie(v);
        assertEquals(v, g.huidigVakje);
        assertTrue(v.personen.contains(g));
    }

    // beweeg: persoon zonder pathfinder beweegt niet
    @Test void testBeweegZonderPathfinder() {
        Gast g = new Gast(1, 1);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        g.zetDoel(hotel.layout.krijgVakje(5, 1));
        g.beweeg();
        // zonder pathfinder geen stap
        assertEquals(2, g.huidigVakje.x);
    }

    // beweeg: persoon zonder huidigVakje beweegt niet
    @Test void testBeweegZonderHuidigVakje() {
        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        g.zetDoel(hotel.layout.krijgVakje(3, 1));
        assertDoesNotThrow(() -> g.beweeg());
    }

    // trapvertraging: persoon op trap beweegt trager
    @Test void testTrapVertraging() {
        Trap trap = hotel.trap;
        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        Vakje trapVakje = hotel.layout.krijgVakje(7, 1);
        g.zetStartPositie(trapVakje);
        g.zetDoel(hotel.layout.krijgVakje(7, 3));
        // trapTijd = 2, dus eerste beweeg telt de vertraging, tweede pas beweegt
        g.beweeg(); // trapTicks = 2, dan 1
        assertEquals(7, g.huidigVakje.x);
        assertEquals(1, g.huidigVakje.y); // nog niet bewogen
        g.beweeg(); // trapTicks = 0 → beweeg nu
        assertEquals(2, g.huidigVakje.y);
    }

    // beweeg: verlaat/betreed ruimte worden aangeroepen
    @Test void testBeweegBetreedt() {
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        Gast g = maakGast(1, 2, 1);
        g.zetDoel(hotel.layout.krijgVakje(3, 1));
        g.beweeg();
        assertTrue(kamer.getAanwezigen().contains(g));
    }

    // evacueer: persoon krijgt route naar uitgang
    @Test void testEvacueer() {
        Gast g = maakGast(1, 3, 3);
        Vakje uitgang = hotel.layout.krijgVakje(4, 1);
        g.evacueer(uitgang, pf);
        assertNotNull(g.doelVakje);
    }

    // evacueer: persoon zonder huidigVakje crasht niet
    @Test void testEvacueerZonderHuidigVakje() {
        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        assertDoesNotThrow(() -> g.evacueer(hotel.layout.krijgVakje(3, 1), pf));
    }

    // evacueer: persoon zonder pathfinder crasht niet
    @Test void testEvacueerZonderPathfinder() {
        Gast g = maakGast(1, 3, 1);
        assertDoesNotThrow(() -> g.evacueer(hotel.layout.krijgVakje(5, 1), null));
    }

    // setPathfinder: pathfinder wordt correct bewaard
    @Test void testSetPathfinder() {
        Gast g = new Gast(1, 1);
        g.setPathfinder(pf);
        assertEquals(pf, g.getPathfinder());
    }

    // isGast: Gast geeft true
    @Test void testIsGast() {
        assertTrue(new Gast(1, 1).isGast());
    }

    // isSchoonmaker: Schoonmaker geeft true
    @Test void testIsSchoonmaker() {
        assertTrue(new Schoonmaker().isSchoonmaker());
    }

    // isGast: Schoonmaker geeft false
    @Test void testSchoonmakerIsGeenGast() {
        assertFalse(new Schoonmaker().isGast());
    }

    // beweeg: gast in lift beweegt niet zelfstandig
    @Test void testGastInLiftBeweegNiet() {
        Gast g = maakGast(1, 2, 1);
        g.inLift = true;
        g.zetDoel(hotel.layout.krijgVakje(5, 1));
        g.beweeg();
        assertEquals(2, g.huidigVakje.x);
    }

    // gast wacht op lift: beweeg stopt
    @Test void testGastWachtOpLiftBeweegNiet() {
        Gast g = maakGast(1, 2, 1);
        g.wachtOpLift = true;
        g.zetDoel(hotel.layout.krijgVakje(5, 1));
        g.beweeg();
        assertEquals(2, g.huidigVakje.x);
    }

    // hulpmethode
    private Gast maakGast(int id, int x, int y) {
        Gast g = new Gast(id, 1);
        g.setPathfinder(pf);
        g.zetStartPositie(hotel.layout.krijgVakje(x, y));
        return g;
    }
}
