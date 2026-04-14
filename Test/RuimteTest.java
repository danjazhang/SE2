import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class RuimteTest {

    @Test
    void testConstructorLeeg() {
        Ruimte r = new Ruimte();
        assertEquals(0, r.posX);
        assertEquals(0, r.posY);
        assertEquals(0, r.breedte);
        assertEquals(0, r.hoogte);
    }

    @Test
    void testConstructorMetPositie() {
        Ruimte r = new Ruimte(1, 2, 3, 4);
        assertEquals(1, r.posX);
        assertEquals(2, r.posY);
        assertEquals(3, r.breedte);
        assertEquals(4, r.hoogte);
    }

    @Test
    void testSetEnKrijgIngang() {
        Ruimte r = new Ruimte();
        r.setIngang(2, 3);
        int[] ingang = r.krijgIngang();
        assertEquals(2, ingang[0]);
        assertEquals(3, ingang[1]);
    }

    @Test
    void testSetPositie() {
        Ruimte r = new Ruimte();
        r.setPositie(5, 6);
        assertEquals(5, r.getX());
        assertEquals(6, r.getY());
    }

    @Test
    void testSetAfmetingen() {
        Ruimte r = new Ruimte();
        r.setAfmetingen(3, 4);
        assertEquals(3, r.getBreedte());
        assertEquals(4, r.getHoogte());
    }

    @Test
    void testBetreedEnVerlaat() {
        Ruimte r = new Ruimte(1, 1, 2, 2);
        Persoon p = new Persoon();
        r.betreed(p);
        assertEquals(1, r.getAanwezigen().size());
        r.verlaat(p);
        assertEquals(0, r.getAanwezigen().size());
    }

    @Test
    void testGetAanwezigenIsKopie() {
        Ruimte r = new Ruimte();
        Persoon p = new Persoon();
        r.betreed(p);
        // kopie aanpassen mag de originele lijst niet veranderen
        r.getAanwezigen().clear();
        assertEquals(1, r.getAanwezigen().size());
    }
}
