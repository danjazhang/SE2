import Model.ruimte.Kamer;
import Model.persoon.Gast;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Kamer: ik test bezetting, schoon-status en koppeling met gasten.
public class KamerTest {

    // Ik maak een nieuwe Kamer; ik verwacht dat hij schoon is.
    @Test void testNieuweKamerIsSchoon() { assertTrue(new Kamer().isSchoon()); }
    // Ik maak een nieuwe Kamer; ik verwacht dat hij nog niet bezet is.
    @Test void testNieuweKamerNietBezet() { assertFalse(new Kamer().isBezet()); }
    // Ik maak een nieuwe Kamer; ik verwacht dat sterren standaard 0 is.
    @Test void testSterrenStandaard() { assertEquals(0, new Kamer().sterren); }
    // Ik maak een nieuwe Kamer; ik verwacht dat kamernummer standaard 0 is.
    @Test void testKamernummerStandaard() { assertEquals(0, new Kamer().kamernummer); }

    // Ik koppel een Gast aan een Kamer; ik verwacht dat de Kamer bezet is.
    @Test
    void testKoppelGastMaaktBezet() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertTrue(k.isBezet());
    }

    // Ik koppel een Gast aan een Kamer; ik verwacht dat de Gast die Kamer bewaart.
    @Test
    void testKoppelGastKoppeltKamerAanGast() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertEquals(k, g.kamer);
    }

    // Ik ontkoppel een gekoppelde Gast; ik verwacht dat de Kamer niet meer bezet is.
    @Test
    void testOntkoppelGastMaaktLeeg() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertFalse(k.isBezet());
    }

    // Ik ontkoppel de laatste Gast; ik verwacht dat de Kamer vies wordt.
    @Test
    void testOntkoppelGastMaaktVies() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertFalse(k.isSchoon());
    }

    // Ik ontkoppel een Gast; ik verwacht dat zijn kamerreferentie null wordt.
    @Test
    void testOntkoppelGastZetKamerGastOpNull() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.ontkoppelGast(g);
        assertNull(g.kamer);
    }

    // Ik maak een vieze Kamer schoon; ik verwacht dat isSchoon true wordt.
    @Test
    void testSchoonmakenZetSchoonOpTrue() {
        Kamer k = new Kamer();
        k.schoon = false;
        k.schoonmaken();
        assertTrue(k.isSchoon());
    }

    // Ik koppel een Gast; ik verwacht dat hij nog niet fysiek aanwezig is.
    @Test
    void testGastNietAanwezigNaKoppelen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertFalse(k.isGastAanwezig(g));
    }

    // Ik laat een gekoppelde Gast binnenkomen; ik verwacht dat hij aanwezig is.
    @Test
    void testGastAanwezigNaKomtBinnen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        assertTrue(k.isGastAanwezig(g));
    }

    // Ik laat een aanwezige Gast vertrekken; ik verwacht dat hij niet meer aanwezig is.
    @Test
    void testGastNietAanwezigNaVerlaten() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        k.gastVerlaatKamer(g);
        assertFalse(k.isGastAanwezig(g));
    }

    // Ik koppel meerdere Gasten; ik verwacht dat de Kamer ze allemaal bewaart.
    @Test
    void testMeerdereGastenKunnenKoppelen() {
        Kamer k = new Kamer();
        k.koppelGast(new Gast(1, 2));
        k.koppelGast(new Gast(2, 3));
        assertEquals(2, k.getIngecheckteGasten().size());
    }

    // Ik ontkoppel een niet-gekoppelde Gast; ik verwacht geen exception.
    @Test
    void testOntkoppelNietGekoppeldeGastCrashetNiet() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        assertDoesNotThrow(() -> k.ontkoppelGast(g));
    }
}
