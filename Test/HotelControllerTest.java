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

    // Ik maak een nieuwe HotelController aan; ik verwacht dat hotel en layoutcontroller meteen bestaan.
    @Test void testConstructor() {
        HotelController hc = new HotelController();
        assertNotNull(hc.getHotel());
        assertNotNull(hc.getLayoutController());
    }

    // Ik vraag of een leeg hotel al een layout heeft; ik verwacht dat dit false geeft.
    @Test void testHeeftLayoutFalse() {
        assertFalse(new HotelController().heeftLayout());
    }

    // Ik geef het hotel een layout; ik verwacht dat heeftLayout daarna true teruggeeft.
    @Test void testHeeftLayoutTrue() {
        HotelController hc = new HotelController();
        Hotel h = new Hotel();
        h.layout = new Layout(3, 3);
        hc.setHotel(h);
        assertTrue(hc.heeftLayout());
    }

    // Ik stel een hotel in op de controller; ik verwacht dat datzelfde hotel teruggelezen kan worden.
    @Test void testSetHotel() {
        HotelController hc = new HotelController();
        Hotel h = new Hotel();
        hc.setHotel(h);
        assertEquals(h, hc.getHotel());
    }

    // Ik stel een logger in op de controller; ik verwacht dat dit geen crash geeft.
    @Test void testSetLogger() {
        HotelController hc = new HotelController();
        assertDoesNotThrow(() -> hc.setLogger(bericht -> {}));
    }

    // Ik stel een eventcontroller in; ik verwacht dat dit geen crash geeft.
    @Test void testSetEventController() {
        HotelController hc = new HotelController();
        EventController ec = new EventController(new HotelEventManager(true));
        assertDoesNotThrow(() -> hc.setEventController(ec));
    }

    // Ik stel eerst een logger in en daarna een hotel met lobby; ik verwacht dat dit zonder crash lukt.
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

    // Ik vraag de layoutcontroller op; ik verwacht dat die bestaat en van het juiste type is.
    @Test void testGetLayoutController() {
        HotelController hc = new HotelController();
        assertTrue(hc.getLayoutController() instanceof LayoutController);
    }

    // Ik voeg een listener toe en notificeer daarna; ik verwacht dat die listener wordt aangeroepen.
    @Test void testVoegListenerToe() {
        HotelController hc = new HotelController();
        boolean[] called = {false};
        hc.voegListenerToe(() -> called[0] = true);
        hc.notifyListeners();
        assertTrue(called[0]);
    }

    // Ik voeg twee listeners toe en notificeer daarna; ik verwacht dat beide listeners worden aangeroepen.
    @Test void testNotifyListenersRoeptAlleListenersAan() {
        HotelController hc = new HotelController();
        int[] count = {0};
        hc.voegListenerToe(() -> count[0]++);
        hc.voegListenerToe(() -> count[0]++);
        hc.notifyListeners();
        assertEquals(2, count[0]);
    }

    // Ik registreer hotellisteners voor een leeg hotel; ik verwacht dat dit geen crash geeft.
    @Test void testRegistreerListenersLeegHotel() {
        EventController ec = new EventController(new HotelEventManager(true));
        Hotel h = new Hotel();
        assertDoesNotThrow(() -> ec.registreerHotelListeners(h));
    }

    // Ik registreer hotellisteners voor een hotel met lobby; ik verwacht dat dit geen crash geeft.
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
