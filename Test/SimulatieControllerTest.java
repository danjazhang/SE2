import Controller.EventController;
import Controller.HotelController;
import Controller.SimulatieController;
import hotelevents.HotelEventManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimulatieControllerTest {

    private HotelEventManager manager = new HotelEventManager(true);
    private EventController ec = new EventController(manager);
    private HotelController hc = new HotelController();

    @Test void testConstructor() {
        assertDoesNotThrow(() -> new SimulatieController(manager, ec, hc));
    }

    @Test void testStartGooidExceptionZonderScenario() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertThrows(RuntimeException.class, () -> sc.start());
    }
}
