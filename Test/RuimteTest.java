import Model.ruimte.Ruimte;
import Model.persoon.Gast;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RuimteTest {

    // Ik maak een lege ruimte aan; ik verwacht dat de standaardpositie op 0,0 staat.
    @Test void testLegeConstrutor() {
        Ruimte r = new Ruimte();
        assertEquals(0, r.posX);
        assertEquals(0, r.posY);
    }

    // Ik maak een ruimte met expliciete positie en afmetingen; ik verwacht dat die waarden correct bewaard worden.
    @Test void testConstructorMetPositie() {
        Ruimte r = new Ruimte(2, 3, 4, 5);
        assertEquals(2, r.posX);
        assertEquals(3, r.posY);
        assertEquals(4, r.breedte);
        assertEquals(5, r.hoogte);
    }

    // Ik laat een gast een ruimte betreden en verlaten; ik verwacht dat de aanwezigheidslijst mee verandert.
    @Test void testBetreedEnVerlaat() {
        Ruimte r = new Ruimte(1, 1, 2, 2);
        // Gast gebruiken want Persoon is abstract
        Gast p = new Gast(1, 1);
        r.betreed(p);
        assertTrue(r.getAanwezigen().contains(p));
        r.verlaat(p);
        assertFalse(r.getAanwezigen().contains(p));
    }

    // Ik stel een ingang in op een ruimte; ik verwacht dat ik dezelfde ingang terugkrijg.
    @Test void testSetEnKrijgIngang() {
        Ruimte r = new Ruimte();
        r.setIngang(3, 4);
        assertArrayEquals(new int[]{3, 4}, r.krijgIngang());
    }

    // Ik stel een nieuwe positie in op een ruimte; ik verwacht dat de getters die positie teruggeven.
    @Test void testSetPositie() {
        Ruimte r = new Ruimte();
        r.setPositie(5, 6);
        assertEquals(5, r.getX());
        assertEquals(6, r.getY());
    }

    // Ik stel nieuwe afmetingen in op een ruimte; ik verwacht dat de getters die afmetingen teruggeven.
    @Test void testSetAfmetingen() {
        Ruimte r = new Ruimte();
        r.setAfmetingen(3, 4);
        assertEquals(3, r.getBreedte());
        assertEquals(4, r.getHoogte());
    }
}
