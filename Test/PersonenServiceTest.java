import Model.Hotel;
import Model.Pathfinder;
import Model.PersonenService;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonenServiceTest {

    private Hotel hotel;
    private PersonenService service;

    // -------------------------------------------------
    // SETUP: klein testhotel met layout + pathfinder
    // -------------------------------------------------
    @BeforeEach
    void setUp() {

        // ik doe dit: ik maak een nieuw Hotel en initialiseer een basis layout + services
        // ik verwacht: dat alle tests starten met een schone, consistente hotelsituatie
        hotel = new Hotel();

        // eenvoudige grid layout
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;

        // lift voor volledigheid (niet essentieel hier maar voorkomt null issues)
        Lift lift = new Lift(hotel);
        lift.posX = 1;
        lift.posY = 1;
        lift.breedte = 1;
        lift.hoogte = 4;

        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        // trap voor volledigheid
        Trap trap = new Trap(2);
        trap.posX = 6;
        trap.posY = 1;
        trap.breedte = 1;
        trap.hoogte = 4;

        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);

        service = new PersonenService(hotel);
    }

    // -------------------------------------------------
    // CONSTRUCTOR TEST
    // -------------------------------------------------

    @Test
        // ik doe dit: ik maak een PersonenService aan
        // ik verwacht: dat de service niet null is en correct geïnitialiseerd wordt
    void testConstructorMaaktService() {
        assertNotNull(service);
    }

    // -------------------------------------------------
    // MAAK GAST TESTS (factory + hotel integratie)
    // -------------------------------------------------

    @Test
        // ik doe dit: ik maak een gast via de service en voeg die toe aan het hotel
        // ik verwacht: dat de gast correct wordt aangemaakt en in hotel.personen komt
    void testMaakGastVoegtToeAanHotel() {
        Vakje start = hotel.layout.krijgVakje(2, 1);

        Gast g = service.maakGast(10, 3, start);

        assertNotNull(g);
        assertEquals(1, hotel.personen.size());
    }

    @Test
        // ik doe dit: ik maak een gast met een specifiek ID
        // ik verwacht: dat het ID correct wordt opgeslagen in het Gast object
    void testMaakGastZetCorrecteId() {
        Vakje start = hotel.layout.krijgVakje(2, 1);

        Gast g = service.maakGast(99, 2, start);

        assertEquals(99, g.gastId);
    }

    @Test
        // ik doe dit: ik geef een null startvakje aan maakGast
        // ik verwacht: dat de methode geen crash veroorzaakt
    void testMaakGastMetNullStartVakje() {
        assertDoesNotThrow(() -> service.maakGast(1, 2, null));
    }

    @Test
        // ik doe dit: ik maak meerdere gasten via de service
        // ik verwacht: dat alle gasten worden toegevoegd aan hotel.personen
    void testMeerdereGastenWordenToegevoegd() {
        Vakje v = hotel.layout.krijgVakje(2, 1);

        service.maakGast(1, 2, v);
        service.maakGast(2, 3, v);
        service.maakGast(3, 4, v);

        assertEquals(3, hotel.personen.size());
    }

    // -------------------------------------------------
    // VIND GAST TESTS (loop coverage + branch true/false)
    // -------------------------------------------------

    @Test
        // ik doe dit: ik zoek een bestaande gast op ID
        // ik verwacht: dat de juiste gast wordt teruggegeven
    void testVindGastBestaat() {
        Vakje v = hotel.layout.krijgVakje(2, 1);

        service.maakGast(7, 2, v);

        Gast gevonden = service.vindGast(7);

        assertNotNull(gevonden);
        assertEquals(7, gevonden.gastId);
    }

    @Test
        // ik doe dit: ik zoek een gast die niet bestaat
        // ik verwacht: dat de methode null teruggeeft
    void testVindGastNietBestaand() {
        assertNull(service.vindGast(123));
    }

    @Test
        // ik doe dit: ik mix gast en andere objecten in hotel.personen
        // ik verwacht: dat alleen de juiste gast gevonden wordt
    void testVindGastMetMeerderePersonenMixed() {
        Vakje v = hotel.layout.krijgVakje(2, 1);

        service.maakGast(1, 2, v);

        Schoonmaker s = new Schoonmaker();
        hotel.voegPersoonToe(s);

        assertNotNull(service.vindGast(1));
    }

    // -------------------------------------------------
    // VIND VRIJE SCHOONMAKER TESTS
    // -------------------------------------------------

    @Test
        // ik doe dit: ik voeg een vrije schoonmaker toe
        // ik verwacht: dat deze wordt teruggevonden
    void testVindVrijeSchoonmakerBestaat() {
        Schoonmaker s = new Schoonmaker();

        hotel.voegPersoonToe(s);

        Schoonmaker gevonden = service.vindVrijeSchoonmaker();

        assertEquals(s, gevonden);
    }

    @Test
        // ik doe dit: ik zet een schoonmaker op bezig
        // ik verwacht: dat er geen vrije schoonmaker gevonden wordt
    void testVindVrijeSchoonmakerBezet() {
        Schoonmaker s = new Schoonmaker();

        s.bezig = true;

        hotel.voegPersoonToe(s);

        assertNull(service.vindVrijeSchoonmaker());
    }

    @Test
        // ik doe dit: ik zoek in een lege schoonmakerlijst
        // ik verwacht: dat de methode null teruggeeft
    void testVindVrijeSchoonmakerGeenSchoonmakers() {
        assertNull(service.vindVrijeSchoonmaker());
    }

    @Test
        // ik doe dit: ik heb meerdere schoonmakers waarvan één vrij is
        // ik verwacht: dat de eerste vrije schoonmaker wordt teruggegeven
    void testVindVrijeSchoonmakerMeerdere() {
        Schoonmaker s1 = new Schoonmaker();
        Schoonmaker s2 = new Schoonmaker();

        s2.bezig = true;

        hotel.voegPersoonToe(s1);
        hotel.voegPersoonToe(s2);

        assertEquals(s1, service.vindVrijeSchoonmaker());
    }

    // -------------------------------------------------
    // EDGE CASES / BRANCH STRESS TESTS
    // -------------------------------------------------

    @Test
        // ik doe dit: ik zoek in een hotel zonder personen
        // ik verwacht: dat vindGast null teruggeeft
    void testVindGastInLegeHotelLijst() {
        assertNull(service.vindGast(1));
    }

    @Test
        // ik doe dit: ik maak hotel.personen expliciet leeg
        // ik verwacht: dat alle zoekfuncties veilig null teruggeven
    void testServiceMetNullHotelPersonen() {
        hotel.personen.clear();

        assertNull(service.vindGast(1));
        assertNull(service.vindVrijeSchoonmaker());
    }

    @Test
        // ik doe dit: ik roep dezelfde zoekmethode meerdere keren aan
        // ik verwacht: dat het resultaat consistent blijft
    void testHerhaaldZoekenIsConsistent() {
        Vakje v = hotel.layout.krijgVakje(2, 1);

        service.maakGast(5, 2, v);

        assertEquals(5, service.vindGast(5).gastId);
        assertEquals(5, service.vindGast(5).gastId);
    }

    @Test
        // ik doe dit: ik combineer gasten en schoonmakers in één hotel
        // ik verwacht: dat beide zoekmethodes correct blijven werken
    void testMixGastEnSchoonmakerZoekLogica() {
        Vakje v = hotel.layout.krijgVakje(2, 1);

        service.maakGast(1, 2, v);

        Schoonmaker s = new Schoonmaker();
        hotel.voegPersoonToe(s);

        assertNotNull(service.vindGast(1));
        assertNotNull(service.vindVrijeSchoonmaker());
    }
}