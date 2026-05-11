import Controller.EventController;
import Controller.HotelController;
import Controller.SimulatieController;
import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import hotelevents.HotelEventManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimulatieControllerTest {

    private HotelEventManager manager = new HotelEventManager(true);
    private EventController ec = new EventController(manager);
    private HotelController hc = new HotelController();

    // hulpmethode: maak hotel met pathfinder
    private Hotel maakHotel() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;
        Lift lift = new Lift();
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        Trap trap = new Trap(2);
        trap.posX = 6; trap.posY = 1; trap.breedte = 1; trap.hoogte = 4;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        hotel.pathfinder = new Pathfinder(hotel);
        return hotel;
    }

    // constructor: geen crash
    @Test void testConstructor() {
        assertDoesNotThrow(() -> new SimulatieController(manager, ec, hc));
    }

    // start: gooit exception zonder scenario
    @Test void testStartGooidExceptionZonderScenario() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertThrows(RuntimeException.class, () -> sc.start());
    }

    // pauzeer: geen crash
    @Test void testPauzeer() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> sc.pauzeer());
    }

    // stop: gooit exception in testmodus want executor is null
    @Test void testStop() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertThrows(Exception.class, () -> sc.stop());
    }

    // tik: geen crash zonder personen
    @Test void testTikZonderPersonen() {
        Hotel hotel = maakHotel();
        hc.setHotel(hotel);
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> sc.tik());
    }

    // tik: personen bewegen per tik
    @Test void testTikBeweegPersonen() {
        Hotel hotel = maakHotel();
        Gast gast = new Gast(1, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        gast.zetDoel(hotel.layout.krijgVakje(4, 1));
        hotel.voegPersoonToe(gast);
        hc.setHotel(hotel);
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        sc.tik();
        // gast is 1 stap verder
        assertEquals(3, gast.huidigVakje.x);
    }

    // tik: notifyListeners wordt aangeroepen
    @Test void testTikNotificeertListeners() {
        Hotel hotel = maakHotel();
        hc.setHotel(hotel);
        boolean[] called = {false};
        hc.voegListenerToe(() -> called[0] = true);
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        sc.tik();
        assertTrue(called[0]);
    }

    // tik: geen crash als hotel null is
    @Test void testTikZonderHotel() {
        HotelController legeHc = new HotelController();
        SimulatieController sc = new SimulatieController(manager, ec, legeHc);
        // hotel is leeg maar niet null, dus geen crash verwacht
        assertDoesNotThrow(() -> sc.tik());
    }
}
