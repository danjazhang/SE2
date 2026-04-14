import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class SchoonmakerTest {

    @Test
    void testConstructor() {
        Schoonmaker s = new Schoonmaker();
        assertFalse(s.bezig);
        assertNull(s.kamer);
    }

    @Test
    void testZetBezig() {
        Schoonmaker s = new Schoonmaker();
        s.bezig = true;
        assertTrue(s.bezig);
    }

    @Test
    void testKoppelKamer() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        s.kamer = k;
        assertEquals(k, s.kamer);
    }

    @Test
    void testErftVanPersoon() {
        Schoonmaker s = new Schoonmaker();
        assertNull(s.huidigVakje);
        assertNull(s.doelVakje);
    }

    @Test
    void testMaakKamerSchoon() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        k.schoon = false;
        s.maakKamerSchoon(k);
        assertTrue(k.schoon);
        // na schoonmaken is schoonmaker niet meer bezig
        assertFalse(s.bezig);
        assertNull(s.kamer);
    }

    @Test
    void testHandelEmergency() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        k.schoon = false;
        s.handelEmergency(k);
        assertTrue(k.schoon);
    }

    @Test
    void testGaNaarOptimalePositieCrashetNiet() {
        Schoonmaker s = new Schoonmaker();
        assertDoesNotThrow(() -> s.gaNaarOptimalePositie());
    }
}
