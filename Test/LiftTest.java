import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class LiftTest {

    @Test
    void testConstructor() {
        Lift l = new Lift();
        assertEquals(0, l.getHuidigeVerdieping());
        assertNotNull(l.getVerzoeken());
        assertTrue(l.getVerzoeken().isEmpty());
    }

    @Test
    void testGaOmhoog() {
        Lift l = new Lift();
        l.gaOmhoog();
        assertEquals(1, l.getHuidigeVerdieping());
    }

    @Test
    void testGaOmlaag() {
        Lift l = new Lift();
        l.gaOmhoog();
        l.gaOmlaag();
        assertEquals(0, l.getHuidigeVerdieping());
    }

    @Test
    void testVoegVerzoekToe() {
        Lift l = new Lift();
        l.voegVerzoekToe(3);
        assertEquals(1, l.getVerzoeken().size());
        assertEquals(3, l.getVerzoeken().get(0));
    }

    @Test
    void testDubbelVerzoekWordtNietToegevoegd() {
        Lift l = new Lift();
        l.voegVerzoekToe(3);
        l.voegVerzoekToe(3);
        assertEquals(1, l.getVerzoeken().size());
    }

    @Test
    void testOpenEnSluitDeur() {
        Lift l = new Lift();
        assertDoesNotThrow(() -> l.openDeur());
        assertDoesNotThrow(() -> l.sluitDeur());
    }

    @Test
    void testMeerdereVerzoeken() {
        Lift l = new Lift();
        l.voegVerzoekToe(1);
        l.voegVerzoekToe(2);
        l.voegVerzoekToe(3);
        assertEquals(3, l.getVerzoeken().size());
    }
}
