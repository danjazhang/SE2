import Model.*;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantTest {

    // hulpklasse om logs op te vangen in de test
    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override
        public void log(String bericht) { logs.add(bericht); }
    }

    // hulpklasse om een nep HotelEvent aan te maken
    static HotelEvent maakEvent(HotelEventType type, int tijd, int gastId) {
        return new HotelEvent(tijd, type, gastId, -1);
    }

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

    // NEED_FOOD event wordt gelogd
    @Test
    void testNeedFoodWordtGelogd() {
        TestLogger logger = new TestLogger();
        Restaurant r = new Restaurant(logger);
        r.onEvent(maakEvent(HotelEventType.NEED_FOOD, 60, 1));
        assertTrue(logger.logs.get(0).contains("bestelt eten"));
        assertTrue(logger.logs.get(0).contains("gast 1"));
    }

    // gast is klaar na EETDUUR ticks
    @Test
    void testGastKlaarNaEetduur() {
        TestLogger logger = new TestLogger();
        Restaurant r = new Restaurant(logger);
        r.onEvent(maakEvent(HotelEventType.NEED_FOOD, 60, 1));
        // EETDUUR = 20, dus klaar op tick 80
        r.onEvent(maakEvent(HotelEventType.NONE, 80, 0));
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // gast is nog niet klaar voor de eindtijd
    @Test
    void testGastNogNietKlaar() {
        TestLogger logger = new TestLogger();
        Restaurant r = new Restaurant(logger);
        r.onEvent(maakEvent(HotelEventType.NEED_FOOD, 60, 1));
        r.onEvent(maakEvent(HotelEventType.NONE, 70, 0));
        assertFalse(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }
}
