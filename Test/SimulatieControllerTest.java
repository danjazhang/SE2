package tests;

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

    // Maak testhotel
    private Hotel maakHotel() {

        Hotel hotel = new Hotel();

        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;

        // Lift toevoegen
        Lift lift = new Lift(hotel);
        lift.posX = 1;
        lift.posY = 1;
        lift.breedte = 1;
        lift.hoogte = 4;

        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        // Trap toevoegen
        Trap trap = new Trap(2);
        trap.posX = 6;
        trap.posY = 1;
        trap.breedte = 1;
        trap.hoogte = 4;

        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        // Pathfinder
        hotel.pathfinder = new Pathfinder(hotel);

        return hotel;
    }

    // Constructor test
    @Test
    void testConstructor() {

        assertDoesNotThrow(() ->
                new SimulatieController(manager, ec, hc)
        );
    }

    // Start test (kan runtime afhankelijk zijn van event system)
    @Test
    void testStart() {

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        // Mag niet crashen
        assertDoesNotThrow(() -> sc.start());
    }

    // Pauze test
    @Test
    void testPauzeer() {

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        assertDoesNotThrow(() -> sc.pauzeer());
    }

    // Stop test
    @Test
    void testStop() {

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        assertDoesNotThrow(() -> sc.stop());
    }

    // Snelheid instellingen
    @Test
    void testPasSnelheidToeLangzaam() {

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        sc.pasSnelheidToe("Langzaam");
    }

    @Test
    void testPasSnelheidToeNormaal() {

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        sc.pasSnelheidToe("Normaal");
    }

    @Test
    void testPasSnelheidToeSnel() {

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        sc.pasSnelheidToe("Snel");
    }

    @Test
    void testPasSnelheidToeDefault() {

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        sc.pasSnelheidToe("Onbekend");
    }

    // Tik zonder hotel
    @Test
    void testTikZonderHotel() {

        HotelController lege = new HotelController();

        SimulatieController sc =
                new SimulatieController(manager, ec, lege);

        assertDoesNotThrow(() -> sc.tik());
    }

    // Tik met leeg hotel
    @Test
    void testTikMetLeegHotel() {

        Hotel hotel = maakHotel();
        hc.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        assertDoesNotThrow(() -> sc.tik());
    }

    // Tik met gast beweging
    @Test
    void testTikBeweegGast() {

        Hotel hotel = maakHotel();

        Gast gast = new Gast(1, 1);
        gast.setPathfinder(hotel.pathfinder);

        gast.zetStartPositie(
                hotel.layout.krijgVakje(2, 1)
        );

        gast.zetDoel(
                hotel.layout.krijgVakje(4, 1)
        );

        hotel.voegPersoonToe(gast);

        hc.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        sc.tik();

        assertNotNull(gast.huidigVakje);
    }

    // Tick meerdere keren (branch coverage snelheid)
    @Test
    void testTikSnelleModus() {

        Hotel hotel = maakHotel();
        hc.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        sc.setSnelheid(4);

        assertDoesNotThrow(() -> {
            sc.tik();
            sc.tik();
        });
    }

    // Langzame modus branch
    @Test
    void testTikLangzameModus() {

        Hotel hotel = maakHotel();
        hc.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(manager, ec, hc);

        sc.setSnelheid(0);

        assertDoesNotThrow(() -> sc.tik());
    }
}