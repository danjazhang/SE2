import Model.layout.LayoutParser;
import Model.layout.ParseResultaat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LayoutParserTest {

    // laadGeldigBestand: resultaat is niet null en heeft afmetingen
    @Test void testLaadGeldigBestand() {
        ParseResultaat r = new LayoutParser().laad("layout.json");
        assertNotNull(r);
        assertTrue(r.breedte > 0);
        assertTrue(r.hoogte > 0);
        assertFalse(r.ruimteData.isEmpty());
    }

    // laadOngeldigBestand: geeft null terug
    @Test void testLaadOngeldigBestandGeeftNull() {
        ParseResultaat r = new LayoutParser().laad("bestaat_niet.json");
        assertNull(r);
    }

    // aantal ruimtes klopt met het json bestand
    @Test void testAantalRuimtes() {
        ParseResultaat r = new LayoutParser().laad("layout.json");
        assertNotNull(r);
        assertEquals(29, r.ruimteData.size());
    }
}
