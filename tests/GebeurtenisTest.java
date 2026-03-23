import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GebeurtenisTest {

    // controleer dat tijd en type correct worden opgeslagen via de constructor
    @Test
    void testConstructor() {
        Gebeurtenis g = new Gebeurtenis(10, "checkin");
        assertEquals(10, g.tijd);
        assertEquals("checkin", g.type);
    }

    // twee verschillende gebeurtenissen mogen niet dezelfde tijd hebben
    @Test
    void testVerschillendeTijden() {
        Gebeurtenis g1 = new Gebeurtenis(5, "checkin");
        Gebeurtenis g2 = new Gebeurtenis(20, "schoonmaak");
        assertNotEquals(g1.tijd, g2.tijd);
    }

    // controleer dat alle vaste type-strings de juiste waarde hebben
    @Test
    void testTypeWaarden() {
        Gebeurtenis g = new Gebeurtenis(0, "checkin");
        assertEquals("checkin", g.checkin);
        assertEquals("checkout", g.checkout);
        assertEquals("schoonmaak", g.schoonmaak);
        assertEquals("brandalarm", g.brandalarm);
        assertEquals("drukte", g.drukte);
    }

    // het type veld moet exact het meegegeven type bevatten, niet een vaste string
    @Test
    void testTypeIsHetzelfdeAlsMeegegeven() {
        Gebeurtenis g = new Gebeurtenis(5, "brandalarm");
        assertEquals("brandalarm", g.type);
    }
}
