import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Controller.*;
import Model.*;

// Test de SimulatieController ipv een niet-bestaande Klok klasse
public class KlokTest {

    @Test
    void testSimulatieControllerAanmaken() {
        HotelController hc = new HotelController();
        assertNotNull(hc);
    }

    @Test
    void testHotelControllerHeeftGeenLayoutBijStart() {
        HotelController hc = new HotelController();
        assertFalse(hc.heeftLayout());
    }

    @Test
    void testHotelControllerHeeftLayoutNaLaden() {
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().laadVanBestand("layout.json", "layout.json");
        Hotel hotel = hc.getLayoutController().getHotel(id);
        hc.setHotel(hotel);
        assertTrue(hc.heeftLayout());
    }

    @Test
    void testLayoutControllerGeeftMinusEenBijFout() {
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().laadVanBestand("bestaat_niet.json", "x");
        assertEquals(-1, id);
    }

    @Test
    void testHotelControllerGetHotel() {
        HotelController hc = new HotelController();
        assertNotNull(hc.getHotel());
    }
}
