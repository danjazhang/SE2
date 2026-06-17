import Model.Hotel;
import Model.Pathfinder;
import Model.PersonenService;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor PersonenService: maakGast, vindGast, vindVrijeSchoonmaker
public class PersonenServiceTest {

    private Hotel hotel;
    private PersonenService service;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;
        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        lift.initWachtrijen(4);
        Trap trap = new Trap(2);
        trap.posX = 6; trap.posY = 1; trap.breedte = 1; trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        hotel.pathfinder = new Pathfinder(hotel);
        service = new PersonenService(hotel);
    }

    // maakGast: gast wordt aangemaakt en toegevoegd aan hotel
    @Test void testMaakGastToegeVoegd() {
        Gast g = service.maakGast(1, 2, hotel.layout.krijgVakje(2, 1));
        assertTrue(hotel.personen.contains(g));
    }

    // maakGast: gast heeft correct id en sterren
    @Test void testMaakGastIdEnSterren() {
        Gast g = service.maakGast(7, 3, hotel.layout.krijgVakje(2, 1));
        assertEquals(7, g.gastId);
        assertEquals(3, g.gewensteSterren);
    }

    // maakGast: gast staat op startvakje
    @Test void testMaakGastStartposicie() {
        Gast g = service.maakGast(1, 1, hotel.layout.krijgVakje(3, 2));
        assertEquals(hotel.layout.krijgVakje(3, 2), g.huidigVakje);
    }

    // vindGast: geeft de juiste gast terug op basis van id
    @Test void testVindGast() {
        service.maakGast(5, 1, hotel.layout.krijgVakje(2, 1));
        Gast g = service.vindGast(5);
        assertNotNull(g);
        assertEquals(5, g.gastId);
    }

    // vindGast: geeft null voor onbekend id
    @Test void testVindGastNietGevonden() {
        assertNull(service.vindGast(999));
    }

    // vindGast: onderscheidt meerdere gasten correct
    @Test void testVindGastMeerdere() {
        service.maakGast(1, 1, hotel.layout.krijgVakje(2, 1));
        service.maakGast(2, 2, hotel.layout.krijgVakje(3, 1));
        assertEquals(1, service.vindGast(1).gastId);
        assertEquals(2, service.vindGast(2).gastId);
    }

    // vindVrijeSchoonmaker: geeft vrije schoonmaker terug
    @Test void testVindVrijeSchoonmaker() {
        Schoonmaker s = new Schoonmaker();
        s.bezig = false;
        hotel.voegPersoonToe(s);
        assertSame(s, service.vindVrijeSchoonmaker());
    }

    // vindVrijeSchoonmaker: geeft null als schoonmaker bezig is
    @Test void testVindVrijeSchoonmakerBezig() {
        Schoonmaker s = new Schoonmaker();
        s.bezig = true;
        hotel.voegPersoonToe(s);
        assertNull(service.vindVrijeSchoonmaker());
    }

    // vindVrijeSchoonmaker: geeft null als er geen schoonmakers zijn
    @Test void testVindVrijeSchoonmakerGeenSchoonmakers() {
        assertNull(service.vindVrijeSchoonmaker());
    }

    // vindVrijeSchoonmaker: gasten worden overgeslagen
    @Test void testVindVrijeSchoonmakerNegeerGasten() {
        hotel.voegPersoonToe(new Gast(1, 1));
        hotel.voegPersoonToe(new Gast(2, 2));
        assertNull(service.vindVrijeSchoonmaker());
    }
}
