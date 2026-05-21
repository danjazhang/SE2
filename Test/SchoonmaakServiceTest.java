import Model.Hotel;
import Model.Pathfinder;
import Model.SchoonmaakService;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SchoonmaakServiceTest {

    private Hotel hotel;
    private SchoonmaakService service;
    private Kamer kamer;
    private Schoonmaker schoonmaker;

    // =========================
    // SETUP: klein werkend hotel
    // =========================
    @BeforeEach
    void setUp() {

        hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;

        // lift (nodig voor Pathfinder)
        Lift lift = new Lift(hotel);
        lift.posX = 1;
        lift.posY = 1;
        lift.breedte = 1;
        lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        // trap
        Trap trap = new Trap(2);
        trap.posX = 6;
        trap.posY = 1;
        trap.breedte = 1;
        trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        // kamer
        kamer = new Kamer();
        kamer.posX = 3;
        kamer.posY = 4;
        kamer.breedte = 1;
        kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        // pathfinder verplicht
        hotel.pathfinder = new Pathfinder(hotel);

        // gast koppelen aan kamer
        Gast gast = new Gast(7, 1);
        gast.setPathfinder(hotel.pathfinder);
        Vakje start = hotel.layout.krijgVakje(2, 4);
        gast.zetStartPositie(start);
        kamer.koppelGast(gast);
        hotel.voegPersoonToe(gast);

        // schoonmaker
        schoonmaker = new Schoonmaker();
        schoonmaker.setPathfinder(hotel.pathfinder);
        schoonmaker.setWachtVakje(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(schoonmaker);

        // service zonder logger
        service = new SchoonmaakService(hotel, null);
    }

    // =========================================================
    // 1. HAPPY PATH: CLEANING_EMERGENCY werkt volledig
    // =========================================================
    @Test
    void testCleaningEmergencyVolledigeFlow() {

        // event dat een schoonmaak-noodgeval triggert
        HotelEvent event =
                new HotelEvent(5, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        service.onEvent(event);

        // schoonmaker moet toegewezen worden
        assertTrue(schoonmaker.bezig);

        // kamer moet correct gekoppeld zijn
        assertEquals(kamer, schoonmaker.kamer);

        // we testen NIET intern pathing state → dat is Pathfinder verantwoordelijkheid
    }

    // =========================================================
    // 2. EVENT TYPE WORDT GEGENOREERD
    // =========================================================
    @Test
    void testAnderEventWordtGenegeerd() {

        HotelEvent event =
                new HotelEvent(5, HotelEventType.CHECK_IN, 7, -1);

        service.onEvent(event);

        // niets mag gebeuren
        assertFalse(schoonmaker.bezig);
        assertNull(schoonmaker.kamer);
    }

    // =========================================================
    // 3. hotel.pathfinder == null branch
    // =========================================================
    @Test
    void testZonderPathfinder() {

        hotel.pathfinder = null;

        HotelEvent event =
                new HotelEvent(1, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        service.onEvent(event);

        assertFalse(schoonmaker.bezig);
    }

    // =========================================================
    // 4. gast niet gevonden branch
    // =========================================================
    @Test
    void testGastNietGevonden() {

        HotelEvent event =
                new HotelEvent(1, HotelEventType.CLEANING_EMERGENCY, 999, -1);

        service.onEvent(event);

        assertFalse(schoonmaker.bezig);
    }

    // =========================================================
    // 5. gast zonder kamer branch
    // =========================================================
    @Test
    void testGastZonderKamer() {

        Gast losseGast = new Gast(100, 1);
        hotel.voegPersoonToe(losseGast);

        HotelEvent event =
                new HotelEvent(1, HotelEventType.CLEANING_EMERGENCY, 100, -1);

        service.onEvent(event);

        assertFalse(schoonmaker.bezig);
    }

    // =========================================================
    // 6. geen vrije schoonmaker branch
    // =========================================================
    @Test
    void testGeenVrijeSchoonmaker() {

        // maak schoonmaker bezet
        schoonmaker.bezig = true;

        HotelEvent event =
                new HotelEvent(1, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        service.onEvent(event);

        // status mag niet veranderen
        assertTrue(schoonmaker.bezig);
        assertNull(schoonmaker.kamer);
    }

    // =========================================================
    // 7. LOGGER NULL branch
    // =========================================================
    @Test
    void testZonderLoggerGeenCrash() {

        SchoonmaakService s =
                new SchoonmaakService(hotel, null);

        HotelEvent event =
                new HotelEvent(5, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        assertDoesNotThrow(() -> s.onEvent(event));

        assertTrue(schoonmaker.bezig);
    }

    // =========================================================
    // 8. LOGGER WEL ACTIEF branch
    // =========================================================
    @Test
    void testMetLogger() {

        StringBuilder log = new StringBuilder();

        SchoonmaakService s =
                new SchoonmaakService(hotel, log::append);

        HotelEvent event =
                new HotelEvent(5, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        s.onEvent(event);

        // logger moet iets hebben geschreven
        assertTrue(log.length() > 0);
    }

    // =========================================================
    // 9. MEERDERE EVENTS (stress branch coverage)
    // =========================================================
    @Test
    void testMeerdereEventsAchterElkaar() {

        HotelEvent e1 =
                new HotelEvent(1, HotelEventType.CHECK_IN, 7, -1);

        HotelEvent e2 =
                new HotelEvent(2, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        service.onEvent(e1); // wordt genegeerd
        service.onEvent(e2); // wordt verwerkt

        assertTrue(schoonmaker.bezig);
    }

    // =========================================================
    // 10. SERVICE HERGEBRUIK (setLogger branch)
    // =========================================================
    @Test
    void testSetLogger() {

        StringBuilder log = new StringBuilder();

        service.setLogger(log::append);

        HotelEvent event =
                new HotelEvent(1, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        service.onEvent(event);

        assertTrue(log.length() > 0);
    }
}