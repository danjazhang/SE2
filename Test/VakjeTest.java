import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.Kamer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VakjeTest {

    @Test void testNieuwVakjeLeeg() { assertTrue(new Vakje().krijgPersonen().isEmpty()); }

    @Test void testVoegPersoonToe() {
        Vakje v = new Vakje();
        Gast g = new Gast(1, 2);
        v.voegPersoonToe(g);
        assertEquals(1, v.krijgPersonen().size());
    }

    @Test void testVerwijderPersoon() {
        Vakje v = new Vakje();
        Gast g = new Gast(1, 2);
        v.voegPersoonToe(g);
        v.verwijderPersoon(g);
        assertEquals(0, v.krijgPersonen().size());
    }

    @Test void testSetRuimte() {
        Vakje v = new Vakje();
        Kamer k = new Kamer();
        v.setRuimte(k);
        assertEquals(k, v.getRuimte());
    }

    @Test void testCoordinaten() {
        Vakje v = new Vakje();
        v.x = 3; v.y = 4;
        assertEquals(3, v.getX());
        assertEquals(4, v.getY());
    }

    @Test void testMeerderePersoonOpVakje() {
        Vakje v = new Vakje();
        v.voegPersoonToe(new Gast(1, 2));
        v.voegPersoonToe(new Gast(2, 3));
        assertEquals(2, v.krijgPersonen().size());
    }
}