import Model.ruimte.Lift;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LiftTest {

    @Test void testConstructor() {
        Lift l = new Lift();
        assertEquals(1, l.getHuidigeVerdieping());
        assertTrue(l.getVerzoeken().isEmpty());
    }

    @Test void testGaOmhoog() {
        Lift l = new Lift();
        l.gaOmhoog();
        assertEquals(2, l.getHuidigeVerdieping());
    }

    @Test void testGaOmlaag() {
        Lift l = new Lift();
        l.gaOmhoog();
        l.gaOmlaag();
        assertEquals(1, l.getHuidigeVerdieping());
    }

    @Test void testVoegVerzoekToe() {
        Lift l = new Lift();
        l.voegVerzoekToe(3);
        assertEquals(1, l.getVerzoeken().size());
        assertEquals(3, l.getVerzoeken().get(0));
    }

    @Test void testDubbelVerzoekNietToegevoegd() {
        Lift l = new Lift();
        l.voegVerzoekToe(3);
        l.voegVerzoekToe(3);
        assertEquals(1, l.getVerzoeken().size());
    }

    @Test void testMeerdereVerschillendeVerzoeken() {
        Lift l = new Lift();
        l.voegVerzoekToe(2);
        l.voegVerzoekToe(4);
        assertEquals(2, l.getVerzoeken().size());
    }

    @Test void testErftVanRuimte() {
        Lift l = new Lift();
        assertEquals(0, l.posX);
    }
}
