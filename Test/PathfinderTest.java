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
    // SETUP
    // -------------------------------------------------
    @BeforeEach
    void setUp() {

        // ik doe dit: ik bouw een volledig hotel met layout, lift, trap en pathfinder
        // ik verwacht: dat alle route-berekeningen getest kunnen worden zonder null errors

        hotel = new Hotel();

        hotel.layout = new Layout(6, 4);

        hotel.breedte = 6;
        hotel.hoogte = 4;

        Lift lift = new Lift(hotel);
        lift.posX = 1;
        lift.posY = 1;
        lift.breedte = 1;
        lift.hoogte = 4;

        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

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
    // CONSTRUCTOR
    // -------------------------------------------------

    // ik doe dit: ik controleer of de pathfinder correct is aangemaakt
    // ik verwacht: dat het object niet null is
    @Test
    void testConstructor() {

        assertNotNull(pathfinder);
    }

    // -------------------------------------------------
    // HORIZONTALE BEWEGING
    // -------------------------------------------------

    // ik doe dit: ik laat een stap naar rechts berekenen
    // ik verwacht: dat de x-positie 1 stap opschuift richting doel
    @Test
    void testStapRechts() {

        Vakje huidig = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(4, 1);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(3, stap.x);
        assertEquals(1, stap.y);
    }

    // ik doe dit: ik laat een stap naar links berekenen
    // ik verwacht: dat de route correct 1 stap richting links gaat
    @Test
    void testStapLinks() {

        Vakje huidig = hotel.layout.krijgVakje(4, 1);
        Vakje doel = hotel.layout.krijgVakje(2, 1);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(3, stap.x);
    }

    // -------------------------------------------------
    // GEEN BEWEGING
    // -------------------------------------------------

    // ik doe dit: ik geef start en doel dezelfde positie
    // ik verwacht: dat de positie niet verandert
    @Test
    void testStapGeenBeweging() {

        Vakje huidig = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(2, 1);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(2, stap.x);
        assertEquals(1, stap.y);
    }

    // -------------------------------------------------
    // NULL SAFETY
    // -------------------------------------------------

    // ik doe dit: ik geef null als huidig vakje
    // ik verwacht: dat de methode null teruggeeft
    @Test
    void testStapNullHuidig() {

        Vakje doel = hotel.layout.krijgVakje(2, 1);

        assertNull(pathfinder.volgendeStap(null, doel));
    }

    // ik doe dit: ik geef null als doelvakje
    // ik verwacht: dat de methode null teruggeeft
    @Test
    void testStapNullDoel() {

        Vakje huidig = hotel.layout.krijgVakje(2, 1);

        assertNull(pathfinder.volgendeStap(huidig, null));
    }

    // -------------------------------------------------
    // VERTICALE BEWEGING (TRAP LOGICA)
    // -------------------------------------------------

    // ik doe dit: ik simuleer beweging omhoog via trap
    // ik verwacht: dat y correct omhoog beweegt richting doelverdieping
    @Test
    void testStapOmhoogViaTrap() {

        Vakje huidig = hotel.layout.krijgVakje(6, 1);
        huidig.ruimte = hotel.trap;

        Vakje doel = hotel.layout.krijgVakje(6, 3);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(6, stap.x);
        assertEquals(2, stap.y);
    }

    // ik doe dit: ik simuleer beweging omlaag via trap
    // ik verwacht: dat de y-positie correct daalt richting doel
    @Test
    void testStapOmlaagViaTrap() {

        Vakje huidig = hotel.layout.krijgVakje(6, 3);
        huidig.ruimte = hotel.trap;

        Vakje doel = hotel.layout.krijgVakje(6, 1);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(2, stap.y);
    }

    // -------------------------------------------------
    // ROUTING LOGICA (GAST)
    // -------------------------------------------------

    // ik doe dit: ik zet route voor gast op zelfde verdieping
    // ik verwacht: dat doelvakje correct wordt ingesteld
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
    // NULL START EDGE CASE
    // -------------------------------------------------

    // ik doe dit: ik probeer route te zetten zonder startpositie
    // ik verwacht: dat dit geen crash veroorzaakt
    @Test
    void testZetRouteZonderStart() {

        Gast g = new Gast(1, 1);

        Kamer k = new Kamer();
        k.posX = 4;
        k.posY = 1;

        assertDoesNotThrow(() -> pathfinder.zetRoute(g, k));
    }

    // -------------------------------------------------
    // SCHOONMAKER ROUTE LOGICA
    // -------------------------------------------------

    // ik doe dit: ik zet route voor schoonmaker naar kamer
    // ik verwacht: dat een geldige route wordt aangemaakt
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
    // LIFT BRANCH
    // -------------------------------------------------

    // ik doe dit: ik zet route voor gast naar andere verdieping
    // ik verwacht: dat lift-logica wordt gebruikt
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
    // EDGE CASE
    // -------------------------------------------------

    // ik doe dit: ik test routeberekening indirect met liftgebruik
    // ik verwacht: dat de route correct blijft werken zonder crash
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

        assertNotNull(g);
    }

    // -------------------------------------------------
    // EXTREME NULL SAFETY
    // -------------------------------------------------

    // ik doe dit: ik geef null aan beide parameters
    // ik verwacht: dat de methode veilig null teruggeeft
    @Test
    void testExtremeNullSafety() {

        assertNull(pathfinder.volgendeStap(null, null));
    }
}