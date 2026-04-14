import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class GastTest {

    @Test
    void testConstructor() {
        Gast gast = new Gast(1, 3);
        assertEquals(1, gast.gastId);
        assertEquals(3, gast.gewensteSterren);
        assertNull(gast.kamer);
    }

    @Test
    void testGewensteSterrenVerschillend() {
        Gast gast1 = new Gast(1, 1);
        Gast gast2 = new Gast(2, 5);
        assertEquals(1, gast1.gewensteSterren);
        assertEquals(5, gast2.gewensteSterren);
    }

    @Test
    void testCheckIn() {
        Gast gast = new Gast(1, 2);
        Kamer kamer = new Kamer();
        gast.checkIn(kamer);
        assertEquals(kamer, gast.kamer);
    }

    @Test
    void testCheckInVerschillendeKamer() {
        Gast gast = new Gast(1, 3);
        Kamer kamer1 = new Kamer();
        Kamer kamer2 = new Kamer();
        gast.checkIn(kamer1);
        gast.checkIn(kamer2);
        // gast is nu aan beide kamers gekoppeld, kamer2 is de laatste
        assertEquals(kamer2, gast.kamer);
    }

    @Test
    void testGeerfdeVakjesNull() {
        Gast gast = new Gast(1, 3);
        assertNull(gast.huidigVakje);
        assertNull(gast.doelVakje);
    }

    @Test
    void testCheckOutCrashetNiet() {
        Gast gast = new Gast(1, 3);
        Kamer kamer = new Kamer();
        gast.checkIn(kamer);
        assertDoesNotThrow(() -> gast.checkOut());
    }

    @Test
    void testCheckOutZonderKamerCrashetNiet() {
        Gast gast = new Gast(1, 3);
        assertDoesNotThrow(() -> gast.checkOut());
    }

    @Test
    void testGaNaarActiviteitCrashetNiet() {
        Gast gast = new Gast(1, 3);
        assertDoesNotThrow(() -> gast.gaNaarActiviteit());
    }

    @Test
    void testGaNaarKamerCrashetNiet() {
        Gast gast = new Gast(1, 3);
        Kamer kamer = new Kamer();
        gast.checkIn(kamer);
        assertDoesNotThrow(() -> gast.gaNaarkamer());
    }

    @Test
    void testVerlaatKamerCrashetNiet() {
        Gast gast = new Gast(1, 3);
        Kamer kamer = new Kamer();
        gast.checkIn(kamer);
        gast.gaNaarkamer();
        assertDoesNotThrow(() -> gast.verlaatKamer());
    }

    @Test
    void testGaNaarKamerZonderKamerCrashetNiet() {
        Gast gast = new Gast(1, 3);
        assertDoesNotThrow(() -> gast.gaNaarkamer());
    }

    @Test
    void testVerlaatKamerZonderKamerCrashetNiet() {
        Gast gast = new Gast(1, 3);
        assertDoesNotThrow(() -> gast.verlaatKamer());
    }
}
