import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class BioscoopTest {

    @Test
    void testConstructor() {
        Bioscoop b = new Bioscoop();
        assertFalse(b.filmBezig);
        assertEquals(0, b.filmDuur);
        assertNotNull(b.gasten);
        assertTrue(b.gasten.isEmpty());
    }

    @Test
    void testErftVanRuimte() {
        Bioscoop b = new Bioscoop();
        assertEquals(0, b.posX);
        assertEquals(0, b.posY);
    }

    @Test
    void testZetFilmBezig() {
        Bioscoop b = new Bioscoop();
        b.filmBezig = true;
        assertTrue(b.filmBezig);
    }

    @Test
    void testVoegGastToe() {
        Bioscoop b = new Bioscoop();
        Gast g = new Gast(1, 3);
        b.gasten.add(g);
        assertEquals(1, b.gasten.size());
        assertEquals(g, b.gasten.get(0));
    }

    @Test
    void testStartFilmCrashetNiet() {
        Bioscoop b = new Bioscoop();
        assertDoesNotThrow(() -> b.startFilm());
    }

    @Test
    void testStopFilmCrashetNiet() {
        Bioscoop b = new Bioscoop();
        assertDoesNotThrow(() -> b.stopFilm());
    }

    @Test
    void testBetreedBioscoopCrashetNiet() {
        Bioscoop b = new Bioscoop();
        assertDoesNotThrow(() -> b.betreedBioscoop());
    }
}
