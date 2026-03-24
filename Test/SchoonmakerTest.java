import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SchoonmakerTest {

    // een nieuwe schoonmaker is niet bezig en heeft geen kamer
    @Test
    void testConstructor() {
        Schoonmaker s = new Schoonmaker();
        assertFalse(s.bezig);
        assertNull(s.kamer);
    }

    // bezig mag handmatig op true gezet worden
    @Test
    void testZetBezig() {
        Schoonmaker s = new Schoonmaker();
        s.bezig = true;
        assertTrue(s.bezig);
    }

    // kamer mag handmatig gekoppeld worden aan de schoonmaker
    @Test
    void testKoppelKamer() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        s.kamer = k;
        assertEquals(k, s.kamer);
    }

    // schoonmaker is een Persoon, dus huidigVakje begint op null
    @Test
    void testErftVanPersoon() {
        Schoonmaker s = new Schoonmaker();
        assertNull(s.huidigVakje);
        assertNull(s.doelVakje);
    }
}
