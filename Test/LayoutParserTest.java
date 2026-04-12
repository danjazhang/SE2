import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class LayoutParserTest {

    // na het laden moet het resultaat niet null zijn
    @Test
    void testLaadGeeftResultaatTerug() {
        LayoutParser parser = new LayoutParser();
        ParseResultaat resultaat = parser.laad("layout.json");
        assertNotNull(resultaat);
    }

    // na het laden moet het resultaat de juiste breedte en hoogte hebben
    @Test
    void testLaadVultBreedteEnHoogte() {
        LayoutParser parser = new LayoutParser();
        ParseResultaat resultaat = parser.laad("layout.json");
        // layout.json heeft een hotel van 6 breed en 8 hoog
        assertEquals(6, resultaat.breedte);
        assertEquals(8, resultaat.hoogte);
    }

    // na het laden moet de ruimteData lijst gevuld zijn
    @Test
    void testLaadVultRuimteData() {
        LayoutParser parser = new LayoutParser();
        ParseResultaat resultaat = parser.laad("layout.json");
        // layout.json heeft 29 ruimtes
        assertEquals(29, resultaat.ruimteData.size());
    }

    // bij een ongeldig bestandspad moet laad() null teruggeven en niet crashen
    @Test
    void testLaadMetOngeldigPadGeeftNull() {
        LayoutParser parser = new LayoutParser();
        // ongeldig pad moet null teruggeven en niet crashen
        ParseResultaat resultaat = parser.laad("bestaat_niet.json");
        assertNull(resultaat);
    }

    // elk json object in ruimteData moet een _posX veld hebben
    @Test
    void testLaadVoegdPositieToe() {
        LayoutParser parser = new LayoutParser();
        ParseResultaat resultaat = parser.laad("layout.json");
        // elk object moet een _posX veld hebben dat door de parser is toegevoegd
        assertTrue(resultaat.ruimteData.get(0).has("_posX"));
        assertTrue(resultaat.ruimteData.get(0).has("_posY"));
    }
}
