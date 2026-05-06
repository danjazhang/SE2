import Model.ruimte.Bioscoop;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor de Bioscoop: ik test of cinema-events de filmstatus juist aanpassen.
public class BioscoopTest {

    // Ik maak een nieuwe Bioscoop; ik verwacht dat er geen film bezig is en dat de gastenlijst leeg is.
    @Test void testConstructor() {
        Bioscoop b = new Bioscoop();
        assertFalse(b.filmBezig);
        assertEquals(0, b.filmDuur);
        assertTrue(b.gasten.isEmpty());
    }

    // Ik stuur een START_CINEMA-event; ik verwacht dat filmBezig true wordt.
    @Test void testStartCinemaZetFilmBezig() {
        Bioscoop b = new Bioscoop();
        b.onEvent(new HotelEvent(10, HotelEventType.START_CINEMA, -1, -1));
        assertTrue(b.filmBezig);
    }

    // Ik start een film en stuur later een NONE-tick op de eindtijd; ik verwacht dat de film stopt.
    @Test void testFilmEindigtNaFilmduur() {
        Bioscoop b = new Bioscoop();
        b.onEvent(new HotelEvent(10, HotelEventType.START_CINEMA, -1, -1));
        b.onEvent(new HotelEvent(50, HotelEventType.NONE, -1, -1));
        assertFalse(b.filmBezig);
    }

    // Ik start een film en stuur een vroege NONE-tick; ik verwacht dat de film nog bezig blijft.
    @Test void testFilmNogBezig() {
        Bioscoop b = new Bioscoop();
        b.onEvent(new HotelEvent(10, HotelEventType.START_CINEMA, -1, -1));
        b.onEvent(new HotelEvent(20, HotelEventType.NONE, -1, -1));
        assertTrue(b.filmBezig);
    }

    // Ik stuur een GOTO_CINEMA-event; ik verwacht dat dit geen exception geeft.
    @Test void testGotoCinemaCrashetNiet() {
        Bioscoop b = new Bioscoop();
        assertDoesNotThrow(() -> b.onEvent(new HotelEvent(1, HotelEventType.GOTO_CINEMA, 1, -1)));
    }
}
