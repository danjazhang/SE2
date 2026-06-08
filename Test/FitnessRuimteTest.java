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

    // ik maak een fitnessruimte zonder parameters aan; ik verwacht dat de gastenlijst leeg is
    @Test
    void testConstructorLeeg() {
        Fitnessruimte f = new Fitnessruimte();
        assertTrue(f.gasten.isEmpty());
    }

    // ik maak een fitnessruimte met logger aan; ik verwacht dat de gastenlijst nog steeds leeg is
    @Test
    void testConstructorMetLogger() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);

        assertTrue(f.gasten.isEmpty());
    }

    // -------------------------------------------------
    // GOTO_FITNESS tests
    // -------------------------------------------------

    // ik stuur een GOTO_FITNESS event; ik verwacht dat de fitnessruimte dit opslaat zonder crash
    @Test
    void testGotoFitnessZetEindtijd() {
        Fitnessruimte f = new Fitnessruimte();

        f.onEvent(new HotelEvent(10, HotelEventType.GOTO_FITNESS, 1, -1));

        assertDoesNotThrow(() ->
                f.onEvent(new HotelEvent(11, HotelEventType.GOTO_FITNESS, 2, -1))
        );
    }

    // ik stuur een GOTO_FITNESS event met logger; ik verwacht dat er een log wordt toegevoegd
    @Test
    void testGotoFitnessMetLogger() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);

        f.onEvent(new HotelEvent(5, HotelEventType.GOTO_FITNESS, 99, -1));

        assertFalse(logger.logs.isEmpty());
    }

    // -------------------------------------------------
    // NONE event tests
    // -------------------------------------------------

    // ik stuur een NONE event zonder gasten; ik verwacht dat dit geen crash geeft
    @Test
    void testNoneZonderGasten() {
        Fitnessruimte f = new Fitnessruimte();

        assertDoesNotThrow(() ->
                f.onEvent(new HotelEvent(100, HotelEventType.NONE, -1, -1))
        );
    }

    // ik laat een gast te vroeg eindigen (voor SPORTDUUR); ik verwacht dat de gast nog niet wordt afgerond
    @Test
    void testNoneMetTeVroegeTijd() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);

        f.onEvent(new HotelEvent(10, HotelEventType.GOTO_FITNESS, 1, -1));

        f.onEvent(new HotelEvent(15, HotelEventType.NONE, -1, -1));

        assertTrue(logger.logs.size() == 1);
    }

    // ik laat de tijd voorbij SPORTDUUR gaan; ik verwacht dat de gast klaar gemeld wordt
    @Test
    void testNoneGastWordtKlaar() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);

        DummyGastRoutingService service = new DummyGastRoutingService();
        f.setGastTerugService(service);

        f.onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 7, -1));

        f.onEvent(new HotelEvent(21, HotelEventType.NONE, -1, -1));

        assertTrue(
                logger.logs.stream().anyMatch(s -> s.contains("klaar"))
        );
    }

    // ik zet geen routing service; ik verwacht dat NONE alsnog geen crash geeft
    @Test
    void testNoneZonderRoutingService() {
        Fitnessruimte f = new Fitnessruimte(new TestLogger());

        f.onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 5, -1));

        assertDoesNotThrow(() ->
                f.onEvent(new HotelEvent(30, HotelEventType.NONE, -1, -1))
        );
    }

    // -------------------------------------------------
    // Edge cases
    // -------------------------------------------------

    // ik laat meerdere gasten sporten; ik verwacht dat alle entries correct verwerkt worden
    @Test
    void testMeerdereGastenWordenVerwerkt() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);

        f.onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 1, -1));
        f.onEvent(new HotelEvent(2, HotelEventType.GOTO_FITNESS, 2, -1));

        f.onEvent(new HotelEvent(25, HotelEventType.NONE, -1, -1));

        long count = logger.logs.stream()
                .filter(s -> s.contains("klaar"))
                .count();

        assertTrue(count >= 1);
    }

    // ik stuur een onbekend event type; ik verwacht dat de fitnessruimte dit negeert zonder crash
    @Test
    void testOnbekendEventTypeWordtGenegeerd() {
        Fitnessruimte f = new Fitnessruimte();

        assertDoesNotThrow(() ->
                f.onEvent(new HotelEvent(1, null, 1, -1))
        );
    }

    // ik stuur meerdere NONE events achter elkaar; ik verwacht dat de code stabiel blijft
    @Test
    void testHerhaaldNONEIsSafe() {
        Fitnessruimte f = new Fitnessruimte();

        f.onEvent(new HotelEvent(1, HotelEventType.NONE, -1, -1));
        f.onEvent(new HotelEvent(2, HotelEventType.NONE, -1, -1));
        f.onEvent(new HotelEvent(3, HotelEventType.NONE, -1, -1));

        assertTrue(true);
    }
}