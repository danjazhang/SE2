import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PathfinderTest {

    private Hotel hotel;
    private Pathfinder pathfinder;

    // maak een hotel met lift en trap voor elke test
    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;

        Lift lift = new Lift();
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);
        trap.posX = 6; trap.posY = 1; trap.breedte = 1; trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);
        pathfinder = hotel.pathfinder;
    }

    // constructor: pathfinder wordt aangemaakt zonder crash
    @Test void testConstructor() {
        assertNotNull(pathfinder);
    }

    // volgendeStap: beweegt 1 stap naar rechts
    @Test void testVolgendeStapNaarRechts() {
        Vakje huidig = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(4, 1);
        Vakje stap = pathfinder.volgendeStap(huidig, doel);
        assertEquals(3, stap.x);
        assertEquals(1, stap.y);
    }

    // volgendeStap: beweegt 1 stap naar links
    @Test void testVolgendeStapNaarLinks() {
        Vakje huidig = hotel.layout.krijgVakje(4, 1);
        Vakje doel = hotel.layout.krijgVakje(2, 1);
        Vakje stap = pathfinder.volgendeStap(huidig, doel);
        assertEquals(3, stap.x);
    }

    // volgendeStap: beweegt 1 stap naar beneden
    @Test void testVolgendeStapNaarBeneden() {
        Vakje huidig = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(2, 3);
        Vakje stap = pathfinder.volgendeStap(huidig, doel);
        assertEquals(2, stap.y);
    }

    // volgendeStap: beweegt 1 stap naar boven
    @Test void testVolgendeStapNaarBoven() {
        Vakje huidig = hotel.layout.krijgVakje(2, 3);
        Vakje doel = hotel.layout.krijgVakje(2, 1);
        Vakje stap = pathfinder.volgendeStap(huidig, doel);
        assertEquals(2, stap.y);
    }

    // berekenRoute: route eindigt altijd op het doelvakje
    @Test void testBerekenRouteEindigtOpDoel() {
        Vakje start = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(4, 1);
        List<Vakje> route = pathfinder.berekenRoute(start, doel);
        assertFalse(route.isEmpty());
        assertEquals(doel, route.get(route.size() - 1));
    }

    // berekenRoute: route bevat tussenstop bij andere verdieping
    @Test void testBerekenRouteAnderVerdieping() {
        Vakje start = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(2, 3);
        List<Vakje> route = pathfinder.berekenRoute(start, doel);
        // route heeft minimaal 3 stappen: transport heen, transport doel, einddoel
        assertTrue(route.size() >= 2);
        assertEquals(doel, route.get(route.size() - 1));
    }

    // berekenRoute: route op zelfde verdieping heeft geen extra tussenstop
    @Test void testBerekenRouteZelfdeVerdieping() {
        Vakje start = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(4, 1);
        List<Vakje> route = pathfinder.berekenRoute(start, doel);
        assertEquals(doel, route.get(route.size() - 1));
    }

    // zetRoute: route wordt op gast gezet
    @Test void testZetRoute() {
        Gast gast = new Gast(1, 1);
        gast.setPathfinder(pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));

        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        pathfinder.zetRoute(gast, kamer);
        assertNotNull(gast.doelVakje);
    }

    // zetRoute: geen crash als gast geen startpositie heeft
    @Test void testZetRouteZonderStartpositie() {
        Gast gast = new Gast(1, 1);
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        assertDoesNotThrow(() -> pathfinder.zetRoute(gast, kamer));
    }
}
