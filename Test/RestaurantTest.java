import Model.ruimte.Restaurant;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    @Test void testConstructor() {
        Restaurant r = new Restaurant();
        assertEquals(0, r.capaciteit);
    }

    @Test void testNeedFoodCrashetNiet() {
        Restaurant r = new Restaurant();
        assertDoesNotThrow(() -> r.onEvent(new HotelEvent(1, HotelEventType.NEED_FOOD, 1, -1)));
    }

    @Test void testNoneCrashetNiet() {
        Restaurant r = new Restaurant();
        assertDoesNotThrow(() -> r.onEvent(new HotelEvent(1, HotelEventType.NONE, -1, -1)));
    }

    @Test void testGastKlaarNaEetduur() {
        boolean[] logged = {false};
        Restaurant r = new Restaurant(bericht -> logged[0] = true);
        r.onEvent(new HotelEvent(1, HotelEventType.NEED_FOOD, 1, -1));
        r.onEvent(new HotelEvent(21, HotelEventType.NONE, -1, -1));
        assertTrue(logged[0]);
    }
}