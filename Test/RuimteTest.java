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
        assertEquals(0, r.ingangX);
        assertEquals(0, r.ingangY);
    }
}
