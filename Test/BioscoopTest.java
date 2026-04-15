import Model.*;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class BioscoopTest {

    // hulpklasse om logs op te vangen in de test
    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override
        public void log(String bericht) { logs.add(bericht); }
    }

    // hulpklasse om een nep HotelEvent aan te maken
    static HotelEvent maakEvent(HotelEventType type, int tijd, int gastId) {
        return new HotelEvent(tijd, type, gastId, -1);
    }

    // film is niet bezig, duur is 0 en gastenlijst is leeg na aanmaken
    @Test
    void testConstructor() {
        Bioscoop b = new Bioscoop();
        assertFalse(b.filmBezig);
        assertEquals(0, b.filmDuur);
        assertNotNull(b.gasten);
        assertTrue(b.gasten.isEmpty());
    }

    // bioscoop erft van Ruimte, posX en posY beginnen op 0
    @Test
    void testErftVanRuimte() {
        Bioscoop b = new Bioscoop();
        assertEquals(0, b.posX);
        assertEquals(0, b.posY);
    }

    // filmBezig wordt true na GOTO_CINEMA event
    @Test
    void testGotoCinemaWordtGelogd() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.GOTO_CINEMA, 100, 12));
        assertTrue(logger.logs.get(0).contains("gast 12 komt binnen"));
    }

    // filmBezig wordt true na START_CINEMA event
    @Test
    void testStartCinemaZetFilmBezig() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        assertTrue(b.filmBezig);
        assertTrue(logger.logs.get(0).contains("film start"));
    }

    // film eindigt na FILMDUUR ticks
    @Test
    void testFilmEindigt() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        // stuur NONE events tot de film eindigt (FILMDUUR = 40)
        b.onEvent(maakEvent(HotelEventType.NONE, 140, 0));
        assertFalse(b.filmBezig);
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("film eindigt")));
    }

    // film eindigt niet voor de eindtijd
    @Test
    void testFilmEindigtNietVroeg() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        b.onEvent(maakEvent(HotelEventType.NONE, 130, 0));
        assertTrue(b.filmBezig);
    }
}
