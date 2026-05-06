import Controller.EventController;
import Controller.HotelController;
import Controller.SimulatieController;
import Model.*;
import Model.layout.Layout;
import Model.ruimte.Lobby;
import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventControllerTest {

    private HotelEventManager manager = new HotelEventManager(true);

    // constructor: geen crash
    @Test void testConstructor() {
        assertNotNull(new EventController(manager));
    }

    // registreerListener: listener wordt toegevoegd zonder crash
    @Test void testRegistreerListener() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.registreerListener(event -> {}));
    }

    // setLogger: geen crash
    @Test void testSetLogger() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.setLogger(bericht -> {}));
    }

    // setHotelController: geen crash
    @Test void testSetHotelController() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.setHotelController(new HotelController()));
    }

    // setSimulatieController: geen crash
    @Test void testSetSimulatieController() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> ec.setSimulatieController(sc));
    }

    // notify: geen crash zonder hotelcontroller
    @Test void testNotifyZonderHotelControllerCrashetNiet() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1)));
    }

    // notify EVACUATE: logger wordt aangeroepen
    @Test void testNotifyEvacuateLogt() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(3, 3);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        boolean[] logged = {false};
        ec.setLogger(bericht -> logged[0] = true);
        ec.notify(new HotelEvent(1, HotelEventType.EVACUATE, -1, -1));
        assertTrue(logged[0]);
    }

    // notify GODZILLA: logger wordt aangeroepen
    @Test void testNotifyGodzillaLogt() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(3, 3);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        boolean[] logged = {false};
        ec.setLogger(bericht -> logged[0] = true);
        ec.notify(new HotelEvent(1, HotelEventType.GODZILLA, -1, -1));
        assertTrue(logged[0]);
    }

    // notify NONE: geen crash zonder personen
    @Test void testNotifyNoneZonderPersonenCrashetNiet() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.NONE, -1, -1)));
    }

    // stuurNaarListeners: alle listeners worden aangeroepen
    @Test void testStuurNaarListenersRoeptAlleListenersAan() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(3, 3);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        int[] count = {0};
        ec.registreerListener(event -> count[0]++);
        ec.registreerListener(event -> count[0]++);
        ec.notify(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        assertEquals(2, count[0]);
    }

    // registreerHotelListeners: geen crash met null hotel
    @Test void testRegistreerHotelListenersNullHotel() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.registreerHotelListeners(null));
    }

    // registreerHotelListeners: geen crash met leeg hotel
    @Test void testRegistreerHotelListenersLeegHotel() {
        EventController ec = new EventController(manager);
        Hotel hotel = new Hotel();
        assertDoesNotThrow(() -> ec.registreerHotelListeners(hotel));
    }

    // notificeerPersoon: geen crash
    @Test void testNotificeerPersoon() {
        EventController ec = new EventController(manager);
        Model.persoon.Gast gast = new Model.persoon.Gast(1, 1);
        hotelevents.HotelEvent evt = new hotelevents.HotelEvent(1, HotelEventType.CHECK_IN, 1, 1);
        assertDoesNotThrow(() -> ec.notificeerPersoon(gast, evt));
    }
}