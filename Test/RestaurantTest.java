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
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Restaurant: constructor, registreerGast, onEvent, isVol, gastTerugService
public class RestaurantTest {

    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    static HotelEvent noneEvent(int tijd) {
        return new HotelEvent(tijd, HotelEventType.NONE, -1, -1);
    }

    // constructor: capaciteit standaard 0
    @Test void testConstructor() {
        assertEquals(0, new Restaurant().capaciteit);
    }

    // NEED_FOOD event: geen crash
    @Test void testNeedFoodCrashetNiet() {
        assertDoesNotThrow(() -> new Restaurant().onEvent(new HotelEvent(1, HotelEventType.NEED_FOOD, 1, -1)));
    }

    // NONE event zonder gasten: geen crash
    @Test void testNoneCrashetNiet() {
        assertDoesNotThrow(() -> new Restaurant().onEvent(noneEvent(1)));
    }

    // registreerGast: gast wordt gelogd
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
        assertEquals(1, logger.logs.size());
    }

    // gast klaar na eetduur van 20 ticks
    @Test void testGastKlaarNaEetduur() {
        TestLogger logger = new TestLogger();
        Restaurant r = new Restaurant(logger);
        r.registreerGast(1, 1);
        r.onEvent(noneEvent(21));
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // gast nog niet klaar voor eetduur voorbij
    @Test void testGastNogNietKlaar() {
        TestLogger logger = new TestLogger();
        Restaurant r = new Restaurant(logger);
        r.registreerGast(1, 1);
        r.onEvent(noneEvent(10));
        assertFalse(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // gast klaar via boolean flag (zonder logger)
    @Test void testGastKlaarZonderLogger() {
        boolean[] logged = {false};
        Restaurant r = new Restaurant(bericht -> logged[0] = true);
        r.registreerGast(1, 1);
        r.onEvent(noneEvent(21));
        assertTrue(logged[0]);
    }

    // null logger: geen crash
    @Test void testNullLoggerGeenCrash() {
        Restaurant r = new Restaurant();
        r.registreerGast(1, 1);
        assertDoesNotThrow(() -> r.onEvent(noneEvent(21)));
    }

    // isVol: false als capaciteit 0
    @Test void testIsVolCapaciteit0() {
        assertFalse(new Restaurant().isVol());
    }

    // isVol: false als minder aanwezigen dan capaciteit
    @Test void testIsVolNietVol() {
        Restaurant r = new Restaurant();
        r.capaciteit = 5;
        assertFalse(r.isVol());
    }

    // isVol: true als aanwezigen >= capaciteit
    @Test void testIsVolVol() {
        Restaurant r = new Restaurant();
        r.capaciteit = 1;
        Gast g = new Gast(1, 1);
        r.betreed(g);
        assertTrue(r.isVol());
    }

    // isFaciliteit: true
    @Test void testIsFaciliteit() {
        assertTrue(new Restaurant().isFaciliteit());
    }

    // gastTerugService: gast krijgt route terug naar kamer na eetduur
    @Test void testGastTerugServiceWordtAangeroepen() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;
        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        lift.initWachtrijen(4);
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
        r.onEvent(noneEvent(21));
        assertNotNull(gast.doelVakje);
    }

    // meerdere gasten: allemaal klaar na eetduur
    @Test void testMeerdereGastenKlaar() {
        TestLogger logger = new TestLogger();
        Restaurant r = new Restaurant(logger);
        r.registreerGast(1, 1);
        r.registreerGast(2, 1);
        r.onEvent(noneEvent(21));
        long klaar = logger.logs.stream().filter(l -> l.contains("klaar")).count();
        assertEquals(2, klaar);
    }
}
