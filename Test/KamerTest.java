import Model.ruimte.Kamer;
import Model.persoon.Gast;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KamerTest {

    @Test void testNieuweKamerIsSchoon() { assertTrue(new Kamer().isSchoon()); }
    @Test void testNieuweKamerNietBezet() { assertFalse(new Kamer().isBezet()); }
    @Test void testSterrenStandaard() { assertEquals(0, new Kamer().sterren); }
    @Test void testKamernummerStandaard() { assertEquals(0, new Kamer().kamernummer); }

    @Test
    void testKoppelGastMaaktBezet() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertTrue(k.isBezet());
    }

    @Test
    void testKoppelGastKoppeltKamerAanGast() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertEquals(k, g.kamer);
    }

    @Test
    void testOntkoppelGastMaaktLeeg() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertFalse(k.isBezet());
    }

    @Test
    void testOntkoppelGastMaaktVies() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertFalse(k.isSchoon());
    }

    @Test
    void testOntkoppelGastZetKamerGastOpNull() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertNull(g.kamer);
    }

    @Test
    void testSchoonmakenZetSchoonOpTrue() {
        Kamer k = new Kamer();
        k.schoon = false;
        k.schoonmaken();
        assertTrue(k.isSchoon());
    }

    @Test
    void testGastNietAanwezigNaKoppelen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertFalse(k.isGastAanwezig(g));
    }

    @Test
    void testGastAanwezigNaKomtBinnen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        assertTrue(k.isGastAanwezig(g));
    }

    @Test
    void testGastNietAanwezigNaVerlaten() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        k.gastVerlaatKamer(g);
        assertFalse(k.isGastAanwezig(g));
    }

    @Test
    void testMeerdereGastenKunnenKoppelen() {
        Kamer k = new Kamer();
        k.koppelGast(new Gast(1, 2));
        k.koppelGast(new Gast(2, 3));
        assertEquals(2, k.getIngecheckteGasten().size());
    }

    @Test
    void testOntkoppelNietGekoppeldeGastCrashetNiet() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        assertDoesNotThrow(() -> k.ontkoppelGast(g));
    }
}
