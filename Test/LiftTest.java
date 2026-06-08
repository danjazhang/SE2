import Model.ruimte.Lift;
import Model.Hotel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LiftTest {

    // Ik maak een nieuwe lift aan; ik verwacht dat die op verdieping 1 start en nog geen verzoeken heeft.
    @Test void testConstructor() {
        Lift l = new Lift(hotel);
        assertEquals(1, l.getHuidigeVerdieping());
        assertTrue(l.getVerzoeken().isEmpty());
    }

    // Ik laat de lift omhoog gaan; ik verwacht dat de huidige verdieping met 1 stijgt.
    @Test void testGaOmhoog() {
        Lift l = new Lift();
        l.gaOmhoog();
        assertEquals(2, l.getHuidigeVerdieping());
    }

    // Ik laat de lift eerst omhoog en daarna omlaag gaan; ik verwacht dat hij terug op verdieping 1 komt.
    @Test void testGaOmlaag() {
        Lift l = new Lift();
        l.gaOmhoog();
        l.gaOmlaag();
        assertEquals(1, l.getHuidigeVerdieping());
    }

    // Ik voeg een verzoek toe; ik verwacht dat dit verzoek in de lijst terechtkomt.
    @Test void testVoegVerzoekToe() {
        Lift l = new Lift();
        l.voegVerzoekToe(3);
        assertEquals(1, l.getVerzoeken().size());
        assertEquals(3, l.getVerzoeken().get(0));
    }

    // Ik voeg hetzelfde verzoek twee keer toe; ik verwacht dat het maar één keer bewaard blijft.
    @Test void testDubbelVerzoekNietToegevoegd() {
        Lift l = new Lift();
        l.voegVerzoekToe(3);
        l.voegVerzoekToe(3);
        assertEquals(1, l.getVerzoeken().size());
    }

    // Ik voeg meerdere verschillende verzoeken toe; ik verwacht dat de lift ze allemaal bewaart.
    @Test void testMeerdereVerschillendeVerzoeken() {
        Lift l = new Lift();
        l.voegVerzoekToe(2);
        l.voegVerzoekToe(4);
        assertEquals(2, l.getVerzoeken().size());
    }

    // Ik gebruik de lift als subklasse van Ruimte; ik verwacht dat hij de standaard ruimte-eigenschappen heeft.
    @Test void testErftVanRuimte() {
        Lift l = new Lift();
        assertEquals(0, l.posX);
    }
}
