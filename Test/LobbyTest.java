import Model.*;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class LobbyTest {

    // hulpklasse om logs op te vangen in de test
    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override
        public void log(String bericht) { logs.add(bericht); }
    }

    // hulpmethode om een HotelEvent aan te maken
    static HotelEvent maakEvent(HotelEventType type, int tijd, int gastId) {
        return new HotelEvent(tijd, type, gastId, -1);
    }

    // balieX en balieY worden correct opgeslagen via de constructor
    @Test
    void testConstructor() {
        TestLogger logger = new TestLogger();
        Lobby lobby = new Lobby(1, 1, 2, 2, 3, 4, logger);
        assertEquals(3, lobby.getBalieX());
        assertEquals(4, lobby.getBalieY());
    }

    // lobby erft van Ruimte, posX en posY worden correct opgeslagen
    @Test
    void testErftVanRuimte() {
        TestLogger logger = new TestLogger();
        Lobby lobby = new Lobby(2, 3, 2, 2, 1, 1, logger);
        assertEquals(2, lobby.posX);
        assertEquals(3, lobby.posY);
    }

    // breedte en hoogte worden correct opgeslagen
    @Test
    void testAfmetingen() {
        TestLogger logger = new TestLogger();
        Lobby lobby = new Lobby(1, 1, 4, 5, 1, 1, logger);
        assertEquals(4, lobby.breedte);
        assertEquals(5, lobby.hoogte);
    }

    // lobby logt check-in event correct
    @Test
    void testCheckInWordtGelogd() {
        TestLogger logger = new TestLogger();
        Lobby lobby = new Lobby(0, 0, 5, 5, 1, 1, logger);
        lobby.onEvent(maakEvent(HotelEventType.CHECK_IN, 10, 3));
        assertEquals(1, logger.logs.size());
        assertTrue(logger.logs.get(0).contains("checkt in"));
        assertTrue(logger.logs.get(0).contains("gast 3"));
    }

    // lobby logt check-out event correct
    @Test
    void testCheckOutWordtGelogd() {
        TestLogger logger = new TestLogger();
        Lobby lobby = new Lobby(0, 0, 5, 5, 1, 1, logger);
        lobby.onEvent(maakEvent(HotelEventType.CHECK_OUT, 20, 5));
        assertEquals(1, logger.logs.size());
        assertTrue(logger.logs.get(0).contains("checkt uit"));
        assertTrue(logger.logs.get(0).contains("gast 5"));
    }

    // lobby negeert andere events en logt niets
    @Test
    void testAndereEventsWordenGenegeerd() {
        TestLogger logger = new TestLogger();
        Lobby lobby = new Lobby(0, 0, 5, 5, 1, 1, logger);
        lobby.onEvent(maakEvent(HotelEventType.NEED_FOOD, 30, 1));
        assertTrue(logger.logs.isEmpty());
    }

    // lobby met null logger crasht niet bij check-in
    @Test
    void testNullLoggerCrashetNietBijCheckIn() {
        Lobby lobby = new Lobby(0, 0, 5, 5, 1, 1, null);
        assertDoesNotThrow(() -> lobby.onEvent(maakEvent(HotelEventType.CHECK_IN, 10, 1)));
    }

    // lobby met null logger crasht niet bij check-out
    @Test
    void testNullLoggerCrashetNietBijCheckOut() {
        Lobby lobby = new Lobby(0, 0, 5, 5, 1, 1, null);
        assertDoesNotThrow(() -> lobby.onEvent(maakEvent(HotelEventType.CHECK_OUT, 10, 1)));
    }

    // toonStatusScherm mag niet crashen
    @Test
    void testToonStatusSchermCrashetNiet() {
        TestLogger logger = new TestLogger();
        Lobby lobby = new Lobby(1, 1, 2, 2, 1, 1, logger);
        assertDoesNotThrow(() -> lobby.toonStatusScherm());
    }

    // check-in log bevat het tijdstip
    @Test
    void testCheckInLogBevatTijdstip() {
        TestLogger logger = new TestLogger();
        Lobby lobby = new Lobby(0, 0, 5, 5, 1, 1, logger);
        lobby.onEvent(maakEvent(HotelEventType.CHECK_IN, 42, 7));
        assertTrue(logger.logs.get(0).contains("42"));
    }

    // check-out log bevat het tijdstip
    @Test
    void testCheckOutLogBevatTijdstip() {
        TestLogger logger = new TestLogger();
        Lobby lobby = new Lobby(0, 0, 5, 5, 1, 1, logger);
        lobby.onEvent(maakEvent(HotelEventType.CHECK_OUT, 99, 2));
        assertTrue(logger.logs.get(0).contains("99"));
    }
}
