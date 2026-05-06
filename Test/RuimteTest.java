import Model.ruimte.Ruimte;
import Model.persoon.Gast;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RuimteTest {

    @Test void testLegeConstrutor() {
        Ruimte r = new Ruimte();
        assertEquals(0, r.posX);
        assertEquals(0, r.posY);
    }

    @Test void testConstructorMetPositie() {
        Ruimte r = new Ruimte(2, 3, 4, 5);
        assertEquals(2, r.posX);
        assertEquals(3, r.posY);
        assertEquals(4, r.breedte);
        assertEquals(5, r.hoogte);
    }

    @Test void testBetreedEnVerlaat() {
        Ruimte r = new Ruimte(1, 1, 2, 2);
        // Gast gebruiken want Persoon is abstract
        Gast p = new Gast(1, 1);
        r.betreed(p);
        assertTrue(r.getAanwezigen().contains(p));
        r.verlaat(p);
        assertFalse(r.getAanwezigen().contains(p));
    }

    @Test void testSetEnKrijgIngang() {
        Ruimte r = new Ruimte();
        r.setIngang(3, 4);
        assertArrayEquals(new int[]{3, 4}, r.krijgIngang());
    }

    @Test void testSetPositie() {
        Ruimte r = new Ruimte();
        r.setPositie(5, 6);
        assertEquals(5, r.getX());
        assertEquals(6, r.getY());
    }

    @Test void testSetAfmetingen() {
        Ruimte r = new Ruimte();
        r.setAfmetingen(3, 4);
        assertEquals(3, r.getBreedte());
        assertEquals(4, r.getHoogte());
    }
}