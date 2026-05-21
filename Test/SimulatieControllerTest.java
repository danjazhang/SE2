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

    // Test of constructor werkt
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

    // Test start simulatie
    @Test
    void testStart() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        // Verwacht exception omdat scen1.hotel ontbreekt
        assertThrows(RuntimeException.class, () -> {

            sc.start(1);
        });
    }
    // Test pauzeren
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

    // Test stoppen
    @Test
    void testStop() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        // Verwacht exception omdat simulatie niet gestart is
        assertThrows(Exception.class, () -> {

            sc.stop();
        });
    }

    // -------------------------------------------------
    // Snelheid tests
    // -------------------------------------------------

    // Test snelheid langzaam
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

    // Test snelheid normaal
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

    // Test snelheid snel
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

    // Test onbekende snelheid
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

    // Test setSnelheid direct
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

    // Test tik zonder hotel
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

    // Test tik met leeg hotel
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

    // Test tik zonder lift
    @Test
    void testTikZonderLift() {

        Hotel hotel = maakHotel();

        // Lift verwijderen
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

    // Test beweging van gast
    @Test
    void testGastBeweegtTijdensTik() {

        Hotel hotel = maakHotel();

        // Gast maken
        Gast gast = new Gast(1, 3);

        // Pathfinder koppelen
        gast.setPathfinder(
                hotel.pathfinder
        );

        // Startpositie
        Vakje start =
                hotel.layout.krijgVakje(3, 1);

        // Eindpositie
        Vakje doel =
                hotel.layout.krijgVakje(6, 1);

        // Zet start
        gast.zetStartPositie(start);

        // Zet doel
        gast.zetDoel(doel);

        // Voeg toe aan hotel
        hotel.voegPersoonToe(gast);

        hotelController.setHotel(hotel);

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        hotelController
                );

        // Tick uitvoeren
        sc.tik();

        // Gast moet bewogen zijn
        assertEquals(
                4,
                gast.huidigVakje.x
        );
    }

    // Test notify listeners
    @Test
    void testNotifyListeners() {

        Hotel hotel = maakHotel();

        hotelController.setHotel(hotel);

        // Controlevariabele
        boolean[] called = {false};

        // Listener toevoegen
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

        // Listener moet uitgevoerd zijn
        assertTrue(called[0]);
    }

    // -------------------------------------------------
    // Langzame snelheid tests
    // -------------------------------------------------

    // Test langzaam mode
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

        // Zet langzaam
        sc.setSnelheid(0);

        // Eerste tik doet niks
        assertDoesNotThrow(() -> {

            sc.tik();
        });

        // Tweede tik voert simulatie uit
        assertDoesNotThrow(() -> {

            sc.tik();
        });
    }

    // -------------------------------------------------
    // Snelle snelheid tests
    // -------------------------------------------------

    // Test snelle mode
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

        // Zet snelle modus
        sc.setSnelheid(4);

        assertDoesNotThrow(() -> {

            sc.tik();
        });
    }

    // -------------------------------------------------
    // Lift tests
    // -------------------------------------------------

    // Test gast wacht op lift
    @Test
    void testGastWachtOpLift() {

        Hotel hotel = maakHotel();

        Gast gast = new Gast(1, 2);

        gast.gebruiktLift = true;

        gast.setPathfinder(
                hotel.pathfinder
        );

        // Zet gast naast lift
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

        // Gast wacht niet meer
        assertFalse(gast.wachtOpLift);
    }

    // Test gast zit al in lift
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

    // Test gast moet uitstappen
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

        // Flag moet gereset zijn
        assertFalse(gast.moetUitstappen);
    }

    // -------------------------------------------------
    // Branch coverage tests
    // -------------------------------------------------

    // Test persoon die geen gast is
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

    // Test gast zonder positie
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

    // Test: hotel == null branch (early return)
    @Test
    void testTikHotelNullBranchExtra() {

        SimulatieController sc =
                new SimulatieController(
                        manager,
                        eventController,
                        new HotelController() // geen hotel gezet
                );

        // Moet direct return doen
        assertDoesNotThrow(sc::tik);
    }


    // Test: snelheid = 0 (skip branch in eerste tick)
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

        // eerste tick -> skip pad (tikTeller % 2 != 0)
        sc.tik();

        // tweede tick -> mag wel uitvoeren
        assertDoesNotThrow(sc::tik);
    }


    // Test: snelheid >= 4 (stappen = snelheid branch)
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

        // raakt: stappen = snelheid
        assertDoesNotThrow(sc::tik);
    }


    // Test: geen lift branch (lift == null)
    @Test
    void testGeenLiftBranchExtra() {

        Hotel hotel = maakHotel();

        // BELANGRIJK: lift null branch in beide methods
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


    // Test: persoon is GEEN gast branch (instanceof skip)
    @Test
    void testNietGastBranchExtra() {

        Hotel hotel = maakHotel();

        // Voeg "lege persoon" toe → moet alle Gast-branches skippen
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


    // Test: gast zonder lift gebruik → skip branch
    @Test
    void testGastZonderLiftGebruikBranchExtra() {

        Hotel hotel = maakHotel();

        Gast g = new Gast(1, 1);

        g.gebruiktLift = false; // skip branch hier

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


    // Test: gast zit al in lift branch (inLift == true)
    @Test
    void testGastAlInLiftBranchExtra() {

        Hotel hotel = maakHotel();

        Gast g = new Gast(1, 1);

        g.inLift = true; // skip branch

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


    // Test: gast zonder huidigVakje branch (null guard)
    @Test
    void testGastZonderVakjeBranchExtra() {

        Hotel hotel = maakHotel();

        Gast g = new Gast(1, 1);

        g.gebruiktLift = true;

        g.huidigVakje = null; // raakt continue branch

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


    // Test: notifyListeners altijd uitgevoerd branch
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


    // Test: meerdere ticks → for-loop branch coverage
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

        // raakt for (int i = 0; i < stappen; i++)
        sc.tik();

        sc.tik();

        assertDoesNotThrow(() -> sc.tik());
    }
}