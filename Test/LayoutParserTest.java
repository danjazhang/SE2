import Model.layout.LayoutParser;
import Model.layout.ParseResultaat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LayoutParserTest {

    @Test void testLaadGeldigBestand() {
        ParseResultaat r = new LayoutParser().laad("layout.json");
        assertNotNull(r);
        assertTrue(r.breedte > 0);
        assertTrue(r.hoogte > 0);
        assertFalse(r.ruimteData.isEmpty());
    }

    @Test void testLaadOngeldigBestandGeeftNull() {
        ParseResultaat r = new LayoutParser().laad("bestaat_niet.json");
        assertNull(r);
    }

    @Test void testAantalRuimtes() {
        ParseResultaat r = new LayoutParser().laad("layout.json");
        assertNotNull(r);
        assertEquals(29, r.ruimteData.size());
    }
}