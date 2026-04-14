import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class VakjeTest {

    @Test
    void testVoegPersoonToe() {
        Vakje vakje = new Vakje();
        Gast gast = new Gast(1, 3);
        vakje.voegPersoonToe(gast);
        assertEquals(1, vakje.krijgPersonen().size());
    }

    @Test
    void testVerwijderPersoon() {
        Vakje vakje = new Vakje();
        Gast gast = new Gast(1, 3);
        vakje.voegPersoonToe(gast);
        vakje.verwijderPersoon(gast);
        assertEquals(0, vakje.krijgPersonen().size());
    }

    @Test
    void testConstructor() {
        Vakje v = new Vakje();
        assertNotNull(v.krijgPersonen());
        assertTrue(v.krijgPersonen().isEmpty());
    }

    @Test
    void testSetEnGetRuimte() {
        Vakje v = new Vakje();
        Ruimte r = new Ruimte();
        v.setRuimte(r);
        assertEquals(r, v.getRuimte());
    }

    @Test
    void testCoordinaten() {
        Vakje v = new Vakje();
        v.x = 3;
        v.y = 5;
        assertEquals(3, v.getX());
        assertEquals(5, v.getY());
    }

    @Test
    void testKrijgPersonenIsKopie() {
        Vakje v = new Vakje();
        Persoon p = new Persoon();
        v.voegPersoonToe(p);
        v.krijgPersonen().clear();
        assertEquals(1, v.krijgPersonen().size());
    }
}
