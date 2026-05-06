import Controller.EventController;
import Controller.HotelController;
import Controller.SimulatieController;
import hotelevents.HotelEventManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor SimulatieController: ik test de koppeling met HotelEventManager.
public class SimulatieControllerTest {

    // Ik gebruik een testmanager; ik verwacht dat er geen echte scenario-run nodig is.
    private HotelEventManager manager = new HotelEventManager(true);
    // Ik geef een EventController mee; ik verwacht dat de constructor zijn dependency krijgt.
    private EventController ec = new EventController(manager);
    // Ik geef een HotelController mee; ik verwacht dat de constructor zijn dependency krijgt.
    private HotelController hc = new HotelController();

    // Ik maak een SimulatieController; ik verwacht geen exception.
    @Test void testConstructor() {
        assertDoesNotThrow(() -> new SimulatieController(manager, ec, hc));
    }

    // Ik start zonder scenario; ik verwacht nu een RuntimeException als randgeval.
    @Test void testStartGooidExceptionZonderScenario() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertThrows(RuntimeException.class, () -> sc.start());
    }
}
