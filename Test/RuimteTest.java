import Model.persoon.Persoon;
import Model.ruimte.Ruimte;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RuimteTest {

    // alle int-velden beginnen op 0 na aanmaken
    @Test
    void testConstructor() {
        Ruimte r = new Ruimte();
        assertEquals(0, r.posX);
        assertEquals(0, r.posY);
        assertEquals(0, r.breedte);
        assertEquals(0, r.hoogte);
    }

    // ingang kan gezet worden en wordt correct teruggegeven
    @Test
    void testSetEnKrijgIngang() {
        Ruimte r = new Ruimte();
        r.setIngang(2, 3);
        int[] ingang = r.krijgIngang();
        assertEquals(2, ingang[0]);
        assertEquals(3, ingang[1]);
    }


    @Test
    void testBetreedEnVerlaat() {
        Ruimte r = new Ruimte(1, 1, 2, 2);
        Persoon p = new Persoon();
        r.betreed(p);
        r.verlaat(p);
    }
}
