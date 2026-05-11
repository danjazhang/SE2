import Model.HotelManager;
import Model.Hotel;
import Model.layout.Layout;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotelManagerTest {

    // addLayout: layout wordt opgeslagen en teruggevonden via id
    @Test void testAddLayout() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(3, 3);
        int id = hm.addLayout("test", l);
        assertEquals(l, hm.getLayout(id));
    }

    // loadHotel en getHotel: hotel wordt opgeslagen en teruggevonden via id
    @Test void testLoadEnGetHotel() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(3, 3);
        int id = hm.addLayout("test", l);
        Hotel h = new Hotel();
        hm.loadHotel(id, h);
        assertEquals(h, hm.getHotel(id));
    }

    // removeLayout: layout is weg na verwijderen
    @Test void testRemoveLayout() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(3, 3);
        int id = hm.addLayout("test", l);
        hm.removeLayout(id);
        assertNull(hm.getLayout(id));
    }

    // getAllLayoutIds: geeft alle ids terug
    @Test void testGetAllLayoutIds() {
        HotelManager hm = new HotelManager();
        hm.addLayout("a", new Layout(2, 2));
        hm.addLayout("b", new Layout(2, 2));
        assertEquals(2, hm.getAllLayoutIds().size());
    }

    // ids zijn oplopend
    @Test void testIdOplopend() {
        HotelManager hm = new HotelManager();
        int id1 = hm.addLayout("a", new Layout(2, 2));
        int id2 = hm.addLayout("b", new Layout(2, 2));
        assertTrue(id2 > id1);
    }
}
