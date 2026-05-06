import Model.HotelManager;
import Model.Hotel;
import Model.layout.Layout;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor HotelManager: ik test opslaan, ophalen en verwijderen van layouts/hotels.
public class HotelManagerTest {

    // Ik voeg een layout toe; ik verwacht dat ik die met het id kan terugvinden.
    @Test void testAddLayout() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(3, 3);
        int id = hm.addLayout("test", l);
        assertEquals(l, hm.getLayout(id));
    }

    // Ik koppel een hotel aan een layout-id; ik verwacht dat getHotel hetzelfde hotel teruggeeft.
    @Test void testLoadEnGetHotel() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(3, 3);
        int id = hm.addLayout("test", l);
        Hotel h = new Hotel();
        hm.loadHotel(id, h);
        assertEquals(h, hm.getHotel(id));
    }

    // Ik verwijder een layout; ik verwacht dat getLayout daarna null teruggeeft.
    @Test void testRemoveLayout() {
        HotelManager hm = new HotelManager();
        Layout l = new Layout(3, 3);
        int id = hm.addLayout("test", l);
        hm.removeLayout(id);
        assertNull(hm.getLayout(id));
    }

    // Ik voeg twee layouts toe; ik verwacht twee layout-id's terug.
    @Test void testGetAllLayoutIds() {
        HotelManager hm = new HotelManager();
        hm.addLayout("a", new Layout(2, 2));
        hm.addLayout("b", new Layout(2, 2));
        assertEquals(2, hm.getAllLayoutIds().size());
    }

    // Ik voeg twee layouts toe; ik verwacht dat het tweede id groter is.
    @Test void testIdOplopend() {
        HotelManager hm = new HotelManager();
        int id1 = hm.addLayout("a", new Layout(2, 2));
        int id2 = hm.addLayout("b", new Layout(2, 2));
        assertTrue(id2 > id1);
    }
}
