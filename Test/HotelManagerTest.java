import Model.HotelManager;
import Model.Hotel;
import Model.layout.Layout;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotelManagerTest {

    // Ik voeg een layout toe; ik verwacht dat ik die via het teruggegeven id weer kan ophalen.
    @Test void testAddLayout() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(3, 3);
        int id = hm.addLayout("test", l);
        assertEquals(l, hm.getLayout(id));
    }

    // Ik laad een hotel op een bestaand layout-id; ik verwacht dat ik dat hotel daarna weer kan ophalen.
    @Test void testLoadEnGetHotel() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(3, 3);
        int id = hm.addLayout("test", l);
        Hotel h = new Hotel();
        hm.loadHotel(id, h);
        assertEquals(h, hm.getHotel(id));
    }

    // Ik verwijder een layout; ik verwacht dat die daarna niet meer opgehaald kan worden.
    @Test void testRemoveLayout() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(3, 3);
        int id = hm.addLayout("test", l);
        hm.removeLayout(id);
        assertNull(hm.getLayout(id));
    }

    // Ik voeg twee layouts toe; ik verwacht dat de lijst met ids daarna twee elementen bevat.
    @Test void testGetAllLayoutIds() {
        HotelManager hm = new HotelManager();
        hm.addLayout("a", new Layout(2, 2));
        hm.addLayout("b", new Layout(2, 2));
        assertEquals(2, hm.getAllLayoutIds().size());
    }

    // Ik voeg meerdere layouts toe; ik verwacht dat de ids oplopend worden uitgedeeld.
    @Test void testIdOplopend() {
        HotelManager hm = new HotelManager();
        int id1 = hm.addLayout("a", new Layout(2, 2));
        int id2 = hm.addLayout("b", new Layout(2, 2));
        assertTrue(id2 > id1);
    }
}
