import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class LiftTest {

    // lift begint op verdieping 0 met een lege verzoeklijst
    @Test
    void testConstructor() {
        Lift l = new Lift();
        assertEquals(0, l.getHuidigeVerdieping());
        assertNotNull(l.getVerzoeken());
        assertTrue(l.getVerzoeken().isEmpty());
    }

    // verdieping gaat omhoog na gaOmhoog()
    @Test
    void testGaOmhoog() {
        Lift l = new Lift();
        l.gaOmhoog();
        assertEquals(1, l.getHuidigeVerdieping());
    }

    // verdieping gaat omlaag na gaOmlaag()
    @Test
    void testGaOmlaag() {
        Lift l = new Lift();
        l.gaOmhoog();
        l.gaOmlaag();
        assertEquals(0, l.getHuidigeVerdieping());
    }

    // verzoek wordt toegevoegd aan de lijst
    @Test
    void testVoegVerzoekToe() {
        Lift l = new Lift();
        l.voegVerzoekToe(3);
        assertEquals(1, l.getVerzoeken().size());
        assertEquals(3, l.getVerzoeken().get(0));
    }

    // hetzelfde verzoek mag maar één keer in de lijst staan
    @Test
    void testDubbelVerzoekWordtNietToegevoegd() {
        Lift l = new Lift();
        l.voegVerzoekToe(3);
        l.voegVerzoekToe(3);
        // verzoek 3 staat maar één keer in de lijst
        assertEquals(1, l.getVerzoeken().size());
    }
}
