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

    // maak een hotel met pathfinder voor elke test
    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;

        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);
        trap.posX = 6; trap.posY = 1; trap.breedte = 1; trap.hoogte = 4;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);
        service = new PersonenService(hotel);
    }

    // constructor: service wordt aangemaakt zonder crash
    @Test void testConstructor() {
        assertNotNull(service);
    }

    // maakGast: gast wordt aangemaakt en toegevoegd aan hotel
    @Test void testMaakGastVoegtToeAanHotel() {
        Vakje v = hotel.layout.krijgVakje(2, 1);
        service.maakGast(1, v);
        assertEquals(1, hotel.personen.size());
    }

    // maakGast: gast heeft juiste id
    @Test void testMaakGastHeeftJuistId() {
        Vakje v = hotel.layout.krijgVakje(2, 1);
        Gast g = service.maakGast(5, v);
        assertEquals(5, g.gastId);
    }

    // maakGast: gast heeft juiste startpositie
    @Test void testMaakGastHeeftStartpositie() {
        Vakje v = hotel.layout.krijgVakje(2, 1);
        Gast g = service.maakGast(1, v);
        assertEquals(v, g.huidigVakje);
    }

    // maakGast: geen crash met null startvakje
    @Test void testMaakGastZonderStartvakje() {
        assertDoesNotThrow(() -> service.maakGast(1, null));
    }

    // vindGast: geeft juiste gast terug op basis van id
    @Test void testVindGast() {
        Vakje v = hotel.layout.krijgVakje(2, 1);
        service.maakGast(3, v);
        Gast gevonden = service.vindGast(3);
        assertNotNull(gevonden);
        assertEquals(3, gevonden.gastId);
    }

    // vindGast: geeft null terug als gast niet bestaat
    @Test void testVindGastNietGevonden() {
        assertNull(service.vindGast(99));
    }

    // vindVrijeSchoonmaker: geeft vrije schoonmaker terug
    @Test void testVindVrijeSchoonmaker() {
        Schoonmaker s = new Schoonmaker();
        hotel.voegPersoonToe(s);
        Schoonmaker gevonden = service.vindVrijeSchoonmaker();
        assertNotNull(gevonden);
        assertEquals(s, gevonden);
    }

    // vindVrijeSchoonmaker: geeft null terug als alle schoonmakers bezig zijn
    @Test void testVindVrijeSchoonmakerAlleBezig() {
        Schoonmaker s = new Schoonmaker();
        s.bezig = true;
        hotel.voegPersoonToe(s);
        assertNull(service.vindVrijeSchoonmaker());
    }

    // vindVrijeSchoonmaker: geeft null terug als er geen schoonmakers zijn
    @Test void testVindVrijeSchoonmakerGeenSchoonmakers() {
        assertNull(service.vindVrijeSchoonmaker());
    }
}
