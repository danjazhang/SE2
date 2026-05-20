import Model.ruimte.Restaurant;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    // Ik maak een nieuw restaurant aan; ik verwacht dat de capaciteit standaard 0 is.
    @Test void testConstructor() {
        Restaurant r = new Restaurant();
        assertEquals(0, r.capaciteit);
    }

    // Ik stuur een NEED_FOOD event; ik verwacht dat het restaurant dit zonder crash verwerkt.
    @Test void testNeedFoodCrashetNiet() {
        Restaurant r = new Restaurant();
        assertDoesNotThrow(() -> r.onEvent(new HotelEvent(1, HotelEventType.NEED_FOOD, 1, -1)));
    }

    // Ik stuur een NONE event; ik verwacht dat dit zonder crash verwerkt wordt.
    @Test void testNoneCrashetNiet() {
        Restaurant r = new Restaurant();
        assertDoesNotThrow(() -> r.onEvent(new HotelEvent(1, HotelEventType.NONE, -1, -1)));
    }

    // Ik registreer een gast en stuur daarna een NONE event na de eetduur;
    // ik verwacht dat het restaurant aangeeft dat de gast klaar is.
    @Test void testGastKlaarNaEetduur() {
        boolean[] logged = {false};
        Restaurant r = new Restaurant(bericht -> logged[0] = true);
        r.registreerGast(1, 1);
        r.onEvent(new HotelEvent(21, HotelEventType.NONE, -1, -1));
        assertTrue(logged[0]);
    }
}
