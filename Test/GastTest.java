import Model.persoon.Gast;
import Model.ruimte.Kamer;
import Model.layout.Vakje;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GastTest {

    // Ik maak een nieuwe gast aan; ik verwacht dat id, gewenste sterren en kamer correct starten.
    @Test void testConstructor() {
        Gast g = new Gast(1, 3);
        assertEquals(1, g.gastId);
        assertEquals(3, g.gewensteSterren);
        assertNull(g.kamer);
    }

    // standaard vlaggen zijn false
    @Test void testStandaardVlaggen() {
        Gast g = new Gast(1, 1);
        assertFalse(g.uitcheckend);
        assertFalse(g.inLift);
        assertFalse(g.gebruiktLift);
        assertFalse(g.wachtOpLift);
        assertFalse(g.moetUitstappen);
        assertFalse(g.wachtOpRestaurant);
        assertFalse(g.keertTerugNaAlarm);
    }

    // standaard tellerwaarden
    @Test void testStandaardTellers() {
        Gast g = new Gast(1, 1);
        assertEquals(0, g.wachtTicks);
        assertEquals(-1, g.summonTick);
        assertEquals(1, g.gewensteVerdieping);
    }

    // huidigVakje is null na constructie
    @Test void testHuidigVakjeNull() {
        assertNull(new Gast(1, 2).huidigVakje);
    }

    // doelVakje is null na constructie
    @Test void testDoelVakjeNull() {
        assertNull(new Gast(1, 2).doelVakje);
    }

    // eindbestemming is null na constructie
    @Test void testEindbestemmingNull() {
        assertNull(new Gast(1, 1).eindbestemming);
    }

    // gaNaarkamer zonder kamer: geen crash
    @Test void testGaNaarkamerZonderKamer() {
        assertDoesNotThrow(() -> new Gast(1, 2).gaNaarkamer());
    }

    // verlaatKamer zonder kamer: geen crash
    @Test void testVerlaatKamerZonderKamer() {
        assertDoesNotThrow(() -> new Gast(1, 2).verlaatKamer());
    }

    // gaNaarkamer met kamer: gast wordt aanwezig in kamer
    @Test void testGaNaarkamerMetKamer() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        g.gaNaarkamer();
        assertTrue(k.isGastAanwezig(g));
    }

    // verlaatKamer na binnenkomen: niet meer aanwezig
    @Test void testVerlaatKamerMetKamer() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        g.verlaatKamer();
        assertFalse(k.isGastAanwezig(g));
    }

    // zetDoel: doel wordt bewaard
    @Test void testZetDoel() {
        Gast g = new Gast(1, 2);
        Vakje v = new Vakje();
        g.zetDoel(v);
        assertEquals(v, g.doelVakje);
    }

    // isGast: true
    @Test void testIsGast() {
        assertTrue(new Gast(1, 1).isGast());
    }

    // isSchoonmaker: false
    @Test void testIsSchoonmaker() {
        assertFalse(new Gast(1, 1).isSchoonmaker());
    }

    // getStatusTekst: bevat gastId
    @Test void testGetStatusTekstBevatId() {
        assertTrue(new Gast(7, 2).getStatusTekst().contains("7"));
    }

    // getStatusTekst: bevat sterren
    @Test void testGetStatusTekstBevatSterren() {
        assertTrue(new Gast(1, 3).getStatusTekst().contains("3"));
    }

    // getStatusTekst: "aan het uitchecken" als uitcheckend=true
    @Test void testGetStatusTekstUitcheckend() {
        Gast g = new Gast(1, 1);
        g.uitcheckend = true;
        assertTrue(g.getStatusTekst().contains("uitchecken"));
    }

    // getStatusTekst: "in lift" als inLift=true
    @Test void testGetStatusTekstInLift() {
        Gast g = new Gast(1, 1);
        g.inLift = true;
        assertTrue(g.getStatusTekst().contains("lift"));
    }

    // getStatusTekst: "wacht" als geen doel
    @Test void testGetStatusTekstWacht() {
        Gast g = new Gast(1, 1);
        g.zetStartPositie(new Vakje());
        assertTrue(g.getStatusTekst().contains("wacht"));
    }

    // getStatusTekst: "geen positie" als huidigVakje null
    @Test void testGetStatusTekstGeenPositie() {
        assertTrue(new Gast(1, 1).getStatusTekst().contains("positie"));
    }
}
