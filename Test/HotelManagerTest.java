import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class HotelManagerTest {

    // voeg twee layouts toe en controleer dat beide beschikbaar zijn
    @Test
    void testMeerdereLayoutsBeschikbaar() {
        HotelManager manager = new HotelManager();
        Layout layout1 = new Layout(6, 8);
        Layout layout2 = new Layout(4, 5);

        int id1 = manager.addLayout("layout1", layout1);
        int id2 = manager.addLayout("layout2", layout2);

        assertNotNull(manager.getLayout(id1));
        assertNotNull(manager.getLayout(id2));
        assertEquals(2, manager.getAllLayoutIds().size());
    }

    // sla een hotel op en haal het op via id
    @Test
    void testLoadEnGetHotel() {
        HotelManager manager = new HotelManager();
        Hotel hotel = new Hotel();
        manager.loadHotel(1, hotel);
        assertEquals(hotel, manager.getHotel(1));
    }

    // verwijder een layout en controleer dat hij weg is
    @Test
    void testVerwijderLayout() {
        HotelManager manager = new HotelManager();
        Layout layout = new Layout(6, 8);
        int id = manager.addLayout("layout", layout);
        manager.removeLayout(id);
        assertNull(manager.getLayout(id));
    }
}
