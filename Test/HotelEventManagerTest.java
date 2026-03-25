import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;
import hotelevents.*;

public class HotelEventManagerTest {

    // register voegt een listener toe en fire roept notify aan
    @Test
    void testFireNotificeertListeners() {
        HotelEventManager manager = new HotelEventManager();
        boolean[] ontvangen = {false};

        HotelEventListener listener = evt -> ontvangen[0] = true;
        manager.register(listener);
        manager.fire(new HotelEvent(1, HotelEventType.CHECK_IN, 10, 1));

        assertTrue(ontvangen[0]);
    }

    // meerdere listeners worden allemaal genotificeerd
    @Test
    void testMeerdereListeners() {
        HotelEventManager manager = new HotelEventManager();
        int[] teller = {0};

        manager.register(evt -> teller[0]++);
        manager.register(evt -> teller[0]++);
        manager.fire(new HotelEvent(1, HotelEventType.CHECK_IN, 10, 1));

        assertEquals(2, teller[0]);
    }
}
