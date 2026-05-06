import Model.layout.LayoutParser;
import Model.layout.ParseResultaat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor LayoutParser: ik test het lezen van layout.json naar ParseResultaat.
public class LayoutParserTest {

    // Ik laad een geldig bestand; ik verwacht breedte, hoogte en ruimtedata.
    @Test void testLaadGeldigBestand() {
        ParseResultaat r = new LayoutParser().laad("layout.json");
        assertNotNull(r);
        assertTrue(r.breedte > 0);
        assertTrue(r.hoogte > 0);
        assertFalse(r.ruimteData.isEmpty());
    }

    // Ik laad een ontbrekend bestand; ik verwacht null.
    @Test void testLaadOngeldigBestandGeeftNull() {
        ParseResultaat r = new LayoutParser().laad("bestaat_niet.json");
        assertNull(r);
    }

    // Ik laad het testbestand; ik verwacht het afgesproken aantal ruimtes.
    @Test void testAantalRuimtes() {
        ParseResultaat r = new LayoutParser().laad("layout.json");
        assertNotNull(r);
        assertEquals(29, r.ruimteData.size());
    }
}
