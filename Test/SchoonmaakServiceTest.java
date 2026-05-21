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

        // ik doe dit: ik maak een volledig hotel met layout, lift, trap, kamer en pathfinder
        // ik verwacht: dat de schoonmaakservice kan werken zonder null errors of ontbrekende dependencies

        Lift lift = new Lift(hotel);
        lift.posX = 1;
        lift.posY = 1;
        lift.breedte = 1;
        lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);
        trap.posX = 6;
        trap.posY = 1;
        trap.breedte = 1;
        trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        kamer = new Kamer();
        kamer.posX = 3;
        kamer.posY = 4;
        kamer.breedte = 1;
        kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        hotel.pathfinder = new Pathfinder(hotel);

        Gast gast = new Gast(7, 1);
        gast.setPathfinder(hotel.pathfinder);
        Vakje start = hotel.layout.krijgVakje(2, 4);
        gast.zetStartPositie(start);
        kamer.koppelGast(gast);
        hotel.voegPersoonToe(gast);

        schoonmaker = new Schoonmaker();
        schoonmaker.setPathfinder(hotel.pathfinder);
        schoonmaker.setWachtVakje(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(schoonmaker);

        service = new SchoonmaakService(hotel, null);
    }

    // =========================================================
    // 1. HAPPY PATH: CLEANING_EMERGENCY werkt volledig
    // =========================================================
    @Test
    // ik doe dit: ik trigger een CLEANING_EMERGENCY event voor een bestaande gast
    // ik verwacht: dat een vrije schoonmaker wordt toegewezen aan de kamer van de gast
    void testCleaningEmergencyVolledigeFlow() {

        HotelEvent event =
                new HotelEvent(5, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        service.onEvent(event);

        assertTrue(schoonmaker.bezig);
        assertEquals(kamer, schoonmaker.kamer);
    }

    // =========================================================
    // 2. EVENT TYPE WORDT GEGENOREERD
    // =========================================================
    @Test
    // ik doe dit: ik stuur een ander event type dan CLEANING_EMERGENCY
    // ik verwacht: dat de schoonmaakservice niets verandert
    void testAnderEventWordtGenegeerd() {

        HotelEvent event =
                new HotelEvent(5, HotelEventType.CHECK_IN, 7, -1);

        service.onEvent(event);

        assertFalse(schoonmaker.bezig);
        assertNull(schoonmaker.kamer);
    }

    // =========================================================
    // 3. hotel.pathfinder == null branch
    // =========================================================
    @Test
    // ik doe dit: ik zet de pathfinder op null en trigger een schoonmaak event
    // ik verwacht: dat de service veilig faalt zonder schoonmaker aan te passen
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
    // ik doe dit: ik geef een gastId die niet bestaat in het hotel
    // ik verwacht: dat er geen schoonmaker wordt toegewezen
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
    // ik doe dit: ik voeg een gast toe zonder kamer en trigger een schoonmaak event
    // ik verwacht: dat de service niets kan doen en schoonmaker vrij blijft
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
    // ik doe dit: ik zet de enige schoonmaker op bezet
    // ik verwacht: dat er geen nieuwe schoonmaaktaak wordt gestart
    void testGeenVrijeSchoonmaker() {

        schoonmaker.bezig = true;

        HotelEvent event =
                new HotelEvent(1, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        service.onEvent(event);

        assertTrue(schoonmaker.bezig);
        assertNull(schoonmaker.kamer);
    }

    // =========================================================
    // 7. LOGGER NULL branch
    // =========================================================
    @Test
    // ik doe dit: ik maak een service zonder logger en trigger een event
    // ik verwacht: dat er geen crash optreedt en logica nog werkt
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
    // ik doe dit: ik geef een actieve logger mee aan de service
    // ik verwacht: dat er logging gebeurt tijdens event verwerking
    void testMetLogger() {

        StringBuilder log = new StringBuilder();

        SchoonmaakService s =
                new SchoonmaakService(hotel, log::append);

        HotelEvent event =
                new HotelEvent(5, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        s.onEvent(event);

        assertTrue(log.length() > 0);
    }

    // =========================================================
    // 9. MEERDERE EVENTS (stress branch coverage)
    // =========================================================
    @Test
    // ik doe dit: ik stuur meerdere events achter elkaar (CHECK_IN + CLEANING_EMERGENCY)
    // ik verwacht: dat alleen CLEANING_EMERGENCY effect heeft op schoonmaker
    void testMeerdereEventsAchterElkaar() {

        HotelEvent e1 =
                new HotelEvent(1, HotelEventType.CHECK_IN, 7, -1);

        HotelEvent e2 =
                new HotelEvent(2, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        service.onEvent(e1);
        service.onEvent(e2);

        assertTrue(schoonmaker.bezig);
    }

    // =========================================================
    // 10. SERVICE HERGEBRUIK (setLogger branch)
    // =========================================================
    @Test
    // ik doe dit: ik verander de logger via setLogger en trigger daarna een event
    // ik verwacht: dat de nieuwe logger effectief gebruikt wordt
    void testSetLogger() {

        StringBuilder log = new StringBuilder();

        service.setLogger(log::append);

        HotelEvent event =
                new HotelEvent(1, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        service.onEvent(event);

        assertTrue(log.length() > 0);
    }
}