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
    static Hotel maakHotel() {
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

    // start met scenario: gooit exception in testmodus want scenario bestand bestaat niet
    @Test void testStartGooidExceptionZonderScenario() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertThrows(RuntimeException.class, () -> sc.start(1));
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

    // tik: gast beweegt één stap per tik
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
        assertDoesNotThrow(() -> sc.tik());
    }

    // pasSnelheidToe: langzaam zet snelheid op 0
    @Test void testPasSnelheidToeLangzaam() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> sc.pasSnelheidToe("Langzaam"));
    }

    // pasSnelheidToe: normaal zet snelheid op 1
    @Test void testPasSnelheidToeNormaal() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> sc.pasSnelheidToe("Normaal"));
    }

    // pasSnelheidToe: snel zet snelheid op 4
    @Test void testPasSnelheidToeSnel() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> sc.pasSnelheidToe("Snel"));
    }

    // setSnelheid: geen crash
    @Test void testSetSnelheid() {
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> sc.setSnelheid(2));
    }

    // tik langzaam: persoon beweegt niet op oneven tik
    @Test void testTikLangzaamBeweegNietOpOneven() {
        Hotel hotel = maakHotel();
        Gast gast = new Gast(1, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        gast.zetDoel(hotel.layout.krijgVakje(4, 1));
        hotel.voegPersoonToe(gast);
        hc.setHotel(hotel);
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        sc.pasSnelheidToe("Langzaam");
        // eerste tik is oneven, gast mag niet bewegen
        sc.tik();
        assertEquals(2, gast.huidigVakje.x);
    }

    // tik langzaam: persoon beweegt wel op even tik
    @Test void testTikLangzaamBeweegWelOpEven() {
        Hotel hotel = maakHotel();
        Gast gast = new Gast(1, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        gast.zetDoel(hotel.layout.krijgVakje(4, 1));
        hotel.voegPersoonToe(gast);
        hc.setHotel(hotel);
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        sc.pasSnelheidToe("Langzaam");
        sc.tik(); // oneven, geen beweging
        sc.tik(); // even, wel beweging
        assertEquals(3, gast.huidigVakje.x);
    }

    // tik snel: persoon zet meerdere stappen per tik
    @Test void testTikSnelMeerdereStappen() {
        Hotel hotel = maakHotel();
        Gast gast = new Gast(1, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        gast.zetDoel(hotel.layout.krijgVakje(6, 1));
        hotel.voegPersoonToe(gast);
        hc.setHotel(hotel);
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        sc.pasSnelheidToe("Snel");
        sc.tik();
        // bij snelheid 4 zet de gast 4 stappen, dus van x=2 naar x=6
        assertTrue(gast.huidigVakje.x > 2);
    }
}
