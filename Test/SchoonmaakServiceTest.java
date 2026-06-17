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

    // =========================================================
    // 11. verwerkWachtendeTaken: pathfinder null branch
    // =========================================================
    @Test
    // ik doe dit: ik zet een vuile kamer in de wachtrij maar haal de pathfinder weg
    // ik verwacht: dat er niets wordt toegewezen en de service veilig stopt
    void testVerwerkWachtendeTakenZonderPathfinder() {

        kamer.schoon = false;
        hotel.voegWachtendeSchoonmaakToe(kamer);
        hotel.pathfinder = null;

        service.verwerkWachtendeTaken(10);

        assertFalse(schoonmaker.bezig);
        assertTrue(hotel.wachtendeSchoonmaakKamers.contains(kamer));
    }

    // =========================================================
    // 12. verwerkWachtendeTaken: vuile kamer wordt toegewezen
    // =========================================================
    @Test
    // ik doe dit: ik zet een vuile kamer in de wachtrij
    // ik verwacht: dat de vrije schoonmaker deze kamer krijgt en de wachtrij leeg wordt
    void testVerwerkWachtendeTakenWijstVuileKamerToe() {

        kamer.schoon = false;
        hotel.voegWachtendeSchoonmaakToe(kamer);

        service.verwerkWachtendeTaken(12);

        assertTrue(schoonmaker.bezig);
        assertSame(kamer, schoonmaker.kamer);
        assertFalse(hotel.wachtendeSchoonmaakKamers.contains(kamer));
    }

    // =========================================================
    // 13. verwerkWachtendeTaken: logger branch
    // =========================================================
    @Test
    // ik doe dit: ik verwerk een wachttaak met logger
    // ik verwacht: dat de toewijzing gelogd wordt
    void testVerwerkWachtendeTakenMetLogger() {

        StringBuilder log = new StringBuilder();
        service.setLogger(log::append);
        kamer.schoon = false;
        kamer.kamernummer = 401;
        hotel.voegWachtendeSchoonmaakToe(kamer);

        service.verwerkWachtendeTaken(12);

        assertTrue(log.toString().contains("401"));
    }

    // =========================================================
    // 14. verwerkWachtendeTaken: ruimt ongeldige wachttaken op
    // =========================================================
    @Test
    // ik doe dit: ik stop null, schone kamers en al toegewezen kamers in de wachtrij
    // ik verwacht: dat alleen de echte vuile wachttaak overblijft en wordt ingepland
    void testVerwerkWachtendeTakenRuimtOngeldigeTakenOp() {

        Kamer schoneKamer = new Kamer();
        schoneKamer.schoon = true;

        Kamer alToegewezen = new Kamer();
        alToegewezen.schoon = false;
        Schoonmaker bezetteSchoonmaker = new Schoonmaker();
        bezetteSchoonmaker.kamer = alToegewezen;
        hotel.voegPersoonToe(bezetteSchoonmaker);

        Kamer vuileKamer = new Kamer();
        vuileKamer.posX = 4;
        vuileKamer.posY = 4;
        vuileKamer.breedte = 1;
        vuileKamer.hoogte = 1;
        vuileKamer.schoon = false;
        hotel.ruimtes.add(vuileKamer);
        hotel.layout.plaatsRuimte(vuileKamer);

        hotel.wachtendeSchoonmaakKamers.add(null);
        hotel.voegWachtendeSchoonmaakToe(schoneKamer);
        hotel.voegWachtendeSchoonmaakToe(alToegewezen);
        hotel.voegWachtendeSchoonmaakToe(vuileKamer);

        service.verwerkWachtendeTaken(20);

        assertSame(vuileKamer, schoonmaker.kamer);
        assertFalse(hotel.wachtendeSchoonmaakKamers.contains(null));
        assertFalse(hotel.wachtendeSchoonmaakKamers.contains(schoneKamer));
        assertFalse(hotel.wachtendeSchoonmaakKamers.contains(alToegewezen));
        assertFalse(hotel.wachtendeSchoonmaakKamers.contains(vuileKamer));
    }

    // =========================================================
    // 15. verwerkWachtendeTaken: kiest dichtstbijzijnde vrije schoonmaker
    // =========================================================
    @Test
    // ik doe dit: ik voeg een tweede schoonmaker toe die dichter bij de kamer staat
    // ik verwacht: dat de dichtstbijzijnde schoonmaker de taak krijgt
    void testVerwerkWachtendeTakenKiestDichtstbijzijndeSchoonmaker() {

        Schoonmaker dichtbij = new Schoonmaker();
        dichtbij.setPathfinder(hotel.pathfinder);
        dichtbij.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        dichtbij.setWachtVakje(hotel.layout.krijgVakje(3, 3));
        hotel.voegPersoonToe(dichtbij);

        kamer.schoon = false;
        hotel.voegWachtendeSchoonmaakToe(kamer);

        service.verwerkWachtendeTaken(30);

        assertFalse(schoonmaker.bezig);
        assertTrue(dichtbij.bezig);
        assertSame(kamer, dichtbij.kamer);
    }

    // =========================================================
    // 16. verwerkWachtendeTaken: geen vrije schoonmaker laat taak wachten
    // =========================================================
    @Test
    // ik doe dit: ik zet alle schoonmakers op bezig en voeg een vuile kamer toe
    // ik verwacht: dat de kamer in de wachtrij blijft
    void testVerwerkWachtendeTakenGeenVrijeSchoonmaker() {

        schoonmaker.bezig = true;
        kamer.schoon = false;
        hotel.voegWachtendeSchoonmaakToe(kamer);

        service.verwerkWachtendeTaken(40);

        assertTrue(hotel.wachtendeSchoonmaakKamers.contains(kamer));
        assertNull(schoonmaker.kamer);
    }

    // =========================================================
    // 17. verwerkWachtendeTaken: schoonmaker zonder positie wordt overgeslagen
    // =========================================================
    @Test
    // ik doe dit: ik verwijder ook het wachtvakje van de enige schoonmaker
    // ik verwacht: dat hij geen kandidaat is en de taak blijft wachten
    void testVerwerkWachtendeTakenSchoonmakerZonderStartWordtOvergeslagen() {

        schoonmaker.wachtVakje = null;
        kamer.schoon = false;
        hotel.voegWachtendeSchoonmaakToe(kamer);

        service.verwerkWachtendeTaken(50);

        assertFalse(schoonmaker.bezig);
        assertTrue(hotel.wachtendeSchoonmaakKamers.contains(kamer));
    }

    // =========================================================
    // 18. verwerkWachtendeTaken: meerdere taken worden in een loop uitgedeeld
    // =========================================================
    @Test
    // ik doe dit: ik maak twee vuile kamers en twee vrije schoonmakers
    // ik verwacht: dat beide kamers worden toegewezen
    void testVerwerkWachtendeTakenDeeltMeerdereTakenUit() {

        Kamer tweedeKamer = new Kamer();
        tweedeKamer.posX = 4;
        tweedeKamer.posY = 4;
        tweedeKamer.breedte = 1;
        tweedeKamer.hoogte = 1;
        tweedeKamer.schoon = false;
        hotel.ruimtes.add(tweedeKamer);
        hotel.layout.plaatsRuimte(tweedeKamer);

        Schoonmaker tweedeSchoonmaker = new Schoonmaker();
        tweedeSchoonmaker.setPathfinder(hotel.pathfinder);
        tweedeSchoonmaker.setWachtVakje(hotel.layout.krijgVakje(5, 1));
        hotel.voegPersoonToe(tweedeSchoonmaker);

        kamer.schoon = false;
        hotel.voegWachtendeSchoonmaakToe(kamer);
        hotel.voegWachtendeSchoonmaakToe(tweedeKamer);

        service.verwerkWachtendeTaken(60);

        assertTrue(schoonmaker.bezig);
        assertTrue(tweedeSchoonmaker.bezig);
        assertTrue(hotel.wachtendeSchoonmaakKamers.isEmpty());
    }

    // =========================================================
    // 19. verwerkWachtendeTaken: vrije schoonmaker gaat terug naar wachtplek
    // =========================================================
    @Test
    // ik doe dit: ik heb geen wachttaken en een vrije schoonmaker staat niet op zijn wachtplek
    // ik verwacht: dat hij een route naar zijn wachtplek krijgt
    void testVerwerkWachtendeTakenStuurtVrijeSchoonmakerTerug() {

        schoonmaker.setPathfinder(hotel.pathfinder);
        schoonmaker.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        Vakje wachtVakje = hotel.layout.krijgVakje(2, 1);
        schoonmaker.setWachtVakje(wachtVakje);

        service.verwerkWachtendeTaken(70);

        assertSame(wachtVakje, schoonmaker.doelVakje);
    }

    // =========================================================
    // 20. verwerkWachtendeTaken: terugsturen wordt overgeslagen als taken wachten
    // =========================================================
    @Test
    // ik doe dit: ik laat een wachttaak staan zonder beschikbare schoonmaker
    // ik verwacht: dat een andere vrije schoonmaker niet teruggestuurd wordt zolang er werk wacht
    void testVerwerkWachtendeTakenStuurtNietTerugAlsTakenWachten() {

        schoonmaker.bezig = true;
        kamer.schoon = false;
        hotel.voegWachtendeSchoonmaakToe(kamer);

        Schoonmaker vrijeSchoonmaker = new Schoonmaker();
        vrijeSchoonmaker.setPathfinder(hotel.pathfinder);
        vrijeSchoonmaker.zetStartPositie(hotel.layout.krijgVakje(4, 1));
        vrijeSchoonmaker.setWachtVakje(hotel.layout.krijgVakje(2, 1));
        vrijeSchoonmaker.bezig = true; // voorkomt dat hij de taak oppakt
        hotel.voegPersoonToe(vrijeSchoonmaker);

        service.verwerkWachtendeTaken(80);

        assertNull(vrijeSchoonmaker.doelVakje);
        assertTrue(hotel.wachtendeSchoonmaakKamers.contains(kamer));
    }
}
