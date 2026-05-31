import Model.layout.LayoutParser;
import Model.layout.ParseResultaat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LayoutParserTest {

    // Ik laad een geldig layoutbestand; ik verwacht dat het resultaat bestaat en geldige afmetingen bevat.
    @Test void testLaadGeldigBestand() {
        ParseResultaat r = new LayoutParser().laad("layout.json");
        assertNotNull(r);
        assertTrue(r.breedte > 0);
        assertTrue(r.hoogte > 0);
        assertFalse(r.ruimteData.isEmpty());
    }

    // Ik laad een ongeldig bestand; ik verwacht dat de parser null teruggeeft.
    @Test void testLaadOngeldigBestandGeeftNull() {
        ParseResultaat r = new LayoutParser().laad("bestaat_niet.json");
        assertNull(r);
    }

    // Ik laad het standaard layoutbestand; ik verwacht dat het aantal ingelezen ruimtes overeenkomt met de JSON.
    @Test void testAantalRuimtes() {
        ParseResultaat r = new LayoutParser().laad("layout.json");
        assertNotNull(r);
        assertEquals(29, r.ruimteData.size());
    }
}
