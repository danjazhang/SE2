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

    // Ik maak met deze hulpmethode een hotel met pathfinder,
    // zodat de simulatiecontroller personen kan laten bewegen.
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

    // Ik maak een nieuwe SimulatieController aan; ik verwacht dat dit geen crash geeft.
    @Test void testConstructor() {
        assertDoesNotThrow(() -> new SimulatieController(manager, ec, hc));
    }

    // Ik start de simulatie zonder scenario; ik verwacht dat dit in testmodus een exception geeft.
    @Test void testStartGooidExceptionZonderScenario() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertThrows(RuntimeException.class, () -> sc.start());
    }

    // Ik pauzeer de simulatiecontroller; ik verwacht dat dit geen crash geeft.
    @Test void testPauzeer() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> sc.pauzeer());
    }

    // Ik stop de simulatiecontroller in testmodus; ik verwacht dat dit een exception geeft omdat de executor ontbreekt.
    @Test void testStop() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertThrows(Exception.class, () -> sc.stop());
    }

    // Ik voer een tik uit in een hotel zonder personen; ik verwacht dat dit geen crash geeft.
    @Test void testTikZonderPersonen() {
        Hotel hotel = maakHotel();
        hc.setHotel(hotel);
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> sc.tik());
    }

    // Ik voer een tik uit met een gast die een doel heeft; ik verwacht dat die gast één stap beweegt.
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
        // Ik verwacht dat de gast precies één stap verder staat.
        assertEquals(3, gast.huidigVakje.x);
    }

    // Ik voer een tik uit met een listener op het hotel; ik verwacht dat die listener wordt aangeroepen.
    @Test void testTikNotificeertListeners() {
        Hotel hotel = maakHotel();
        hc.setHotel(hotel);
        boolean[] called = {false};
        hc.voegListenerToe(() -> called[0] = true);
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        sc.tik();
        assertTrue(called[0]);
    }

    // Ik voer een tik uit met een lege hotelcontroller; ik verwacht dat dit geen crash geeft.
    @Test void testTikZonderHotel() {
        HotelController legeHc = new HotelController();
        SimulatieController sc = new SimulatieController(manager, ec, legeHc);
        // Ik verwacht geen crash, omdat de controller wel bestaat ook al is het hotel leeg.
        assertDoesNotThrow(() -> sc.tik());
    }
}
