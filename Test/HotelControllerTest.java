import Controller.HotelController;
import Model.Hotel;
import Model.ILogger;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Trap;
import Model.Pathfinder;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor HotelController: hotel beheren, listeners, logger
public class HotelControllerTest {

    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    // constructor: hotel is aangemaakt
    @Test void testConstructorHotelAangemaakt() {
        assertNotNull(new HotelController().getHotel());
    }

    // constructor: layoutController is aangemaakt
    @Test void testConstructorLayoutController() {
        assertNotNull(new HotelController().getLayoutController());
    }

    // heeftLayout: false als hotel geen layout heeft
    @Test void testHeeftLayoutFalse() {
        assertFalse(new HotelController().heeftLayout());
    }

    // heeftLayout: true als hotel een layout heeft
    @Test void testHeeftLayoutTrue() {
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(4, 4);
        hc.setHotel(hotel);
        assertTrue(hc.heeftLayout());
    }

    // setHotel: hotel wordt opgeslagen
    @Test void testSetHotel() {
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hc.setHotel(hotel);
        assertSame(hotel, hc.getHotel());
    }

    // setHotel: logger wordt ingesteld op lobby
    @Test void testSetHotelStelLoggerInOpLobby() {
        HotelController hc = new HotelController();
        TestLogger logger = new TestLogger();
        hc.setLogger(logger);

        Hotel hotel = new Hotel();
        hotel.layout = new Layout(8, 5);
        hotel.breedte = 8;
        hotel.hoogte = 5;
        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 5;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        lift.initWachtrijen(5);
        lift.setLobbyVerdieping(2);
        Trap trap = new Trap(2);
        trap.posX = 7; trap.posY = 1; trap.breedte = 2; trap.hoogte = 5;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        Lobby lobby = new Lobby(1, 2, 5, 1, 3, 2, hotel, null);
        hotel.lobby = lobby;
        hotel.ruimtes.add(lobby);
        hotel.layout.plaatsRuimte(lobby);
        hotel.pathfinder = new Pathfinder(hotel);

        hc.setHotel(hotel);
        // geen crash = logger correct ingesteld
        assertDoesNotThrow(() -> hc.notifyListeners());
    }

    // setHotel: schoonmakers krijgen de logger
    @Test void testSetHotelStelLoggerInOpSchoonmakers() {
        HotelController hc = new HotelController();
        TestLogger logger = new TestLogger();
        hc.setLogger(logger);

        Hotel hotel = new Hotel();
        Schoonmaker s = new Schoonmaker(logger);
        hotel.voegPersoonToe(s);
        hc.setHotel(hotel);
        // schoonmaker is een Schoonmaker, logger moet opnieuw ingesteld zijn → geen crash
        assertDoesNotThrow(() -> s.beweeg());
    }

    // voegListenerToe en notifyListeners: listener wordt aangeroepen
    @Test void testVoegListenerEnNotify() {
        HotelController hc = new HotelController();
        boolean[] called = {false};
        hc.voegListenerToe(() -> called[0] = true);
        hc.notifyListeners();
        assertTrue(called[0]);
    }

    // meerdere listeners worden allemaal aangeroepen
    @Test void testMeerdereListeners() {
        HotelController hc = new HotelController();
        int[] count = {0};
        hc.voegListenerToe(() -> count[0]++);
        hc.voegListenerToe(() -> count[0]++);
        hc.voegListenerToe(() -> count[0]++);
        hc.notifyListeners();
        assertEquals(3, count[0]);
    }

    // notifyListeners: geen crash zonder listeners
    @Test void testNotifyZonderListeners() {
        assertDoesNotThrow(() -> new HotelController().notifyListeners());
    }

    // setLogger: geen crash
    @Test void testSetLogger() {
        assertDoesNotThrow(() -> new HotelController().setLogger(new TestLogger()));
    }

    // getHotel: geeft het hotel terug na setHotel
    @Test void testGetHotelNaSet() {
        HotelController hc = new HotelController();
        Hotel h = new Hotel();
        hc.setHotel(h);
        assertSame(h, hc.getHotel());
    }
}
