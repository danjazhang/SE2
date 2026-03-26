import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class LayoutParserTest {

    // na het laden moet het hotel de juiste breedte en hoogte hebben
    @Test
    void testLaadVultBreedteEnHoogte() {
        Hotel hotel = new Hotel();
        LayoutParser parser = new LayoutParser();
        parser.laad("layout.json", hotel);
        // layout.json heeft een hotel van 6 breed en 8 hoog
        assertEquals(6, hotel.breedte);
        assertEquals(8, hotel.hoogte);
    }

    // na het laden moet het hotel de juiste hoeveelheid ruimtes hebben
    @Test
    void testLaadVultRuimtes() {
        Hotel hotel = new Hotel();
        LayoutParser parser = new LayoutParser();
        parser.laad("layout.json", hotel);
        // layout.json heeft 29 ruimtes
        assertEquals(29, hotel.ruimtes.size());
    }

    // na het laden moet de layout niet null zijn
    @Test
    void testLaadMaaktLayoutAan() {
        Hotel hotel = new Hotel();
        LayoutParser parser = new LayoutParser();
        parser.laad("layout.json", hotel);
        // layout moet aangemaakt zijn
        assertNotNull(hotel.layout);
    }

    // bij een ongeldig bestandspad moet laad() false teruggeven en niet crashen
    @Test
    void testLaadMetOngeldigPadGeeftFalse() {
        Hotel hotel = new Hotel();
        LayoutParser parser = new LayoutParser();
        // ongeldig pad moet false teruggeven en niet crashen
        boolean resultaat = parser.laad("bestaat_niet.json", hotel);
        assertFalse(resultaat);
    }

    // na het laden moet de eerste ruimte op positie (1,1) een Kamer zijn
    @Test
    void testLaadMaaktJuisteRuimteTypes() {
        Hotel hotel = new Hotel();
        LayoutParser parser = new LayoutParser();
        parser.laad("layout.json", hotel);
        // positie (1,1) moet een Kamer zijn
        assertNotNull(hotel.krijgRuimteOp(1, 1));
        assertTrue(hotel.krijgRuimteOp(1, 1) instanceof Kamer);
    }
}
