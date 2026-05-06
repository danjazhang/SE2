import Model.ruimte.Lift;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Lift: ik test verdieping, verzoekenlijst en overerving van Ruimte.
public class LiftTest {

    // Ik maak een Lift; ik verwacht verdieping 1 en een lege verzoekenlijst.
    @Test void testConstructor() {
        Lift l = new Lift();
        assertEquals(1, l.getHuidigeVerdieping());
        assertTrue(l.getVerzoeken().isEmpty());
    }

    // Ik laat de Lift omhoog gaan; ik verwacht dat de verdieping 2 wordt.
    @Test void testGaOmhoog() {
        Lift l = new Lift();
        l.gaOmhoog();
        assertEquals(2, l.getHuidigeVerdieping());
    }

    // Ik laat de Lift omhoog en omlaag gaan; ik verwacht dat hij terug op verdieping 1 staat.
    @Test void testGaOmlaag() {
        Lift l = new Lift();
        l.gaOmhoog();
        l.gaOmlaag();
        assertEquals(1, l.getHuidigeVerdieping());
    }

    // Ik voeg een verzoek toe; ik verwacht dat het in de lijst staat.
    @Test void testVoegVerzoekToe() {
        Lift l = new Lift();
        l.voegVerzoekToe(3);
        assertEquals(1, l.getVerzoeken().size());
        assertEquals(3, l.getVerzoeken().get(0));
    }

    // Ik voeg hetzelfde verzoek twee keer toe; ik verwacht dat het maar een keer wordt opgeslagen.
    @Test void testDubbelVerzoekNietToegevoegd() {
        Lift l = new Lift();
        l.voegVerzoekToe(3);
        l.voegVerzoekToe(3);
        assertEquals(1, l.getVerzoeken().size());
    }

    // Ik voeg verschillende verzoeken toe; ik verwacht dat beide worden opgeslagen.
    @Test void testMeerdereVerschillendeVerzoeken() {
        Lift l = new Lift();
        l.voegVerzoekToe(2);
        l.voegVerzoekToe(4);
        assertEquals(2, l.getVerzoeken().size());
    }

    // Ik maak een Lift; ik verwacht dat hij Ruimte-attributen zoals posX heeft.
    @Test void testErftVanRuimte() {
        Lift l = new Lift();
        assertEquals(0, l.posX);
    }
}
