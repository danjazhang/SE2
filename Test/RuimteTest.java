import Model.ruimte.Ruimte;
import Model.persoon.Persoon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Ruimte: ik test basisvelden, aanwezigen en ingang.
public class RuimteTest {

    // Ik maak een lege Ruimte; ik verwacht standaardpositie 0,0.
    @Test void testLegeConstrutor() {
        Ruimte r = new Ruimte();
        assertEquals(0, r.posX);
        assertEquals(0, r.posY);
    }

    // Ik maak een Ruimte met positie en afmetingen; ik verwacht dat die waarden opgeslagen zijn.
    @Test void testConstructorMetPositie() {
        Ruimte r = new Ruimte(2, 3, 4, 5);
        assertEquals(2, r.posX);
        assertEquals(3, r.posY);
        assertEquals(4, r.breedte);
        assertEquals(5, r.hoogte);
    }

    // Ik laat een Persoon een Ruimte betreden en verlaten; ik verwacht dat de aanwezigenlijst verandert.
    @Test void testBetreedEnVerlaat() {
        Ruimte r = new Ruimte(1, 1, 2, 2);
        Persoon p = new Persoon();
        r.betreed(p);
        assertTrue(r.getAanwezigen().contains(p));
        r.verlaat(p);
        assertFalse(r.getAanwezigen().contains(p));
    }

    // Ik zet een ingang; ik verwacht dat krijgIngang dezelfde coordinaten teruggeeft.
    @Test void testSetEnKrijgIngang() {
        Ruimte r = new Ruimte();
        r.setIngang(3, 4);
        assertArrayEquals(new int[]{3, 4}, r.krijgIngang());
    }

    // Ik zet een positie; ik verwacht dat getX en getY die positie teruggeven.
    @Test void testSetPositie() {
        Ruimte r = new Ruimte();
        r.setPositie(5, 6);
        assertEquals(5, r.getX());
        assertEquals(6, r.getY());
    }

    // Ik zet afmetingen; ik verwacht dat getBreedte en getHoogte die waarden teruggeven.
    @Test void testSetAfmetingen() {
        Ruimte r = new Ruimte();
        r.setAfmetingen(3, 4);
        assertEquals(3, r.getBreedte());
        assertEquals(4, r.getHoogte());
    }
}
