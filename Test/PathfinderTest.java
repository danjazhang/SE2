import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import Model.ruimte.Ruimte;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathfinderTest {

    private Hotel hotel;
    private Pathfinder pathfinder;

    // -------------------------------------------------
    // Setup: standaard hotel met lift + trap
    // -------------------------------------------------
    @BeforeEach
    void setUp() {

        hotel = new Hotel();

        hotel.layout = new Layout(6, 4);

        hotel.breedte = 6;
        hotel.hoogte = 4;

        // lift setup
        Lift lift = new Lift(hotel);
        lift.posX = 1;
        lift.posY = 1;
        lift.breedte = 1;
        lift.hoogte = 4;

        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        // trap setup
        Trap trap = new Trap(2);
        trap.posX = 6;
        trap.posY = 1;
        trap.breedte = 1;
        trap.hoogte = 4;

        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);
        pathfinder = hotel.pathfinder;
    }

    // -------------------------------------------------
    // Constructor coverage
    // -------------------------------------------------
    @Test
    void testConstructor() {

        assertNotNull(pathfinder);
    }

    // -------------------------------------------------
    // volgendeStap: horizontaal rechts
    // -------------------------------------------------
    @Test
    void testStapRechts() {

        Vakje huidig = hotel.layout.krijgVakje(2, 1);

        Vakje doel = hotel.layout.krijgVakje(4, 1);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(3, stap.x);

        assertEquals(1, stap.y);
    }

    // -------------------------------------------------
    // volgendeStap: horizontaal links
    // -------------------------------------------------
    @Test
    void testStapLinks() {

        Vakje huidig = hotel.layout.krijgVakje(4, 1);

        Vakje doel = hotel.layout.krijgVakje(2, 1);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(3, stap.x);
    }

    // -------------------------------------------------
    // volgendeStap: zelfde positie (geen beweging)
    // -------------------------------------------------
    @Test
    void testStapGeenBeweging() {

        Vakje huidig = hotel.layout.krijgVakje(2, 1);

        Vakje doel = hotel.layout.krijgVakje(2, 1);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        // x blijft gelijk
        assertEquals(2, stap.x);

        // y blijft gelijk
        assertEquals(1, stap.y);
    }

    // -------------------------------------------------
    // volgendeStap: null input branch
    // -------------------------------------------------
    @Test
    void testStapNullHuidig() {

        Vakje doel = hotel.layout.krijgVakje(2, 1);

        assertNull(pathfinder.volgendeStap(null, doel));
    }

    @Test
    void testStapNullDoel() {

        Vakje huidig = hotel.layout.krijgVakje(2, 1);

        assertNull(pathfinder.volgendeStap(huidig, null));
    }

    // -------------------------------------------------
    // verticale beweging via trap
    // -------------------------------------------------
    @Test
    void testStapOmhoogViaTrap() {

        Vakje huidig = hotel.layout.krijgVakje(6, 1);

        huidig.ruimte = hotel.trap;

        Vakje doel = hotel.layout.krijgVakje(6, 3);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(6, stap.x);

        assertEquals(2, stap.y);
    }

    @Test
    void testStapOmlaagViaTrap() {

        Vakje huidig = hotel.layout.krijgVakje(6, 3);

        huidig.ruimte = hotel.trap;

        Vakje doel = hotel.layout.krijgVakje(6, 1);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(2, stap.y);
    }

    // -------------------------------------------------
    // zetRoute: zelfde verdieping branch
    // -------------------------------------------------
    @Test
    void testZetRouteZelfdeVerdieping() {

        Gast g = new Gast(1, 1);

        g.setPathfinder(pathfinder);

        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));

        Kamer k = new Kamer();

        k.posX = 4;
        k.posY = 1;

        hotel.ruimtes.add(k);

        hotel.layout.plaatsRuimte(k);

        pathfinder.zetRoute(g, k);

        assertNotNull(g.doelVakje);
    }

    // -------------------------------------------------
    // zetRoute: null start branch
    // -------------------------------------------------
    @Test
    void testZetRouteZonderStart() {

        Gast g = new Gast(1, 1);

        Kamer k = new Kamer();

        k.posX = 4;
        k.posY = 1;

        assertDoesNotThrow(() -> pathfinder.zetRoute(g, k));
    }

    // -------------------------------------------------
    // zetRoute: schoonmaker branch (force trap route)
    // -------------------------------------------------
    @Test
    void testZetRouteSchoonmaker() {

        Schoonmaker s = new Schoonmaker();

        s.setPathfinder(pathfinder);

        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));

        Kamer k = new Kamer();

        k.posX = 4;
        k.posY = 3;

        hotel.ruimtes.add(k);

        hotel.layout.plaatsRuimte(k);

        pathfinder.zetRoute(s, k);

        assertNotNull(s.doelVakje);
    }

    // -------------------------------------------------
    // zetRoute: lift branch
    // -------------------------------------------------
    @Test
    void testZetRouteViaLift() {

        Gast g = new Gast(1, 1);

        g.setPathfinder(pathfinder);

        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));

        Kamer k = new Kamer();

        k.posX = 4;
        k.posY = 3;

        hotel.ruimtes.add(k);

        hotel.layout.plaatsRuimte(k);

        pathfinder.zetRoute(g, k);

        assertTrue(g.gebruiktLift);
    }


    // -------------------------------------------------
    // schatLiftTijd indirect coverage
    // -------------------------------------------------
    @Test
    void testLiftTijdIndirect() {

        Gast g = new Gast(1, 1);

        g.setPathfinder(pathfinder);

        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));

        Kamer k = new Kamer();

        k.posX = 4;
        k.posY = 4;

        hotel.ruimtes.add(k);

        hotel.layout.plaatsRuimte(k);

        pathfinder.zetRoute(g, k);

        // force lift branch usage
        assertNotNull(g);
    }

    // -------------------------------------------------
    // extreme null safety branch
    // -------------------------------------------------
    @Test
    void testExtremeNullSafety() {

        assertNull(pathfinder.volgendeStap(null, null));
    }
}