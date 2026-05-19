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

    // Ik maak een nieuwe EventController aan; ik verwacht dat dit zonder crash lukt.
    @Test void testConstructor() {
        assertNotNull(new EventController(manager));
    }

    // Ik registreer een listener; ik verwacht dat dit zonder crash lukt.
    @Test void testRegistreerListener() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.registreerListener(event -> {}));
    }

    // Ik stel een logger in; ik verwacht dat dit zonder crash lukt.
    @Test void testSetLogger() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.setLogger(bericht -> {}));
    }

    // Ik stel een hotelcontroller in; ik verwacht dat dit zonder crash lukt.
    @Test void testSetHotelController() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.setHotelController(new HotelController()));
    }

    // Ik stel een simulatiecontroller in; ik verwacht dat dit zonder crash lukt.
    @Test void testSetSimulatieController() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> ec.setSimulatieController(sc));
    }

    // Ik stuur een event zonder hotelcontroller; ik verwacht dat dit geen crash geeft.
    @Test void testNotifyZonderHotelControllerCrashetNiet() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1)));
    }

    // Ik stuur een EVACUATE event met logger; ik verwacht dat de logger wordt aangeroepen.
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

    // Ik stuur een GODZILLA event met logger; ik verwacht dat de logger wordt aangeroepen.
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

    // Ik stuur een NONE event in een leeg hotel; ik verwacht dat dit geen crash geeft.
    @Test void testNotifyNoneZonderPersonenCrashetNiet() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.NONE, -1, -1)));
    }

    // Ik registreer twee listeners en stuur een event; ik verwacht dat beide listeners worden aangeroepen.
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

    // Ik registreer hotellisteners met een null hotel; ik verwacht dat dit geen crash geeft.
    @Test void testRegistreerHotelListenersNullHotel() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.registreerHotelListeners(null));
    }

    // Ik registreer hotellisteners met een leeg hotel; ik verwacht dat dit geen crash geeft.
    @Test void testRegistreerHotelListenersLeegHotel() {
        EventController ec = new EventController(manager);
        Hotel hotel = new Hotel();
        assertDoesNotThrow(() -> ec.registreerHotelListeners(hotel));
    }

    // Ik notificeer één persoon handmatig; ik verwacht dat dit geen crash geeft.
    @Test void testNotificeerPersoon() {
        EventController ec = new EventController(manager);
        Model.persoon.Gast gast = new Model.persoon.Gast(1, 1);
        hotelevents.HotelEvent evt = new hotelevents.HotelEvent(1, HotelEventType.CHECK_IN, 1, 1);
        assertDoesNotThrow(() -> ec.notificeerPersoon(gast, evt));
    }
}
