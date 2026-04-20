import Model.ruimte.Bioscoop;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BioscoopTest {

    @Test void testConstructor() {
        Bioscoop b = new Bioscoop();
        assertFalse(b.filmBezig);
        assertEquals(0, b.filmDuur);
        assertTrue(b.gasten.isEmpty());
    }

    @Test void testStartCinemaZetFilmBezig() {
        Bioscoop b = new Bioscoop();
        b.onEvent(new HotelEvent(10, HotelEventType.START_CINEMA, -1, -1));
        assertTrue(b.filmBezig);
    }

    @Test void testFilmEindigtNaFilmduur() {
        Bioscoop b = new Bioscoop();
        b.onEvent(new HotelEvent(10, HotelEventType.START_CINEMA, -1, -1));
        b.onEvent(new HotelEvent(50, HotelEventType.NONE, -1, -1));
        assertFalse(b.filmBezig);
    }

    @Test void testFilmNogBezig() {
        Bioscoop b = new Bioscoop();
        b.onEvent(new HotelEvent(10, HotelEventType.START_CINEMA, -1, -1));
        b.onEvent(new HotelEvent(20, HotelEventType.NONE, -1, -1));
        assertTrue(b.filmBezig);
    }

    @Test void testGotoCinemaCrashetNiet() {
        Bioscoop b = new Bioscoop();
        assertDoesNotThrow(() -> b.onEvent(new HotelEvent(1, HotelEventType.GOTO_CINEMA, 1, -1)));
    }
}
