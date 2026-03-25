import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class RestaurantTest {

    // capaciteit begint op 0 en gasten is null na aanmaken
    @Test
    void testConstructor() {
        Restaurant r = new Restaurant();
        assertEquals(0, r.capaciteit);
        assertNull(r.gasten);
    }

    // restaurant erft van Ruimte, posX en posY beginnen op 0
    @Test
    void testErftVanRuimte() {
        Restaurant r = new Restaurant();
        assertEquals(0, r.posX);
        assertEquals(0, r.posY);
    }

    // capaciteit kan handmatig gezet worden
    @Test
    void testZetCapaciteit() {
        Restaurant r = new Restaurant();
        r.capaciteit = 50;
        assertEquals(50, r.capaciteit);
    }
}
