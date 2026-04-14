import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;
import Controller.*;

public class SimulatieTest {

    @Test
    void testHotelControllerAanmaken() {
        HotelController hc = new HotelController();
        assertNotNull(hc.getHotel());
        assertNotNull(hc.getLayoutController());
    }

    @Test
    void testLayoutLadenViaController() {
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().laadVanBestand("layout.json", "layout.json");
        assertTrue(id > 0);
    }

    @Test
    void testHotelSetEnGet() {
        HotelController hc = new HotelController();
        Hotel nieuw = new Hotel();
        hc.setHotel(nieuw);
        assertEquals(nieuw, hc.getHotel());
    }

    @Test
    void testHeeftLayoutNaLaden() {
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().laadVanBestand("layout.json", "layout.json");
        hc.setHotel(hc.getLayoutController().getHotel(id));
        assertTrue(hc.heeftLayout());
    }

    @Test
    void testHeeftGeenLayoutBijLeegHotel() {
        HotelController hc = new HotelController();
        // nieuw hotel zonder layout -> heeftLayout() geeft false
        hc.setHotel(new Hotel());
        assertFalse(hc.heeftLayout());
    }
}
