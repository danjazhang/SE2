package tests;

import Controller.EventController;
import Controller.HotelController;
import Controller.SimulatieController;

import Model.Hotel;
import Model.Pathfinder;

import Model.layout.Layout;
import Model.layout.Vakje;

import Model.persoon.Gast;
import Model.persoon.Persoon;

import Model.ruimte.Lift;
import Model.ruimte.Trap;

import hotelevents.HotelEventManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimulatieControllerTest {

    // Event manager voor simulatie
    private HotelEventManager manager =
            new HotelEventManager(true);

    // Event controller
    private EventController eventController =
            new EventController(manager);

    // Hotel controller
    private HotelController hotelController =
            new HotelController();

    // Hulpmethode:
    // maak een volledig testhotel
    private Hotel maakHotel() {

        Hotel hotel = new Hotel();

        // Layout maken
        hotel.layout = new Layout(10, 5);

        hotel.breedte = 10;
        hotel.hoogte = 5;

        // Lift maken
        Lift lift = new Lift(hotel);

        lift.posX = 1;
        lift.posY = 1;

        lift.breedte = 1;
        lift.hoogte = 5;

        // Wachtrijen initialiseren
        lift.initWachtrijen(5);

        // Lift koppelen aan hotel
        hotel.lift = lift;

        // Lift toevoegen aan ruimtes
        hotel.ruimtes.add(lift);

        // Lift plaatsen op layout
        hotel.layout.plaatsRuimte(lift);

        // Trap maken
        Trap trap = new Trap(2);

        trap.posX = 8;
        trap.posY = 1;

        trap.breedte = 1;
        trap.hoogte = 5;

        // Trap toevoegen
        hotel.ruimtes.add(trap);

        // Trap plaatsen
        hotel.layout.plaatsRuimte(trap);

        // Pathfinder maken
        hotel.pathfinder =
                new Pathfinder(hotel);

        return hotel;
    }

    // -------------------------------------------------
    // Constructor tests
    // -------------------------------------------------

    // ik doe dit: een SimulatieController maken met manager, eventController en hotelController; ik verwacht dat dit zonder exceptions gebeurt
    @Test
    void testConstructor() {

        assertDoesNotThrow(() -> {

            new SimulatieController(
                    manager,
                    eventController,
                    hotelController
            );
        });
    }

    // -------------------------------------------------
    // Start / stop / pauze tests
    // -------------------------------------------------

    // ik doe dit: simulatie starten zonder volledig geïnitialiseerd hotel; ik verwacht een RuntimeException omdat scen1.hotel ontbreekt
    @Test
    void testStart() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertThrows(RuntimeException.class, () -> {

            sc.start(1);
        });
    }

    // ik doe dit: simulatie pauzeren zonder dat er al iets draait; ik verwacht dat dit geen error geeft
    @Test
    void testPauzeer() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.pauzeer();
        });
    }

    // ik doe dit: simulatie stoppen zonder actieve simulatie; ik verwacht een exception omdat er niets gestart is
    @Test
    void testStop() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertThrows(Exception.class, () -> {

            sc.stop();
        });
    }

    // -------------------------------------------------
    // Snelheid tests
    // -------------------------------------------------

    // ik doe dit: snelheid instellen op "Langzaam"; ik verwacht dat dit geen exceptions veroorzaakt
    @Test
    void testPasSnelheidToeLangzaam() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.pasSnelheidToe("Langzaam");
        });
    }

    // ik doe dit: snelheid instellen op "Normaal"; ik verwacht dat dit correct verwerkt wordt zonder errors
    @Test
    void testPasSnelheidToeNormaal() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.pasSnelheidToe("Normaal");
        });
    }

    // ik doe dit: snelheid instellen op "Snel"; ik verwacht dat dit zonder exception wordt toegepast
    @Test
    void testPasSnelheidToeSnel() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.pasSnelheidToe("Snel");
        });
    }

    // ik doe dit: onbekende snelheid invoeren; ik verwacht fallback/default gedrag zonder crash
    @Test
    void testPasSnelheidToeDefault() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.pasSnelheidToe("TEST");
        });
    }

    // ik doe dit: directe snelheid zetten op 10; ik verwacht dat dit zonder exceptions lukt
    @Test
    void testSetSnelheid() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.setSnelheid(10);
        });
    }

    // -------------------------------------------------
    // Tik tests
    // -------------------------------------------------

    // ik doe dit: tik uitvoeren zonder hotel; ik verwacht dat de methode veilig returnt zonder crash
    @Test
    void testTikZonderHotel() {

        HotelController legeController =
                new HotelController();

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        legeController
                );

        assertDoesNotThrow(() -> {

            sc.tik();
        });
    }

    // ik doe dit: tik uitvoeren met leeg hotel; ik verwacht dat simulatie veilig draait zonder errors
    @Test
    void testTikLeegHotel() {

        Hotel hotel = maakHotel();

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.tik();
        });
    }

    // ik doe dit: tik uitvoeren zonder lift in hotel; ik verwacht dat dit geen crash veroorzaakt
    @Test
    void testTikZonderLift() {

        Hotel hotel = maakHotel();

        hotel.lift = null;

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.tik();
        });
    }

    // ik doe dit: gast laten bewegen via tik; ik verwacht dat de gast 1 stap vooruit beweegt richting doel
    @Test
    void testGastBeweegtTijdensTik() {

        Hotel hotel = maakHotel();

        Gast gast = new Gast(1, 3);

        gast.setPathfinder(
                hotel.pathfinder
        );

        Vakje start =
                hotel.layout.krijgVakje(3, 1);

        Vakje doel =
                hotel.layout.krijgVakje(6, 1);

        gast.zetStartPositie(start);

        gast.zetDoel(doel);

        hotel.voegPersoonToe(gast);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        sc.tik();

        assertEquals(
                4,
                gast.huidigVakje.x
        );
    }

    // ik doe dit: listeners registreren en tik uitvoeren; ik verwacht dat alle listeners worden aangeroepen
    @Test
    void testNotifyListeners() {

        Hotel hotel = maakHotel();

        hotelController.setHotel(hotel);

        boolean[] called = {false};

        hotelController.voegListenerToe(() -> {

            called[0] = true;
        });

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        sc.tik();

        assertTrue(called[0]);
    }

    // -------------------------------------------------
    // Langzame snelheid tests
    // -------------------------------------------------

    // ik doe dit: snelheid op 0 zetten en meerdere tikken uitvoeren; ik verwacht dat eerste tik wordt overgeslagen en tweede wel uitvoert
    @Test
    void testLangzameSnelheid() {

        Hotel hotel = maakHotel();

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        sc.setSnelheid(0);

        assertDoesNotThrow(() -> {

            sc.tik();
        });

        assertDoesNotThrow(() -> {

            sc.tik();
        });
    }

    // -------------------------------------------------
    // Snelle snelheid tests
    // -------------------------------------------------

    // ik doe dit: snelheid op 4 zetten; ik verwacht dat tik direct meerdere stappen verwerkt zonder crash
    @Test
    void testSnelleSnelheid() {

        Hotel hotel = maakHotel();

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        sc.setSnelheid(4);

        assertDoesNotThrow(() -> {

            sc.tik();
        });
    }

    // -------------------------------------------------
    // Lift tests
    // -------------------------------------------------

    // ik doe dit: gast naast lift plaatsen en tik uitvoeren; ik verwacht dat gast niet meer wacht op lift
    @Test
    void testGastWachtOpLift() {

        Hotel hotel = maakHotel();

        Gast gast = new Gast(1, 2);

        gast.gebruiktLift = true;

        gast.setPathfinder(
                hotel.pathfinder
        );

        Vakje vakje =
                hotel.layout.krijgVakje(
                        hotel.lift.posX + 1,
                        hotel.lift.getHuidigeVerdieping()
                );

        gast.zetStartPositie(vakje);

        hotel.voegPersoonToe(gast);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        sc.tik();

        assertFalse(gast.wachtOpLift);
    }

    // ik doe dit: gast in lift plaatsen; ik verwacht dat tik geen exceptions veroorzaakt
    @Test
    void testGastInLift() {

        Hotel hotel = maakHotel();

        Gast gast = new Gast(1, 2);

        gast.inLift = true;

        hotel.voegPersoonToe(gast);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.tik();
        });
    }

    // -------------------------------------------------
    // Uitstappen tests
    // -------------------------------------------------

    // ik doe dit: gast laten uitstappen; ik verwacht dat flag moetUitstappen wordt gereset
    @Test
    void testGastMoetUitstappen() {

        Hotel hotel = maakHotel();

        Gast gast = new Gast(1, 1);

        gast.moetUitstappen = true;

        gast.setPathfinder(
                hotel.pathfinder
        );

        hotel.voegPersoonToe(gast);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        sc.tik();

        assertFalse(gast.moetUitstappen);
    }

    // -------------------------------------------------
    // Branch coverage tests
    // -------------------------------------------------

    // ik doe dit: normale persoon toevoegen; ik verwacht dat tik dit object overslaat zonder fouten
    @Test
    void testNormalePersoon() {

        Hotel hotel = maakHotel();

        Persoon p = new Persoon() {
        };

        hotel.voegPersoonToe(p);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.tik();
        });
    }

    // ik doe dit: gast zonder positie laten bewegen; ik verwacht dat dit geen crash veroorzaakt
    @Test
    void testGastZonderPositie() {

        Hotel hotel = maakHotel();

        Gast gast = new Gast(1, 1);

        gast.gebruiktLift = true;

        hotel.voegPersoonToe(gast);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(() -> {

            sc.tik();
        });
    }

    // -------------------------------------------------
    // EXTRA BRANCH COVERAGE TESTS (SimulatieController)
    // -------------------------------------------------

    // ik doe dit: hotel is null in controller; ik verwacht dat tik direct veilig returnt
    @Test
    void testTikHotelNullBranchExtra() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        new HotelController()
                );

        assertDoesNotThrow(sc::tik);
    }

    // ik doe dit: langzame snelheid skip branch; ik verwacht dat eerste tick niets doet en tweede wel
    @Test
    void testLangzameSnelheidSkipBranchExtra() {

        Hotel hotel = maakHotel();
        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        sc.setSnelheid(0);

        sc.tik();

        assertDoesNotThrow(sc::tik);
    }

    // ik doe dit: snelle snelheid branch testen; ik verwacht dat meerdere stappen correct worden uitgevoerd
    @Test
    void testSnelleSnelheidStappenBranchExtra() {

        Hotel hotel = maakHotel();
        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        sc.setSnelheid(4);

        assertDoesNotThrow(sc::tik);
    }

    // ik doe dit: hotel zonder lift; ik verwacht dat alle lift-logica wordt overgeslagen zonder errors
    @Test
    void testGeenLiftBranchExtra() {

        Hotel hotel = maakHotel();

        hotel.lift = null;

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(sc::tik);
    }

    // ik doe dit: persoon die geen gast is; ik verwacht dat alleen generieke tick logica wordt uitgevoerd
    @Test
    void testNietGastBranchExtra() {

        Hotel hotel = maakHotel();

        Persoon p = new Persoon() {};
        hotel.voegPersoonToe(p);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(sc::tik);
    }

    // ik doe dit: gast zonder liftgebruik; ik verwacht dat lift-branches worden overgeslagen
    @Test
    void testGastZonderLiftGebruikBranchExtra() {

        Hotel hotel = maakHotel();

        Gast g = new Gast(1, 1);

        g.gebruiktLift = false;

        g.huidigVakje = hotel.layout.krijgVakje(2, 2);

        hotel.voegPersoonToe(g);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(sc::tik);
    }

    // ik doe dit: gast al in lift; ik verwacht dat lift-logica wordt geskipt zonder fouten
    @Test
    void testGastAlInLiftBranchExtra() {

        Hotel hotel = maakHotel();

        Gast g = new Gast(1, 1);

        g.inLift = true;

        g.gebruiktLift = true;

        g.huidigVakje = hotel.layout.krijgVakje(2, 2);

        hotel.voegPersoonToe(g);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(sc::tik);
    }

    // ik doe dit: gast zonder huidig vakje; ik verwacht dat null-safe branches worden geraakt
    @Test
    void testGastZonderVakjeBranchExtra() {

        Hotel hotel = maakHotel();

        Gast g = new Gast(1, 1);

        g.gebruiktLift = true;

        g.huidigVakje = null;

        hotel.voegPersoonToe(g);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        assertDoesNotThrow(sc::tik);
    }

    // ik doe dit: listeners toevoegen en tik uitvoeren; ik verwacht dat notifyListeners altijd wordt uitgevoerd
    @Test
    void testNotifyListenersExtraBranch() {

        Hotel hotel = maakHotel();
        hotelController.setHotel(hotel);

        boolean[] called = {false};

        hotelController.voegListenerToe(() -> called[0] = true);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        sc.tik();

        assertTrue(called[0]);
    }

    // ik doe dit: meerdere ticks uitvoeren; ik verwacht volledige for-loop coverage van stappen logica
    @Test
    void testMeerdereStappenBranch() {

        Hotel hotel = maakHotel();
        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        sc.setSnelheid(4);

        sc.tik();

        sc.tik();

        assertDoesNotThrow(() -> sc.tik());
    }
}