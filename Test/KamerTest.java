import Model.ruimte.Kamer;
import Model.persoon.Gast;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KamerTest {

    // Ik maak een nieuwe kamer aan; ik verwacht dat die schoon begint.
    @Test
    void testNieuweKamerIsSchoon() {
        assertTrue(new Kamer().isSchoon());
    }

    // Ik maak een nieuwe kamer aan; ik verwacht dat die nog niet bezet is.
    @Test
    void testNieuweKamerNietBezet() {
        assertFalse(new Kamer().isBezet());
    }

    // Ik maak een nieuwe kamer aan; ik verwacht dat het aantal sterren standaard 0 is.
    @Test
    void testSterrenStandaard() {
        assertEquals(0, new Kamer().sterren);
    }

    // Ik maak een nieuwe kamer aan; ik verwacht dat het kamernummer standaard 0 is.
    @Test
    void testKamernummerStandaard() {
        assertEquals(0, new Kamer().kamernummer);
    }

    // Ik koppel een gast aan een kamer; ik verwacht dat de kamer daarna bezet is.
    @Test
    void testKoppelGastMaaktBezet() {
        Kamer k = new Kamer();
        k.koppelGast(new Gast(1, 2));
        assertTrue(k.isBezet());
    }

    // Ik koppel een gast aan een kamer; ik verwacht dat de gast een verwijzing naar die kamer krijgt.
    @Test
    void testKoppelGastKoppeltKamerAanGast() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);

        k.koppelGast(g);

        assertEquals(k, g.kamer);
    }

    // Ik koppel meerdere gasten aan een kamer; ik verwacht dat de lijst beide bevat.
    @Test
    void testMeerdereGastenKunnenKoppelen() {
        Kamer k = new Kamer();

        k.koppelGast(new Gast(1, 2));
        k.koppelGast(new Gast(2, 3));

        assertEquals(2, k.getIngecheckteGasten().size());
    }

    // Ik ontkoppel een eerder ingecheckte gast; ik verwacht dat de kamer daarna weer vrij is.
    @Test
    void testOntkoppelGastMaaktLeeg() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);

        k.koppelGast(g);
        k.ontkoppelGast(g);

        assertFalse(k.isBezet());
    }

    // Ik laat een gast uitchecken; ik verwacht dat de kamer daarna vies wordt.
    @Test
    void testOntkoppelGastMaaktVies() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);

        k.koppelGast(g);
        k.ontkoppelGast(g);

        assertFalse(k.isSchoon());
    }

    // Ik ontkoppel een gast van een kamer; ik verwacht dat de gast daarna geen kamer meer heeft.
    @Test
    void testOntkoppelGastZetKamerGastOpNull() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);

        k.koppelGast(g);
        k.ontkoppelGast(g);

        assertNull(g.kamer);
    }

    // Ik ontkoppel een gast die nooit gekoppeld was; ik verwacht dat dit geen crash geeft.
    @Test
    void testOntkoppelNietGekoppeldeGastCrashetNiet() {
        Kamer k = new Kamer();

        assertDoesNotThrow(() -> k.ontkoppelGast(new Gast(1, 2)));
    }

    // Ik laat een gekoppelde gast de kamer binnenkomen; ik verwacht dat hij als aanwezig wordt gemarkeerd.
    @Test
    void testGastAanwezigNaKomtBinnen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);

        k.koppelGast(g);
        k.gastKomtBinnen(g);

        assertTrue(k.isGastAanwezig(g));
    }

    // Ik laat een aanwezige gast de kamer verlaten; ik verwacht dat hij niet meer aanwezig is.
    @Test
    void testGastNietAanwezigNaVerlaten() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);

        k.koppelGast(g);
        k.gastKomtBinnen(g);
        k.gastVerlaatKamer(g);

        assertFalse(k.isGastAanwezig(g));
    }

    // Ik vraag of een niet-gekoppelde gast aanwezig is; ik verwacht false.
    @Test
    void testGastNietAanwezigZonderKoppelen() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);

        assertFalse(k.isGastAanwezig(g));
    }

    // Ik zet de bezet-status handmatig op true; ik verwacht dat de kamer bezet is.
    @Test
    void testZetBezetTrue() {
        Kamer k = new Kamer();

        k.zetBezet(true);

        assertTrue(k.isBezet());
    }

    // Ik zet de bezet-status op true en daarna op false; ik verwacht dat de kamer vrij is.
    @Test
    void testZetBezetFalse() {
        Kamer k = new Kamer();

        k.zetBezet(true);
        k.zetBezet(false);

        assertFalse(k.isBezet());
    }

    // Ik maak een vuile kamer schoon; ik verwacht dat de kamer weer schoon is.
    @Test
    void testSchoonmakenZetSchoonOpTrue() {
        Kamer k = new Kamer();

        k.schoon = false;
        k.schoonmaken();

        assertTrue(k.isSchoon());
    }

    // Ik vraag een vrije en schone kamer op; ik verwacht dat de kamer zichzelf teruggeeft.
    @Test
    void testGetVrijeKamerVrijEnSchoon() {
        Kamer k = new Kamer();

        assertEquals(k, k.getVrijeKamer());
    }

    // Ik vraag een kamer op die bezet is; ik verwacht null.
    @Test
    void testGetVrijeKamerBezet() {
        Kamer k = new Kamer();

        k.koppelGast(new Gast(1, 2));

        assertNull(k.getVrijeKamer());
    }

    // Ik vraag een kamer op die vies is; ik verwacht null.
    @Test
    void testGetVrijeKamerVies() {
        Kamer k = new Kamer();

        k.schoon = false;

        assertNull(k.getVrijeKamer());
    }
}