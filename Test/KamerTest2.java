import Model.persoon.Gast;
import Model.ruimte.Kamer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Extra tests voor Kamer: sterren-label, aanwezigen, meerdere gasten
public class KamerTest2 {

    // sterrenLabel voor 1 ster
    @Test void testSterrenLabel1() {
        Kamer k = new Kamer(); k.sterren = 1;
        assertEquals("★", k.getSterrenLabel());
    }

    // sterrenLabel voor 3 sterren
    @Test void testSterrenLabel3() {
        Kamer k = new Kamer(); k.sterren = 3;
        assertEquals("★★★", k.getSterrenLabel());
    }

    // sterrenLabel voor 0 sterren is leeg
    @Test void testSterrenLabel0() {
        Kamer k = new Kamer(); k.sterren = 0;
        assertEquals("", k.getSterrenLabel());
    }

    // getKamernummer geeft het ingestelde nummer terug
    @Test void testGetKamernummer() {
        Kamer k = new Kamer(); k.kamernummer = 205;
        assertEquals(205, k.getKamernummer());
    }

    // getAanwezigen is leeg bij nieuwe kamer
    @Test void testAanwezigenLeegBijNieuweKamer() {
        assertTrue(new Kamer().getAanwezigen().isEmpty());
    }

    // gastKomtBinnen voor niet-ingecheckte gast doet niets
    @Test void testGastKomtBinnenZonderKoppeling() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 1);
        k.gastKomtBinnen(g); // gast is niet ingecheckt
        assertFalse(k.isGastAanwezig(g));
    }

    // gastVerlaatKamer voor niet-ingecheckte gast doet niets
    @Test void testGastVerlaatKamerZonderKoppeling() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 1);
        assertDoesNotThrow(() -> k.gastVerlaatKamer(g));
    }

    // na ontkoppelen van alle gasten is kamer niet bezet en vies
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

    // getVrijeKamer geeft null als kamer vies is maar niet bezet
    @Test void testGetVrijeKamerViesNietBezet() {
        Kamer k = new Kamer();
        k.schoon = false;
        assertNull(k.getVrijeKamer());
    }

    // getVrijeKamer geeft kamer terug als schoon en vrij
    @Test void testGetVrijeKamerSchoonEnVrij() {
        Kamer k = new Kamer();
        assertSame(k, k.getVrijeKamer());
    }

    // schoonmaken zet schoon op true
    @Test void testSchoonmaken() {
        Kamer k = new Kamer();
        k.schoon = false;
        k.schoonmaken();
        assertTrue(k.isSchoon());
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

    // aanwezigen bevat gast na gastKomtBinnen
    @Test void testAanwezigenNaKomtBinnen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 1);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        assertTrue(k.getAanwezigen().contains(g));
    }

    // aanwezigen bevat gast niet meer na gastVerlaatKamer
    @Test void testAanwezigenNaVerlaten() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 1);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        k.gastVerlaatKamer(g);
        assertFalse(k.getAanwezigen().contains(g));
    }
}
