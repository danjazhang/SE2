import Controller.EventController;
import Controller.HotelController;
import Controller.SimulatieController;
import Model.*;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.*;
import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventControllerTest {

    private HotelEventManager manager = new HotelEventManager(true);

    // hulpmethode: maak een hotel met lift, trap en pathfinder
    static Hotel maakHotel() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(8, 4);
        hotel.breedte = 8;
        hotel.hoogte = 4;
        Lift lift = new Lift();
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        Trap trap = new Trap(2);
        trap.posX = 8; trap.posY = 1; trap.breedte = 1; trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        hotel.pathfinder = new Pathfinder(hotel);
        return hotel;
    }

    // constructor: geen crash
    @Test void testConstructor() {
        assertNotNull(new EventController(manager));
    }

    // registreerListener: geen crash
    @Test void testRegistreerListener() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.registreerListener(event -> {}));
    }

    // setLogger: geen crash
    @Test void testSetLogger() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.setLogger(bericht -> {}));
    }

    // setHotelController: geen crash
    @Test void testSetHotelController() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.setHotelController(new HotelController()));
    }

    // setSimulatieController: geen crash
    @Test void testSetSimulatieController() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        assertDoesNotThrow(() -> ec.setSimulatieController(sc));
    }

    // notify zonder hotelcontroller: geen crash
    @Test void testNotifyZonderHotelControllerCrashetNiet() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1)));
    }

    // notify EVACUATE: logger wordt aangeroepen
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

    // notify EVACUATE zonder logger: geen crash
    @Test void testNotifyEvacuateZonderLoggerCrashetNiet() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(3, 3);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.EVACUATE, -1, -1)));
    }

    // notify GODZILLA: logger wordt aangeroepen
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

    // notify GODZILLA zonder logger: geen crash
    @Test void testNotifyGodzillaZonderLoggerCrashetNiet() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(3, 3);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.GODZILLA, -1, -1)));
    }

    // notify NONE zonder simulatiecontroller: geen crash
    @Test void testNotifyNoneZonderSimulatieControllerCrashetNiet() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.NONE, -1, -1)));
    }

    // notify NONE met simulatiecontroller: tik wordt aangeroepen
    @Test void testNotifyNoneRoeptTikAan() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        boolean[] tikGeroepen = {false};
        // voeg listener toe zodat we weten dat tik() aangeroepen werd
        hc.voegListenerToe(() -> tikGeroepen[0] = true);
        SimulatieController sc = new SimulatieController(manager, ec, hc);
        ec.setSimulatieController(sc);
        ec.notify(new HotelEvent(1, HotelEventType.NONE, -1, -1));
        assertTrue(tikGeroepen[0]);
    }

    // notify NEED_FOOD met restaurant in hotel: geen crash
    @Test void testNotifyNeedFoodMetRestaurant() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = maakHotel();
        Restaurant restaurant = new Restaurant();
        restaurant.posX = 3; restaurant.posY = 1; restaurant.breedte = 1; restaurant.hoogte = 1;
        hotel.ruimtes.add(restaurant);
        hotel.layout.plaatsRuimte(restaurant);
        Gast gast = new Gast(1, 2);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        ec.registreerHotelListeners(hotel);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.NEED_FOOD, 1, -1)));
    }

    // notify GOTO_FITNESS met fitnessruimte in hotel: geen crash
    @Test void testNotifyGotoFitnessMetFitness() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = maakHotel();
        Fitnessruimte fitness = new Fitnessruimte();
        fitness.posX = 4; fitness.posY = 1; fitness.breedte = 1; fitness.hoogte = 1;
        hotel.ruimtes.add(fitness);
        hotel.layout.plaatsRuimte(fitness);
        Gast gast = new Gast(1, 2);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        ec.registreerHotelListeners(hotel);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 1, -1)));
    }

    // notify GOTO_CINEMA met bioscoop in hotel: geen crash
    @Test void testNotifyGotoCinemaMetBioscoop() {
        EventController ec = new EventController(manager);
        HotelController hc = new HotelController();
        Hotel hotel = maakHotel();
        Bioscoop bioscoop = new Bioscoop();
        bioscoop.posX = 5; bioscoop.posY = 1; bioscoop.breedte = 1; bioscoop.hoogte = 1;
        hotel.ruimtes.add(bioscoop);
        hotel.layout.plaatsRuimte(bioscoop);
        Gast gast = new Gast(1, 2);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        ec.registreerHotelListeners(hotel);
        assertDoesNotThrow(() -> ec.notify(new HotelEvent(1, HotelEventType.GOTO_CINEMA, 1, -1)));
    }

    // stuurNaarListeners: alle listeners worden aangeroepen
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

    // registreerHotelListeners met null hotel: geen crash
    @Test void testRegistreerHotelListenersNullHotel() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.registreerHotelListeners(null));
    }

    // registreerHotelListeners met leeg hotel: geen crash
    @Test void testRegistreerHotelListenersLeegHotel() {
        EventController ec = new EventController(manager);
        Hotel hotel = new Hotel();
        assertDoesNotThrow(() -> ec.registreerHotelListeners(hotel));
    }

    // registreerHotelListeners met restaurant: setGastTerugService wordt aangeroepen
    @Test void testRegistreerHotelListenersMetRestaurant() {
        EventController ec = new EventController(manager);
        Hotel hotel = maakHotel();
        Restaurant restaurant = new Restaurant();
        restaurant.posX = 3; restaurant.posY = 1; restaurant.breedte = 1; restaurant.hoogte = 1;
        hotel.ruimtes.add(restaurant);
        hotel.layout.plaatsRuimte(restaurant);
        assertDoesNotThrow(() -> ec.registreerHotelListeners(hotel));
    }

    // registreerHotelListeners met bioscoop en fitness: geen crash
    @Test void testRegistreerHotelListenersMetBioscoopEnFitness() {
        EventController ec = new EventController(manager);
        Hotel hotel = maakHotel();
        Bioscoop bioscoop = new Bioscoop();
        bioscoop.posX = 3; bioscoop.posY = 1; bioscoop.breedte = 1; bioscoop.hoogte = 1;
        hotel.ruimtes.add(bioscoop);
        hotel.layout.plaatsRuimte(bioscoop);
        Fitnessruimte fitness = new Fitnessruimte();
        fitness.posX = 4; fitness.posY = 1; fitness.breedte = 1; fitness.hoogte = 1;
        hotel.ruimtes.add(fitness);
        hotel.layout.plaatsRuimte(fitness);
        assertDoesNotThrow(() -> ec.registreerHotelListeners(hotel));
    }

    // notificeerPersoon: geen crash
    @Test void testNotificeerPersoon() {
        EventController ec = new EventController(manager);
        Gast gast = new Gast(1, 1);
        HotelEvent evt = new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1);
        assertDoesNotThrow(() -> ec.notificeerPersoon(gast, evt));
    }

    // registreer: geen crash
    @Test void testRegistreer() {
        EventController ec = new EventController(manager);
        assertDoesNotThrow(() -> ec.registreer());
    }
}
