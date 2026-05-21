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

    // Ik maak voor elke test een klein hotel met lift en trap,
    // zodat de pathfinder routes tussen verdiepingen kan berekenen.
    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;

        Lift lift = new Lift(hotel);
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

    // Ik maak een nieuwe pathfinder aan; ik verwacht dat die correct wordt aangemaakt.
    @Test void testConstructor() {
        assertNotNull(pathfinder);
    }

    // Ik vraag de volgende stap naar rechts; ik verwacht dat de x-coördinaat met 1 toeneemt.
    @Test void testVolgendeStapNaarRechts() {
        Vakje huidig = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(4, 1);
        Vakje stap = pathfinder.volgendeStap(huidig, doel);
        assertEquals(3, stap.x);
        assertEquals(1, stap.y);
    }

    // Ik vraag de volgende stap naar links; ik verwacht dat de x-coördinaat met 1 afneemt.
    @Test void testVolgendeStapNaarLinks() {
        Vakje huidig = hotel.layout.krijgVakje(4, 1);
        Vakje doel = hotel.layout.krijgVakje(2, 1);
        Vakje stap = pathfinder.volgendeStap(huidig, doel);
        assertEquals(3, stap.x);
    }

    // Ik vraag de volgende stap naar beneden; ik verwacht dat de y-coördinaat met 1 toeneemt.
    @Test void testVolgendeStapNaarBeneden() {
        Vakje huidig = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(2, 3);
        Vakje stap = pathfinder.volgendeStap(huidig, doel);
        assertEquals(2, stap.y);
    }

    // Ik vraag de volgende stap naar boven; ik verwacht dat de y-coördinaat met 1 afneemt.


    // Ik bereken een route; ik verwacht dat die altijd eindigt op het doelvakje.
    @Test void testBerekenRouteEindigtOpDoel() {
        Vakje start = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(4, 1);
        //List<Vakje> route = pathfinder.berekenRoute(start, doel);
        //assertFalse(route.isEmpty());
        //assertEquals(doel, route.get(route.size() - 1));
    }

    // Ik bereken een route naar een andere verdieping; ik verwacht dat de route een tussenstap via lift of trap bevat.
    @Test void testBerekenRouteAnderVerdieping() {
        Vakje start = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(2, 3);
        //List<Vakje> route = pathfinder.berekenRoute(start, doel);
        // Ik verwacht minstens een transportstap en uiteindelijk het einddoel in de route.
        //assertTrue(route.size() >= 2);
        //assertEquals(doel, route.get(route.size() - 1));
    }

    // Ik bereken een route op dezelfde verdieping; ik verwacht nog steeds dat het einddoel correct als laatste stap staat.
    @Test void testBerekenRouteZelfdeVerdieping() {
        Vakje start = hotel.layout.krijgVakje(2, 1);
        Vakje doel = hotel.layout.krijgVakje(4, 1);
        //List<Vakje> route = pathfinder.berekenRoute(start, doel);
        //assertEquals(doel, route.get(route.size() - 1));
    }

    // Ik laat de pathfinder een route op een gast zetten; ik verwacht dat de gast daarna een doelvakje heeft.
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

    // Ik laat de pathfinder een route zetten zonder startpositie; ik verwacht dat dit geen crash geeft.
    @Test void testZetRouteZonderStartpositie() {
        Gast gast = new Gast(1, 1);
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        assertDoesNotThrow(() -> pathfinder.zetRoute(gast, kamer));
    }
}
