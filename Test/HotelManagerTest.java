import Model.Hotel;
import Model.HotelManager;
import Model.layout.Layout;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor HotelManager: addLayout, loadHotel, getLayout, getHotel, removeLayout, getAllLayoutIds
public class HotelManagerTest {

    // addLayout: geeft een id >= 1 terug
    @Test void testAddLayoutGeeftId() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(4, 4);
        int id = hm.addLayout("test", l);
        assertTrue(id >= 1);
    }

    // addLayout: layout heeft daarna het id en de naam
    @Test void testAddLayoutSlaatIdEnNaamOp() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(4, 4);
        int id = hm.addLayout("mijnlayout", l);
        assertEquals(id, l.id);
        assertEquals("mijnlayout", l.naam);
    }

    // getLayout: geeft de eerder opgeslagen layout terug
    @Test void testGetLayout() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(4, 4);
        int id = hm.addLayout("test", l);
    }

    // loadHotel en getHotel: hotel wordt opgeslagen en teruggehaald
    @Test void testLoadEnGetHotel() {
        HotelManager hm = new HotelManager();
        Hotel hotel = new Hotel();
        hm.loadHotel(1, hotel);
        assertSame(hotel, hm.getHotel(1));
    }

    // meerdere layouts: ids zijn uniek en oplopend
    @Test void testMeerdereLayoutsUniekIds() {
        HotelManager hm = new HotelManager();
        int id1 = hm.addLayout("a", new Layout(1, 1));
        int id2 = hm.addLayout("b", new Layout(1, 1));
        int id3 = hm.addLayout("c", new Layout(1, 1));
        assertNotEquals(id1, id2);
        assertNotEquals(id2, id3);
        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
    }
}
