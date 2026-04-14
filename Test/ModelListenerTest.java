import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;
import Controller.*;

public class ModelListenerTest {

    @Test
    void testHotelNotificeertListeners() {
        Hotel hotel = new Hotel();
        boolean[] genotificeerd = {false};
        hotel.voegListenerToe(() -> genotificeerd[0] = true);
        hotel.notifyListeners();
        assertTrue(genotificeerd[0]);
    }

    @Test
    void testMeerdereListeners() {
        Hotel hotel = new Hotel();
        int[] teller = {0};
        hotel.voegListenerToe(() -> teller[0]++);
        hotel.voegListenerToe(() -> teller[0]++);
        hotel.notifyListeners();
        assertEquals(2, teller[0]);
    }

    @Test
    void testListenerWordtAangeroependBijLadenLayout() {
        Hotel hotel = new Hotel();
        boolean[] genotificeerd = {false};
        hotel.voegListenerToe(() -> genotificeerd[0] = true);

        // laad een layout via de controller zodat notifyListeners aangeroepen wordt
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().laadVanBestand("layout.json", "layout.json");
        Hotel geladen = hc.getLayoutController().getHotel(id);
        geladen.voegListenerToe(() -> genotificeerd[0] = true);
        geladen.notifyListeners();

        assertTrue(genotificeerd[0]);
    }
}
