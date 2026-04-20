import Controller.EventController;
import Controller.HotelController;
import Model.*;
import Model.ruimte.Kamer;
import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import Model.layout.Layout;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventControllerTest {

    private HotelEventManager manager = new HotelEventManager(true);

    @Test void testConstructor() {
        EventController ec = new EventController(manager);
        assertNotNull(ec);
    }

    @Test void testRegistreerListener() {
        EventController ec = new EventController(manager);
        boolean[] called = {false};
        ec.registreerListener(event -> called[0] = true);
        ec.notify(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        // notify vereist hotelController, dus geen crash verwacht
        assertDoesNotThrow(() -> ec.registreerListener(event -> {}));
    }

    @Test void testSetLogger() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.setLogger(bericht -> {}));
    }

    @Test void testSetHotelController() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        assertDoesNotThrow(() -> ec.setHotelController(hc));
    }

    @Test void testNotifyZonderHotelControllerCrashetNiet() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1)));
    }

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

    @Test void testNotifyNoneBeweegPersonen() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        // geen personen dus geen crash
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.NONE, -1, -1)));
    }

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

    @Test void testNotifyCheckInStuurNaarListeners() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(3, 3);
        hc.setHotel(hotel);
        ec.setHotelController(hc);

        boolean[] called = {false};
        ec.registreerListener(event -> {
            if (event.getEventType() == HotelEventType.CHECK_IN) called[0] = true;
        });
        ec.notify(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        assertTrue(called[0]);
    }
}
