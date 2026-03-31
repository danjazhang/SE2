import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class ModelListenerTest {

    // hotel notificeert listener als layout geladen wordt
    @Test
    void testHotelNotificeertBijLadenLayout() {
        Hotel hotel = new Hotel();
        boolean[] genotificeerd = {false};

        hotel.voegListenerToe(() -> genotificeerd[0] = true);
        hotel.laadLayoutBestand("layout.json");

        assertTrue(genotificeerd[0]);
    }
}
