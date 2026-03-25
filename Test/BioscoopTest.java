import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class BioscoopTest {

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

    // filmBezig kan op true gezet worden
    @Test
    void testZetFilmBezig() {
        Bioscoop b = new Bioscoop();
        b.filmBezig = true;
        assertTrue(b.filmBezig);
    }

    // een gast kan aan de gastenlijst toegevoegd worden
    @Test
    void testVoegGastToe() {
        Bioscoop b = new Bioscoop();
        Gast g = new Gast(3);
        b.gasten.add(g);
        assertEquals(1, b.gasten.size());
        assertEquals(g, b.gasten.get(0));
    }
}
