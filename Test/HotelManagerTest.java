import Model.Hotel;
import Model.HotelManager;
import Model.Layout;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotelManagerTest {

    @Test
    void testMeerdereLayoutsOpslaan() {
        HotelManager manager = new HotelManager();
        Layout layout1 = new Layout(6, 8);
        Layout layout2 = new Layout(4, 5);
        manager.addLayout("layout1", layout1);
        manager.addLayout("layout2", layout2);
        assertEquals(2, manager.getAllLayoutIds().size());
    }

    @Test
    void testGetLayout() {
        HotelManager manager = new HotelManager();
        Layout layout = new Layout(6, 8);
        int id = manager.addLayout("test", layout);
        assertEquals(layout, manager.getLayout(id));
    }

    @Test
    void testMeerdereHotelsOpslaan() {
        HotelManager manager = new HotelManager();
        Hotel hotel1 = new Hotel();
        Hotel hotel2 = new Hotel();
        manager.loadHotel(1, hotel1);
        manager.loadHotel(2, hotel2);
        assertEquals(hotel1, manager.getHotel(1));
        assertEquals(hotel2, manager.getHotel(2));
    }

    @Test
    void testRemoveLayout() {
        HotelManager manager = new HotelManager();
        Layout layout = new Layout(3, 3);
        int id = manager.addLayout("test", layout);
        manager.removeLayout(id);
        assertNull(manager.getLayout(id));
    }

    @Test
    void testGetLayoutNietBestaand() {
        HotelManager manager = new HotelManager();
        assertNull(manager.getLayout(999));
    }

    @Test
    void testGetHotelNietBestaand() {
        HotelManager manager = new HotelManager();
        assertNull(manager.getHotel(999));
    }

    @Test
    void testLayoutIdWordsOpgehoogd() {
        HotelManager manager = new HotelManager();
        int id1 = manager.addLayout("a", new Layout(1, 1));
        int id2 = manager.addLayout("b", new Layout(1, 1));
        assertNotEquals(id1, id2);
    }

    @Test
    void testLayoutNaamWordtOpgeslagen() {
        HotelManager manager = new HotelManager();
        Layout layout = new Layout(2, 2);
        int id = manager.addLayout("mijnLayout", layout);
        assertEquals("mijnLayout", manager.getLayout(id).naam);
    }

    @Test
    void testLayoutIdWordtOpgeslagen() {
        HotelManager manager = new HotelManager();
        Layout layout = new Layout(2, 2);
        int id = manager.addLayout("test", layout);
        assertEquals(id, manager.getLayout(id).id);
    }
}
