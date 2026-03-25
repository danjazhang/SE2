import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

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

    // type veld begint leeg en kan gezet worden
    @Test
    void testType() {
        Ruimte r = new Ruimte();
        assertEquals("", r.type);
        r.type = "Lift";
        assertEquals("Lift", r.type);
    }
}
