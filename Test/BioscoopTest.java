import Model.*;
import Model.ruimte.Bioscoop;
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

    // hulpmethode om een HotelEvent aan te maken
    static HotelEvent maakEvent(HotelEventType type, int tijd, int gastId) {
        return new HotelEvent(tijd, type, gastId, -1);
    }

    // Ik maak een nieuwe Bioscoop aan; ik verwacht dat de film niet bezig is, de duur 0 is en de gastenlijst leeg is
    @Test
    void testConstructor() {
        Bioscoop b = new Bioscoop();
        assertFalse(b.filmBezig);
        assertEquals(0, b.filmDuur);
        assertNotNull(b.gasten);
        assertTrue(b.gasten.isEmpty());
    }

    // Ik maak een Bioscoop aan; ik verwacht dat posX en posY 0 zijn omdat Bioscoop van Ruimte erft
    @Test
    void testErftVanRuimte() {
        Bioscoop b = new Bioscoop();
        assertEquals(0, b.posX);
        assertEquals(0, b.posY);
    }

    // Ik stuur een GOTO_CINEMA event naar de bioscoop; ik verwacht dat het gastId in de log staat
    @Test
    void testGotoCinemaWordtGelogd() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.GOTO_CINEMA, 100, 12));
        assertEquals(1, logger.logs.size());
        assertTrue(logger.logs.get(0).contains("gast 12 komt binnen"));
    }

    // Ik stuur een START_CINEMA event; ik verwacht dat filmBezig true wordt en de log "film start" bevat
    @Test
    void testStartCinemaZetFilmBezig() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        assertTrue(b.filmBezig);
        assertTrue(logger.logs.get(0).contains("film start"));
    }

    // Ik start een film op tick 100 en stuur NONE op tick 140; ik verwacht dat de film eindigt na 40 ticks
    @Test
    void testFilmEindigt() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        b.onEvent(maakEvent(HotelEventType.NONE, 140, 0));
        assertFalse(b.filmBezig);
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("film eindigt")));
    }

    // Ik start een film op tick 100 en stuur NONE op tick 130; ik verwacht dat de film nog bezig is want 40 ticks zijn nog niet voorbij
    @Test
    void testFilmEindigtNietVroeg() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        b.onEvent(maakEvent(HotelEventType.NONE, 130, 0));
        assertTrue(b.filmBezig);
    }

    // Ik stuur een NONE event zonder dat er een film gestart is; ik verwacht geen log en filmBezig blijft false
    @Test
    void testNoneZonderFilmDoetNiets() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.NONE, 50, 0));
        assertTrue(logger.logs.isEmpty());
        assertFalse(b.filmBezig);
    }

    // Ik stuur een CHECK_IN event naar de bioscoop; ik verwacht dat er niets gelogd wordt want de bioscoop reageert daar niet op
    @Test
    void testAndereEventsWordenGenegeerd() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.CHECK_IN, 10, 1));
        assertTrue(logger.logs.isEmpty());
    }

    // Ik maak een Bioscoop zonder logger en stuur GOTO_CINEMA; ik verwacht geen crash
    @Test
    void testNullLoggerCrashetNietBijGotoCinema() {
        Bioscoop b = new Bioscoop();
        assertDoesNotThrow(() -> b.onEvent(maakEvent(HotelEventType.GOTO_CINEMA, 10, 1)));
    }

    // Ik maak een Bioscoop zonder logger en stuur START_CINEMA; ik verwacht geen crash
    @Test
    void testNullLoggerCrashetNietBijStartCinema() {
        Bioscoop b = new Bioscoop();
        assertDoesNotThrow(() -> b.onEvent(maakEvent(HotelEventType.START_CINEMA, 10, -1)));
    }

    // Ik start een film zonder logger en laat hem eindigen; ik verwacht geen crash
    @Test
    void testNullLoggerCrashetNietBijFilmEinde() {
        Bioscoop b = new Bioscoop();
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        assertDoesNotThrow(() -> b.onEvent(maakEvent(HotelEventType.NONE, 140, 0)));
    }

    // Ik roep startFilm aan; ik verwacht geen crash
    @Test
    void testStartFilmCrashetNiet() {
        Bioscoop b = new Bioscoop();
        assertDoesNotThrow(() -> b.startFilm());
    }

    // Ik roep stopFilm aan; ik verwacht geen crash
    @Test
    void testStopFilmCrashetNiet() {
        Bioscoop b = new Bioscoop();
        assertDoesNotThrow(() -> b.stopFilm());
    }

    // Ik roep betreedBioscoop aan; ik verwacht geen crash
    @Test
    void testBetreedBioscoopCrashetNiet() {
        Bioscoop b = new Bioscoop();
        assertDoesNotThrow(() -> b.betreedBioscoop());
    }

    // Ik stuur GOTO_CINEMA op tijdstip 55; ik verwacht dat het tijdstip in de log staat
    @Test
    void testGotoCinemaLogBevatTijdstip() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.GOTO_CINEMA, 55, 3));
        assertTrue(logger.logs.get(0).contains("55"));
    }
}