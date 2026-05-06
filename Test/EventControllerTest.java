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

// Testklasse voor EventController: ik test event-doorsturing, logging en NONE-ticks.
public class EventControllerTest {

    // Ik gebruik een testmanager; ik verwacht dat de externe library geen echte simulatie start.
    private HotelEventManager manager = new HotelEventManager(true);

    // Ik maak een EventController met een manager; ik verwacht dat het object bestaat.
    @Test void testConstructor() {
        EventController ec = new EventController(manager);
        assertNotNull(ec);
    }

    // Ik registreer een listener; ik verwacht dat registreren geen fout geeft.
    @Test void testRegistreerListener() {
        EventController ec = new EventController(manager);
        boolean[] called = {false};
        ec.registreerListener(event -> called[0] = true);
        ec.notify(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        // notify vereist hotelController, dus geen crash verwacht
        assertDoesNotThrow(() -> ec.registreerListener(event -> {}));
    }

    // Ik zet een logger; ik verwacht dat dit veilig zonder exception kan.
    @Test void testSetLogger() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.setLogger(bericht -> {}));
    }

    // Ik koppel een HotelController; ik verwacht dat dit zonder fout kan.
    @Test void testSetHotelController() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        assertDoesNotThrow(() -> ec.setHotelController(hc));
    }

    // Ik stuur een event zonder HotelController; ik verwacht dat notify niet crasht.
    @Test void testNotifyZonderHotelControllerCrashetNiet() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1)));
    }

    // Ik stuur een EVACUATE-event; ik verwacht dat er een logbericht wordt geschreven.
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

    // Ik stuur een GODZILLA-event; ik verwacht dat er een logbericht wordt geschreven.
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

    // Ik stuur een NONE-event zonder personen; ik verwacht dat dit veilig blijft.
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

    // Ik registreer twee listeners en stuur een event; ik verwacht dat beide worden aangeroepen.
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

    // Ik stuur een CHECK_IN-event; ik verwacht dat de listener dit event ontvangt.
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
