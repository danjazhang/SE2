import Controller.LayoutController;
import Model.Hotel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor LayoutController: maakHandmatigeLayout, getHotel, getHotelManager
public class LayoutControllerTest {

    // maakHandmatigeLayout: geeft een geldig id terug
    @Test void testMaakHandmatigeLayoutGeeftId() {
        LayoutController lc = new LayoutController();
        int id = lc.maakHandmatigeLayout("test", 5, 4);
        assertTrue(id >= 1);
    }

    // maakHandmatigeLayout: hotel heeft de juiste afmetingen
    @Test void testMaakHandmatigeLayoutAfmetingen() {
        LayoutController lc = new LayoutController();
        int id = lc.maakHandmatigeLayout("test", 6, 5);
        Hotel hotel = lc.getHotel(id);
        assertEquals(6, hotel.breedte);
        assertEquals(5, hotel.hoogte);
    }

    // maakHandmatigeLayout: hotel heeft een layout
    @Test void testMaakHandmatigeLayoutHeeftLayout() {
        LayoutController lc = new LayoutController();
        int id = lc.maakHandmatigeLayout("test", 4, 4);
        assertNotNull(lc.getHotel(id).layout);
    }

    // getHotel: geeft null voor onbekend id
    @Test void testGetHotelOnbekendId() {
        assertNull(new LayoutController().getHotel(999));
    }

    // meerdere layouts: ids zijn uniek
    @Test void testMeerdereLayoutsUniekIds() {
        LayoutController lc = new LayoutController();
        int id1 = lc.maakHandmatigeLayout("a", 3, 3);
        int id2 = lc.maakHandmatigeLayout("b", 4, 4);
        assertNotEquals(id1, id2);
        assertNotSame(lc.getHotel(id1), lc.getHotel(id2));
    }

    // setLogger: geen crash
    @Test void testSetLogger() {
        assertDoesNotThrow(() -> new LayoutController().setLogger(bericht -> {}));
    }

    // laadVanBestand met niet-bestaand bestand: geeft -1 terug
    @Test void testLaadVanNietBestaandBestand() {
        LayoutController lc = new LayoutController();
        int result = lc.laadVanBestand("niet_bestaand.json", "test");
        assertEquals(-1, result);
    }
}
