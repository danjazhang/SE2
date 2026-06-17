import Model.GastRoutingService;
import Model.Hotel;
import Model.ILogger;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.Fitnessruimte;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Fitnessruimte: events, gastbeheer, routing
public class FitnessRuimteTest {

    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    static HotelEvent maakEvent(HotelEventType type, int tijd, int gastId) {
        return new HotelEvent(tijd, type, gastId, -1);
    }

    // constructor: gasten-lijst is leeg
    @Test void testConstructorGastenLeeg() {
        assertTrue(new Fitnessruimte().gasten.isEmpty());
    }

    // onEvent GOTO_FITNESS: logt dat gast gaat sporten
    @Test void testGotoFitnessLogt() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 10, 3));
        assertTrue(logger.logs.get(0).contains("gaat sporten"));
    }

    // onEvent NONE: gast is klaar na SPORTDUUR=20 ticks
    @Test void testGastKlaarNaSportduur() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 5));
        f.onEvent(maakEvent(HotelEventType.NONE, 21, -1));
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // onEvent NONE: gast nog niet klaar voor 20 ticks voorbij
    @Test void testGastNogNietKlaarVoor20Ticks() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 5));
        f.onEvent(maakEvent(HotelEventType.NONE, 15, -1));
        assertFalse(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // onEvent NONE: gastTerugService wordt aangeroepen na klaar
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
        Gast gast = new Gast(1, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        kamer.koppelGast(gast);
        hotel.voegPersoonToe(gast);

        Fitnessruimte f = new Fitnessruimte();
        f.setGastTerugService(new GastRoutingService(hotel));
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 1));
        f.onEvent(maakEvent(HotelEventType.NONE, 21, -1));
        assertNotNull(gast.doelVakje);
    }

    // onEvent ander type: geen effect
    @Test void testAnderEventGeenEffect() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.CHECK_IN, 1, 1));
        assertTrue(logger.logs.isEmpty());
    }

    // setGastTerugService: geen crash
    @Test void testSetGastTerugServiceGeenCrash() {
        assertDoesNotThrow(() -> new Fitnessruimte().setGastTerugService(null));
    }

    // isFaciliteit: true
    @Test void testIsFaciliteit() {
        assertTrue(new Fitnessruimte().isFaciliteit());
    }

    // null logger: geen crash bij GOTO_FITNESS
    @Test void testNullLoggerGeenCrash() {
        Fitnessruimte f = new Fitnessruimte();
        assertDoesNotThrow(() -> f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 1)));
    }

    // null logger bij klaar event: geen crash
    @Test void testNullLoggerKlaarGeenCrash() {
        Fitnessruimte f = new Fitnessruimte();
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 2));
        assertDoesNotThrow(() -> f.onEvent(maakEvent(HotelEventType.NONE, 21, -1)));
    }

    // meerdere gasten: allemaal klaar na sportduur
    @Test void testMeerdereGastenKlaar() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 1));
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 2));
        f.onEvent(maakEvent(HotelEventType.NONE, 21, -1));
        long klaar = logger.logs.stream().filter(l -> l.contains("klaar")).count();
        assertEquals(2, klaar);
    }
}
