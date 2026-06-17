import Model.ruimte.Kamer;
import Model.persoon.Gast;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Kamer: constructor, gast koppelen/ontkoppelen, aanwezigheid, sterren-label, status
public class KamerTest {

    // nieuwe kamer is schoon
    @Test void testNieuweKamerIsSchoon() {
        assertTrue(new Kamer().isSchoon());
    }

    // nieuwe kamer is niet bezet
    @Test void testNieuweKamerNietBezet() {
        assertFalse(new Kamer().isBezet());
    }

    // sterren standaard 0
    @Test void testSterrenStandaard() {
        assertEquals(0, new Kamer().sterren);
    }

    // kamernummer standaard 0
    @Test void testKamernummerStandaard() {
        assertEquals(0, new Kamer().kamernummer);
    }

    // getKamernummer geeft het ingestelde nummer terug
    @Test void testGetKamernummer() {
        Kamer k = new Kamer();
        k.kamernummer = 205;
        assertEquals(205, k.getKamernummer());
    }

    // sterrenLabel voor 0 sterren is leeg
    @Test void testSterrenLabel0() {
        Kamer k = new Kamer();
        k.sterren = 0;
        assertEquals("", k.getSterrenLabel());
    }

    // sterrenLabel voor 1 ster
    @Test void testSterrenLabel1() {
        Kamer k = new Kamer();
        k.sterren = 1;
        assertEquals("★", k.getSterrenLabel());
    }

    // sterrenLabel voor 3 sterren
    @Test void testSterrenLabel3() {
        Kamer k = new Kamer();
        k.sterren = 3;
        assertEquals("★★★", k.getSterrenLabel());
    }

    // koppelGast: kamer wordt bezet
    @Test void testKoppelGastMaaktBezet() {
        Kamer k = new Kamer();
        k.koppelGast(new Gast(1, 2));
        assertTrue(k.isBezet());
    }

    // koppelGast: gast krijgt verwijzing naar kamer
    @Test void testKoppelGastKoppeltKamerAanGast() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertEquals(k, g.kamer);
    }

    // meerdere gasten kunnen aan één kamer gekoppeld worden
    @Test void testMeerdereGastenKunnenKoppelen() {
        Kamer k = new Kamer();
        k.koppelGast(new Gast(1, 2));
        k.koppelGast(new Gast(2, 3));
        assertEquals(2, k.getIngecheckteGasten().size());
    }

    // ingecheckte gasten lijst bevat de juiste gasten
    @Test void testGetIngecheckteGasten() {
        Kamer k = new Kamer();
        Gast g1 = new Gast(1, 1);
        Gast g2 = new Gast(2, 2);
        k.koppelGast(g1);
        k.koppelGast(g2);
        assertTrue(k.getIngecheckteGasten().contains(g1));
        assertTrue(k.getIngecheckteGasten().contains(g2));
    }

    // ontkoppelGast: kamer wordt vrij als laatste gast vertrekt
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

    // ontkoppelGast: gast verliest kamer-verwijzing
    @Test void testOntkoppelGastZetKamerGastOpNull() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertNull(g.kamer);
    }

    // ontkoppelGast: niet-gekoppelde gast geeft geen crash
    @Test void testOntkoppelNietGekoppeldeGastCrashetNiet() {
        assertDoesNotThrow(() -> new Kamer().ontkoppelGast(new Gast(1, 2)));
    }

    // na ontkoppelen van alle gasten is kamer vrij en vies
    @Test void testOntkoppelAlleGastenMaaktVrijEnVies() {
        Kamer k = new Kamer();
        Gast g1 = new Gast(1, 1);
        Gast g2 = new Gast(2, 1);
        k.koppelGast(g1);
        k.koppelGast(g2);
        k.ontkoppelGast(g1);
        assertTrue(k.isBezet()); // g2 nog aanwezig
        k.ontkoppelGast(g2);
        assertFalse(k.isBezet());
        assertFalse(k.isSchoon());
    }

    // gastKomtBinnen: gast wordt aanwezig gemarkeerd
    @Test void testGastAanwezigNaKomtBinnen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        assertTrue(k.isGastAanwezig(g));
    }

    // gastKomtBinnen voor niet-ingecheckte gast doet niets
    @Test void testGastKomtBinnenZonderKoppeling() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 1);
        k.gastKomtBinnen(g);
        assertFalse(k.isGastAanwezig(g));
    }

    // aanwezigen bevat gast na gastKomtBinnen
    @Test void testAanwezigenNaKomtBinnen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 1);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        assertTrue(k.getAanwezigen().contains(g));
    }

    // gastVerlaatKamer: gast is niet meer aanwezig
    @Test void testGastNietAanwezigNaVerlaten() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        k.gastVerlaatKamer(g);
        assertFalse(k.isGastAanwezig(g));
    }

    // gastVerlaatKamer voor niet-ingecheckte gast geeft geen crash
    @Test void testGastVerlaatKamerZonderKoppeling() {
        assertDoesNotThrow(() -> new Kamer().gastVerlaatKamer(new Gast(1, 1)));
    }

    // aanwezigen zijn leeg na verlaten
    @Test void testAanwezigenNaVerlaten() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 1);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        k.gastVerlaatKamer(g);
        assertFalse(k.getAanwezigen().contains(g));
    }

    // gast is niet aanwezig zonder koppeling
    @Test void testGastNietAanwezigZonderKoppelen() {
        assertFalse(new Kamer().isGastAanwezig(new Gast(1, 2)));
    }

    // getAanwezigen is leeg bij nieuwe kamer
    @Test void testAanwezigenLeegBijNieuweKamer() {
        assertTrue(new Kamer().getAanwezigen().isEmpty());
    }

    // zetBezet true
    @Test void testZetBezetTrue() {
        Kamer k = new Kamer();
        k.zetBezet(true);
        assertTrue(k.isBezet());
    }

    // zetBezet false
    @Test void testZetBezetFalse() {
        Kamer k = new Kamer();
        k.zetBezet(true);
        k.zetBezet(false);
        assertFalse(k.isBezet());
    }

    // schoonmaken: kamer wordt schoon
    @Test void testSchoonmakenZetSchoonOpTrue() {
        Kamer k = new Kamer();
        k.schoon = false;
        k.schoonmaken();
        assertTrue(k.isSchoon());
    }

    // getVrijeKamer: geeft zichzelf terug als vrij en schoon
    @Test void testGetVrijeKamerVrijEnSchoon() {
        Kamer k = new Kamer();
        assertSame(k, k.getVrijeKamer());
    }

    // getVrijeKamer: null als kamer bezet is
    @Test void testGetVrijeKamerBezet() {
        Kamer k = new Kamer();
        k.koppelGast(new Gast(1, 2));
        assertNull(k.getVrijeKamer());
    }

    // getVrijeKamer: null als kamer vies is
    @Test void testGetVrijeKamerVies() {
        Kamer k = new Kamer();
        k.schoon = false;
        assertNull(k.getVrijeKamer());
    }

    // isKamer: true
    @Test void testIsKamer() {
        assertTrue(new Kamer().isKamer());
    }
}
