import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class KamerTest {

    @Test
    void testConstructor() {
        Kamer k = new Kamer();
        assertTrue(k.schoon);
        assertEquals(0, k.sterren);
    }

    @Test
    void testSterrenStandaard() {
        Kamer k = new Kamer();
        assertEquals(0, k.sterren);
    }

    @Test
    void testErftVanRuimte() {
        Kamer k = new Kamer();
        assertEquals(0, k.posX);
        assertEquals(0, k.posY);
    }

    @Test
    void testZetSterren() {
        Kamer k = new Kamer();
        k.sterren = 4;
        assertEquals(4, k.sterren);
    }

    @Test
    void testZetSchoonOpFalse() {
        Kamer k = new Kamer();
        k.schoon = false;
        assertFalse(k.schoon);
    }

    @Test
    void testKoppelGast() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 3);
        k.koppelGast(g);
        assertTrue(k.isBezet());
        assertEquals(k, g.kamer);
    }

    @Test
    void testOntkoppelGast() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 3);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertFalse(k.isBezet());
        assertNull(g.kamer);
        assertFalse(k.schoon);
    }

    @Test
    void testOntkoppelGastDieNietIngechecktIs() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 3);
        // mag niet crashen als gast niet ingecheckt is
        assertDoesNotThrow(() -> k.ontkoppelGast(g));
    }

    @Test
    void testGastKomtBinnenEnVerlaat() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 3);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        assertTrue(k.isGastAanwezig(g));
        k.gastVerlaatKamer(g);
        assertFalse(k.isGastAanwezig(g));
    }

    @Test
    void testGastKomtBinnenZonderCheckIn() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 3);
        // mag niet crashen als gast niet ingecheckt is
        assertDoesNotThrow(() -> k.gastKomtBinnen(g));
        assertFalse(k.isGastAanwezig(g));
    }

    @Test
    void testSchoonmaken() {
        Kamer k = new Kamer();
        k.schoon = false;
        k.schoonmaken();
        assertTrue(k.isSchoon());
    }

    @Test
    void testGetIngecheckteGasten() {
        Kamer k = new Kamer();
        Gast g1 = new Gast(1, 3);
        Gast g2 = new Gast(2, 4);
        k.koppelGast(g1);
        k.koppelGast(g2);
        assertEquals(2, k.getIngecheckteGasten().size());
    }

    @Test
    void testGetKamernummer() {
        Kamer k = new Kamer();
        k.kamernummer = 101;
        assertEquals(101, k.getKamernummer());
    }

    @Test
    void testOntkoppelGastDieNogBinnenIs() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 3);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        // ontkoppelen terwijl gast fysiek aanwezig is
        k.ontkoppelGast(g);
        assertFalse(k.isBezet());
    }
}
