import Model.ruimte.Kamer;
import Model.persoon.Gast;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KamerTest {

    // constructor: kamer begint schoon en niet bezet
    @Test void testNieuweKamerIsSchoon() { assertTrue(new Kamer().isSchoon()); }
    @Test void testNieuweKamerNietBezet() { assertFalse(new Kamer().isBezet()); }
    @Test void testSterrenStandaard() { assertEquals(0, new Kamer().sterren); }
    @Test void testKamernummerStandaard() { assertEquals(0, new Kamer().kamernummer); }

    // koppelGast: kamer wordt bezet
    @Test void testKoppelGastMaaktBezet() {
        Kamer k = new Kamer();
        k.koppelGast(new Gast(1, 2));
        assertTrue(k.isBezet());
    }

    // koppelGast: gast krijgt referentie naar kamer
    @Test void testKoppelGastKoppeltKamerAanGast() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertEquals(k, g.kamer);
    }

    // ontkoppelGast: kamer wordt vrijgemaakt
    @Test void testOntkoppelGastMaaktLeeg() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertFalse(k.isBezet());
    }

    // ontkoppelGast: kamer wordt vies
    @Test void testOntkoppelGastMaaktVies() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertFalse(k.isSchoon());
    }

    // ontkoppelGast: gast heeft geen kamer meer
    @Test void testOntkoppelGastZetKamerGastOpNull() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertNull(g.kamer);
    }

    // schoonmaken: kamer wordt schoon
    @Test void testSchoonmakenZetSchoonOpTrue() {
        Kamer k = new Kamer();
        k.schoon = false;
        k.schoonmaken();
        assertTrue(k.isSchoon());
    }

    // gastKomtBinnen: gast is aanwezig na binnenkomen
    @Test void testGastAanwezigNaKomtBinnen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        assertTrue(k.isGastAanwezig(g));
    }

    // gastVerlaatKamer: gast is niet meer aanwezig na verlaten
    @Test void testGastNietAanwezigNaVerlaten() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        k.gastVerlaatKamer(g);
        assertFalse(k.isGastAanwezig(g));
    }

    // isGastAanwezig: false als gast niet ingecheckt is
    @Test void testGastNietAanwezigZonderKoppelen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        assertFalse(k.isGastAanwezig(g));
    }

    // getIngecheckteGasten: geeft alle ingecheckte gasten terug
    @Test void testMeerdereGastenKunnenKoppelen() {
        Kamer k = new Kamer();
        k.koppelGast(new Gast(1, 2));
        k.koppelGast(new Gast(2, 3));
        assertEquals(2, k.getIngecheckteGasten().size());
    }

    // ontkoppelGast: geen crash als gast niet gekoppeld is
    @Test void testOntkoppelNietGekoppeldeGastCrashetNiet() {
        Kamer k = new Kamer();
        assertDoesNotThrow(() -> k.ontkoppelGast(new Gast(1, 2)));
    }

    // zetBezet: kamer wordt bezet gezet
    @Test void testZetBezetTrue() {
        Kamer k = new Kamer();
        k.zetBezet(true);
        assertTrue(k.isBezet());
    }

    // zetBezet: kamer wordt vrijgemaakt
    @Test void testZetBezetFalse() {
        Kamer k = new Kamer();
        k.zetBezet(true);
        k.zetBezet(false);
        assertFalse(k.isBezet());
    }

    // getVrijeKamer: geeft kamer terug als vrij en schoon
    @Test void testGetVrijeKamerVrijEnSchoon() {
        Kamer k = new Kamer();
        assertEquals(k, k.getVrijeKamer());
    }

    // getVrijeKamer: geeft null terug als bezet
    @Test void testGetVrijeKamerBezet() {
        Kamer k = new Kamer();
        k.koppelGast(new Gast(1, 2));
        assertNull(k.getVrijeKamer());
    }

    // getVrijeKamer: geeft null terug als vies
    @Test void testGetVrijeKamerVies() {
        Kamer k = new Kamer();
        k.schoon = false;
        assertNull(k.getVrijeKamer());
    }
}
