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

// Testklasse voor HotelController: ik test hotelbeheer, layoutcontroller en eventregistratie.
public class HotelControllerTest {

    // Ik maak een HotelController; ik verwacht dat hij een Hotel en LayoutController heeft.
    @Test void testConstructor() {
        HotelController hc = new HotelController();
        assertNotNull(hc.getHotel());
        assertNotNull(hc.getLayoutController());
    }

    // Ik gebruik een nieuwe HotelController; ik verwacht dat er nog geen layout is.
    @Test void testHeeftLayoutFalse() {
        assertFalse(new HotelController().heeftLayout());
    }

    // Ik zet een Hotel met layout; ik verwacht dat heeftLayout true teruggeeft.
    @Test void testHeeftLayoutTrue() {
        HotelController hc = new HotelController();
        Hotel h = new Hotel();
        h.layout = new Layout(3, 3);
        hc.setHotel(h);
        assertTrue(hc.heeftLayout());
    }

    // Ik zet een nieuw Hotel; ik verwacht dat getHotel datzelfde hotel teruggeeft.
    @Test void testSetHotel() {
        HotelController hc = new HotelController();
        Hotel h = new Hotel();
        hc.setHotel(h);
        assertEquals(h, hc.getHotel());
    }

    // Ik zet een logger; ik verwacht dat dit zonder exception kan.
    @Test void testSetLogger() {
        HotelController hc = new HotelController();
        assertDoesNotThrow(() -> hc.setLogger(bericht -> {}));
    }

    // Ik koppel een EventController; ik verwacht dat dit zonder exception kan.
    @Test void testSetEventController() {
        HotelController hc = new HotelController();
        EventController ec = new EventController(new HotelEventManager(true));
        assertDoesNotThrow(() -> hc.setEventController(ec));
    }

    // Ik zet een Hotel met ruimtes; ik verwacht dat listenerregistratie veilig verloopt.
    @Test void testSetHotelRegistreertListeners() {
        HotelController hc = new HotelController();
        EventController ec = new EventController(new HotelEventManager(true));
        hc.setEventController(ec);

        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        Kamer k = new Kamer();
        k.posX = 1; k.posY = 1; k.breedte = 1; k.hoogte = 1;
        h.ruimtes.add(k);

        int[] count = {0};
        ec.registreerListener(event -> count[0]++);
        hc.setHotel(h);
        // kamer is geen IEventListener dus count blijft 0 van kamer
        // maar de registreerListener die we zelf toevoegden telt mee
        assertDoesNotThrow(() -> hc.setHotel(h));
    }

    // Ik zet een Hotel met Lobby en logger; ik verwacht dat setHotel geen fout geeft.
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

    // Ik vraag de LayoutController op; ik verwacht een echte LayoutController terug.
    @Test void testGetLayoutController() {
        HotelController hc = new HotelController();
        assertTrue(hc.getLayoutController() instanceof LayoutController);
    }
}
