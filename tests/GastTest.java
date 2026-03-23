import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GastTest {

    // controleer dat een nieuwe gast de juiste sterren heeft en nog geen kamer
    @Test
    void testConstructor() {
        Gast gast = new Gast(3);
        assertEquals(3, gast.gewensteSterren);
        assertNull(gast.kamer);
    }

    // twee gasten met verschillende sterren mogen niet dezelfde waarde hebben
    @Test
    void testGewensteSterrenVerschillend() {
        Gast gast1 = new Gast(1);
        Gast gast2 = new Gast(5);
        assertEquals(1, gast1.gewensteSterren);
        assertEquals(5, gast2.gewensteSterren);
    }

    // na checkIn moet de gast gekoppeld zijn aan de opgegeven kamer
    @Test
    void testCheckIn() {
        Gast gast = new Gast(2);
        Kamer kamer = new Kamer();
        gast.checkIn(kamer);
        assertEquals(kamer, gast.kamer);
    }

    // bij een tweede checkIn moet de gast de nieuwe kamer hebben, niet de oude
    @Test
    void testCheckInVerschillendeKamer() {
        Gast gast = new Gast(3);
        Kamer kamer1 = new Kamer();
        Kamer kamer2 = new Kamer();
        gast.checkIn(kamer1);
        gast.checkIn(kamer2);
        assertEquals(kamer2, gast.kamer);
    }

    // geërfd van Persoon: huidigVakje en doelVakje beginnen op null
    @Test
    void testGeerfdeVakjesNull() {
        Gast gast = new Gast(3);
        assertNull(gast.huidigVakje);
        assertNull(gast.doelVakje);
    }
}
