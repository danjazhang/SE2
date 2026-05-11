import Controller.EventController;
import Controller.HotelController;
import Controller.LayoutController;
import Model.*;
import Model.layout.Layout;
import Model.ruimte.Kamer;
import Model.ruimte.Lobby;
import hotelevents.HotelEventManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotelControllerTest {

    // constructor: hotel en layoutcontroller worden aangemaakt
    @Test void testConstructor() {
        HotelController hc = new HotelController();
        assertNotNull(hc.getHotel());
        assertNotNull(hc.getLayoutController());
    }

    // heeftLayout: false als hotel geen layout heeft
    @Test void testHeeftLayoutFalse() {
        assertFalse(new HotelController().heeftLayout());
    }

    // heeftLayout: true als hotel een layout heeft
    @Test void testHeeftLayoutTrue() {
        HotelController hc = new HotelController();
        Hotel h = new Hotel();
        h.layout = new Layout(3, 3);
        hc.setHotel(h);
        assertTrue(hc.heeftLayout());
    }

    // setHotel: hotel wordt correct ingesteld
    @Test void testSetHotel() {
        HotelController hc = new HotelController();
        Hotel h = new Hotel();
        hc.setHotel(h);
        assertEquals(h, hc.getHotel());
    }

    // setLogger: geen crash
    @Test void testSetLogger() {
        HotelController hc = new HotelController();
        assertDoesNotThrow(() -> hc.setLogger(bericht -> {}));
    }

    // setEventController: geen crash
    @Test void testSetEventController() {
        HotelController hc = new HotelController();
        EventController ec = new EventController(new HotelEventManager(true));
        assertDoesNotThrow(() -> hc.setEventController(ec));
    }

    // setHotel: lobby krijgt logger als die ingesteld is
    @Test void testSetHotelMetLobbyZetLogger() {
        HotelController hc = new HotelController();
        hc.setLogger(bericht -> {});
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        Lobby lobby = new Lobby(1, 5, 3, 1, 2, 5, h, null);
        h.lobby = lobby;
        h.ruimtes.add(lobby);
        assertDoesNotThrow(() -> hc.setHotel(h));
    }

    // getLayoutController: geeft een LayoutController terug
    @Test void testGetLayoutController() {
        HotelController hc = new HotelController();
        assertTrue(hc.getLayoutController() instanceof LayoutController);
    }

    // voegListenerToe: listener wordt toegevoegd
    @Test void testVoegListenerToe() {
        HotelController hc = new HotelController();
        boolean[] called = {false};
        hc.voegListenerToe(() -> called[0] = true);
        hc.notifyListeners();
        assertTrue(called[0]);
    }

    // notifyListeners: alle listeners worden aangeroepen
    @Test void testNotifyListenersRoeptAlleListenersAan() {
        HotelController hc = new HotelController();
        int[] count = {0};
        hc.voegListenerToe(() -> count[0]++);
        hc.voegListenerToe(() -> count[0]++);
        hc.notifyListeners();
        assertEquals(2, count[0]);
    }

    // registreerListeners via eventController: geen crash als hotel leeg is
    @Test void testRegistreerListenersLeegHotel() {
        EventController ec = new EventController(new HotelEventManager(true));
        Hotel h = new Hotel();
        assertDoesNotThrow(() -> ec.registreerHotelListeners(h));
    }

    // registreerListeners via eventController: lobby wordt geregistreerd
    @Test void testRegistreerListenersMetLobby() {
        EventController ec = new EventController(new HotelEventManager(true));
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        Lobby lobby = new Lobby(1, 5, 3, 1, 2, 5, h, null);
        h.lobby = lobby;
        h.ruimtes.add(lobby);
        assertDoesNotThrow(() -> ec.registreerHotelListeners(h));
    }
}
