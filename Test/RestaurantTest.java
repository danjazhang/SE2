import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class RestaurantTest {

    @Test
    void testConstructor() {
        Restaurant r = new Restaurant();
        assertEquals(0, r.capaciteit);
        assertNull(r.gasten);
    }

    @Test
    void testErftVanRuimte() {
        Restaurant r = new Restaurant();
        assertEquals(0, r.posX);
        assertEquals(0, r.posY);
    }

    @Test
    void testZetCapaciteit() {
        Restaurant r = new Restaurant();
        r.capaciteit = 50;
        assertEquals(50, r.capaciteit);
    }

    @Test
    void testBetreedRestaurantCrashetNiet() {
        Restaurant r = new Restaurant();
        assertDoesNotThrow(() -> r.betreedRestaurant());
    }

    @Test
    void testVerlaatRestaurantCrashetNiet() {
        Restaurant r = new Restaurant();
        assertDoesNotThrow(() -> r.verlaatRestaurant());
    }

    @Test
    void testIsVolCrashetNiet() {
        Restaurant r = new Restaurant();
        assertDoesNotThrow(() -> r.isVol());
    }
}
