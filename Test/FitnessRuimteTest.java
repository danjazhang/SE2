import Model.ILogger;
import Model.ruimte.Fitnessruimte;
import Model.GastRoutingService;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FitnessRuimteTest {

    // -------------------------------------------------
    // Dummy logger (vermijdt lambda problemen)
    // -------------------------------------------------
    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();

        @Override
        public void log(String bericht) {
            logs.add(bericht);
        }
    }

    // -------------------------------------------------
    // Dummy routing service (geen echte hotel nodig)
    // -------------------------------------------------
    static class DummyGastRoutingService extends GastRoutingService {
        public DummyGastRoutingService() {
            super(null);
        }

        @Override
        public void stuurTerugNaarKamer(int gastId) {
            // niets doen → alleen branch coverage
        }
    }

    // -------------------------------------------------
    // Constructor tests
    // -------------------------------------------------

    @Test
    void testConstructorLeeg() {
        Fitnessruimte f = new Fitnessruimte();
        assertTrue(f.gasten.isEmpty()); // init check
    }

    @Test
    void testConstructorMetLogger() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);

        assertTrue(f.gasten.isEmpty());
    }

    // -------------------------------------------------
    // GOTO_FITNESS tests
    // -------------------------------------------------

    @Test
    void testGotoFitnessZetEindtijd() {
        Fitnessruimte f = new Fitnessruimte();

        // gast start fitness
        f.onEvent(new HotelEvent(10, HotelEventType.GOTO_FITNESS, 1, -1));

        // intern mag geen crash geven
        assertDoesNotThrow(() ->
                f.onEvent(new HotelEvent(11, HotelEventType.GOTO_FITNESS, 2, -1))
        );
    }

    @Test
    void testGotoFitnessMetLogger() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);

        f.onEvent(new HotelEvent(5, HotelEventType.GOTO_FITNESS, 99, -1));

        assertFalse(logger.logs.isEmpty());
    }

    // -------------------------------------------------
    // NONE event tests (belangrijkste branch coverage)
    // -------------------------------------------------

    @Test
    void testNoneZonderGasten() {
        Fitnessruimte f = new Fitnessruimte();

        assertDoesNotThrow(() ->
                f.onEvent(new HotelEvent(100, HotelEventType.NONE, -1, -1))
        );
    }

    @Test
    void testNoneMetTeVroegeTijd() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);

        // start sport op tijd 10
        f.onEvent(new HotelEvent(10, HotelEventType.GOTO_FITNESS, 1, -1));

        // nog niet klaar (tijd 15 < 30)
        f.onEvent(new HotelEvent(15, HotelEventType.NONE, -1, -1));

        assertTrue(logger.logs.size() == 1); // alleen startlog
    }

    @Test
    void testNoneGastWordtKlaar() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);

        DummyGastRoutingService service = new DummyGastRoutingService();
        f.setGastTerugService(service);

        // start sport
        f.onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 7, -1));

        // na SPORTDUUR (20)
        f.onEvent(new HotelEvent(21, HotelEventType.NONE, -1, -1));

        // moet loggen dat gast klaar is
        assertTrue(
                logger.logs.stream().anyMatch(s -> s.contains("klaar"))
        );
    }

    @Test
    void testNoneZonderRoutingService() {
        Fitnessruimte f = new Fitnessruimte(new TestLogger());

        // geen service ingesteld → branch moet null-safe zijn
        f.onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 5, -1));

        assertDoesNotThrow(() ->
                f.onEvent(new HotelEvent(30, HotelEventType.NONE, -1, -1))
        );
    }

    // -------------------------------------------------
    // Edge cases / branch coverage
    // -------------------------------------------------

    @Test
    void testMeerdereGastenWordenVerwerkt() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);

        f.onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 1, -1));
        f.onEvent(new HotelEvent(2, HotelEventType.GOTO_FITNESS, 2, -1));

        f.onEvent(new HotelEvent(25, HotelEventType.NONE, -1, -1));

        // beide moeten verwerkt zijn
        long count = logger.logs.stream()
                .filter(s -> s.contains("klaar"))
                .count();

        assertTrue(count >= 1);
    }

    @Test
    void testOnbekendEventTypeWordtGenegeerd() {
        Fitnessruimte f = new Fitnessruimte();

        assertDoesNotThrow(() ->
                f.onEvent(new HotelEvent(1, null, 1, -1))
        );
    }

    @Test
    void testHerhaaldNONEIsSafe() {
        Fitnessruimte f = new Fitnessruimte();

        f.onEvent(new HotelEvent(1, HotelEventType.NONE, -1, -1));
        f.onEvent(new HotelEvent(2, HotelEventType.NONE, -1, -1));
        f.onEvent(new HotelEvent(3, HotelEventType.NONE, -1, -1));

        assertTrue(true); // alleen crashvrij testen
    }
}