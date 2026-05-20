import Model.*;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Restaurant;
import Model.ruimte.Trap;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantExtraTest {

    // hulpklasse om logs op te vangen
    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    // registreerGast: gast wordt geregistreerd en gelogd
    @Test void testRegistreerGastLogt() {
        TestLogger logger = new TestLogger();
        Restaurant r = new Restaurant(logger);
        r.registreerGast(1, 10);
        assertTrue(logger.logs.get(0).contains("gaat naar restaurant"));
    }

    // registreerGast: dubbele registratie wordt genegeerd
    @Test void testRegistreerGastDubbel() {
        TestLogger logger = new TestLogger();
        Restaurant r = new Restaurant(logger);
        r.registreerGast(1, 10);
        r.registreerGast(1, 20);
        // slechts 1 log want tweede registratie wordt genegeerd
        assertEquals(1, logger.logs.size());
    }

    // onEvent NONE: gast is klaar na eetduur van 20 ticks
    @Test void testGastKlaarNaEetduur() {
        TestLogger logger = new TestLogger();
        Restaurant r = new Restaurant(logger);
        r.registreerGast(1, 1);
        r.onEvent(new HotelEvent(21, HotelEventType.NONE, -1, -1));
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // onEvent NONE: gast is nog niet klaar voor eetduur voorbij is
    @Test void testGastNogNietKlaar() {
        TestLogger logger = new TestLogger();
        Restaurant r = new Restaurant(logger);
        r.registreerGast(1, 1);
        r.onEvent(new HotelEvent(10, HotelEventType.NONE, -1, -1));
        assertFalse(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // onEvent NONE: gastTerugService wordt aangeroepen als gast klaar is
    @Test void testGastTerugServiceWordtAangeroepen() {
        Hotel hotel = new Hotel();
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
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        hotel.pathfinder = new Pathfinder(hotel);
        Gast gast = new Gast(1, 2);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        kamer.koppelGast(gast);
        hotel.voegPersoonToe(gast);
        Restaurant r = new Restaurant();
        r.setGastTerugService(new GastRoutingService(hotel));
        r.registreerGast(1, 1);
        r.onEvent(new HotelEvent(21, HotelEventType.NONE, -1, -1));
        // gast heeft nu een route terug naar kamer
        assertNotNull(gast.doelVakje);
    }

    // betreedRestaurant: geen crash
    @Test void testBetreedRestaurantCrashetNiet() {
        assertDoesNotThrow(() -> new Restaurant().betreedRestaurant());
    }

    // verlaatRestaurant: geen crash
    @Test void testVerlaatRestaurantCrashetNiet() {
        assertDoesNotThrow(() -> new Restaurant().verlaatRestaurant());
    }

    // isVol: geen crash
    @Test void testIsVolCrashetNiet() {
        assertDoesNotThrow(() -> new Restaurant().isVol());
    }
}
