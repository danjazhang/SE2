import Model.ruimte.Restaurant;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Restaurant: ik test NEED_FOOD, NONE-ticks en eindtijdlogica.
public class RestaurantTest {

    // Ik maak een Restaurant; ik verwacht dat capaciteit standaard 0 is.
    @Test void testConstructor() {
        Restaurant r = new Restaurant();
        assertEquals(0, r.capaciteit);
    }

    // Ik stuur NEED_FOOD; ik verwacht dat dit zonder exception verwerkt wordt.
    @Test void testNeedFoodCrashetNiet() {
        Restaurant r = new Restaurant();
        assertDoesNotThrow(() -> r.onEvent(new HotelEvent(1, HotelEventType.NEED_FOOD, 1, -1)));
    }

    // Ik stuur NONE zonder actieve gasten; ik verwacht geen exception.
    @Test void testNoneCrashetNiet() {
        Restaurant r = new Restaurant();
        assertDoesNotThrow(() -> r.onEvent(new HotelEvent(1, HotelEventType.NONE, -1, -1)));
    }

    // Ik laat een Gast eten en stuur later NONE; ik verwacht een klaar-log.
    @Test void testGastKlaarNaEetduur() {
        boolean[] logged = {false};
        Restaurant r = new Restaurant(bericht -> logged[0] = true);
        r.onEvent(new HotelEvent(1, HotelEventType.NEED_FOOD, 1, -1));
        r.onEvent(new HotelEvent(21, HotelEventType.NONE, -1, -1));
        assertTrue(logged[0]);
    }
}
