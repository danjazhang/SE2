import Model.Hotel;
import Model.Pathfinder;
import Model.PersonenFactory;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersonenFactoryTest {

    // hulpmethode: maak een hotel met pathfinder
    private Hotel maakHotel() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 3);
        hotel.breedte = 5;
        hotel.hoogte = 3;
        Lift lift = new Lift();
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 3;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        Trap trap = new Trap(2);
        trap.posX = 5; trap.posY = 1; trap.breedte = 1; trap.hoogte = 3;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        hotel.pathfinder = new Pathfinder(hotel);
        return hotel;
    }

    // maakGast: gast heeft juiste id, sterren en startpositie
    @Test void testMaakGast() {
        Hotel hotel = maakHotel();
        PersonenFactory f = new PersonenFactory();
        Vakje v = hotel.layout.krijgVakje(2, 1);
        Gast g = f.maakGast(1, 3, hotel.pathfinder, v);
        assertEquals(1, g.gastId);
        assertEquals(3, g.gewensteSterren);
        assertEquals(v, g.huidigVakje);
    }

    // maakGast: gast zonder startvakje heeft null huidigVakje
    @Test void testMaakGastZonderStartVakje() {
        Hotel hotel = maakHotel();
        PersonenFactory f = new PersonenFactory();
        Gast g = f.maakGast(1, 2, hotel.pathfinder, null);
        assertNull(g.huidigVakje);
    }

    // maakGast: pathfinder null geeft geen crash
    @Test void testMaakGastMetNullPathfinder() {
        PersonenFactory f = new PersonenFactory();
        assertDoesNotThrow(() -> f.maakGast(1, 2, null, null));
    }

    // maakSchoonmaker: schoonmaker heeft juiste startpositie
    @Test void testMaakSchoonmaker() {
        Hotel hotel = maakHotel();
        PersonenFactory f = new PersonenFactory();
        Vakje v = hotel.layout.krijgVakje(2, 1);
        Schoonmaker s = f.maakSchoonmaker(hotel.pathfinder, v);
        assertEquals(v, s.huidigVakje);
    }

    // maakSchoonmaker: schoonmaker zonder startvakje heeft null huidigVakje
    @Test void testMaakSchoonmakerZonderStartVakje() {
        Hotel hotel = maakHotel();
        PersonenFactory f = new PersonenFactory();
        Schoonmaker s = f.maakSchoonmaker(hotel.pathfinder, null);
        assertNull(s.huidigVakje);
    }

    // maakSchoonmaker: pathfinder null geeft geen crash
    @Test void testMaakSchoonmakerMetNullPathfinder() {
        PersonenFactory f = new PersonenFactory();
        assertDoesNotThrow(() -> f.maakSchoonmaker(null, null));
    }
}
