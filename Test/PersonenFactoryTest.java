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

// Tests voor PersonenFactory: maakGast, maakSchoonmaker, maakStandaardSchoonmakers
public class PersonenFactoryTest {

    static Hotel maakHotel() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(8, 6);
        hotel.breedte = 8;
        hotel.hoogte = 6;
        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 6;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        lift.initWachtrijen(6);
        Trap trap = new Trap(2);
        trap.posX = 7; trap.posY = 1; trap.breedte = 2; trap.hoogte = 6;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        hotel.pathfinder = new Pathfinder(hotel);
        return hotel;
    }

    // maakGast: gast heeft juiste id en sterren
    @Test void testMaakGastIdEnSterren() {
        Hotel hotel = maakHotel();
        PersonenFactory factory = new PersonenFactory();
        Vakje start = hotel.layout.krijgVakje(2, 1);
        Gast g = factory.maakGast(5, 3, hotel.pathfinder, start);
        assertEquals(5, g.gastId);
        assertEquals(3, g.gewensteSterren);
    }

    // maakGast: gast staat op het startvakje
    @Test void testMaakGastStartPositie() {
        Hotel hotel = maakHotel();
        PersonenFactory factory = new PersonenFactory();
        Vakje start = hotel.layout.krijgVakje(3, 2);
        Gast g = factory.maakGast(1, 1, hotel.pathfinder, start);
        assertEquals(start, g.huidigVakje);
    }

    // maakGast: gast heeft de pathfinder
    @Test void testMaakGastPathfinder() {
        Hotel hotel = maakHotel();
        PersonenFactory factory = new PersonenFactory();
        Gast g = factory.maakGast(1, 1, hotel.pathfinder, hotel.layout.krijgVakje(2, 1));
        assertEquals(hotel.pathfinder, g.getPathfinder());
    }

    // maakGast: null startVakje geeft geen crash
    @Test void testMaakGastNullStart() {
        Hotel hotel = maakHotel();
        PersonenFactory factory = new PersonenFactory();
        assertDoesNotThrow(() -> factory.maakGast(1, 1, hotel.pathfinder, null));
    }

    // maakSchoonmaker: schoonmaker staat op het startvakje
    @Test void testMaakSchoonmakerStartPositie() {
        Hotel hotel = maakHotel();
        PersonenFactory factory = new PersonenFactory();
        Vakje start = hotel.layout.krijgVakje(4, 1);
        Schoonmaker s = factory.maakSchoonmaker(hotel.pathfinder, start);
        assertEquals(start, s.huidigVakje);
    }

    // maakSchoonmaker: schoonmaker heeft de pathfinder
    @Test void testMaakSchoonmakerPathfinder() {
        Hotel hotel = maakHotel();
        PersonenFactory factory = new PersonenFactory();
        Schoonmaker s = factory.maakSchoonmaker(hotel.pathfinder, hotel.layout.krijgVakje(2, 1));
        assertEquals(hotel.pathfinder, s.getPathfinder());
    }

    // maakSchoonmaker: null startVakje geeft geen crash
    @Test void testMaakSchoonmakerNullStart() {
        Hotel hotel = maakHotel();
        PersonenFactory factory = new PersonenFactory();
        assertDoesNotThrow(() -> factory.maakSchoonmaker(hotel.pathfinder, null));
    }

    // maakStandaardSchoonmakers: voegt 2 schoonmakers toe
    @Test void testMaakStandaardSchoonmakers() {
        Hotel hotel = maakHotel();
        PersonenFactory factory = new PersonenFactory();
        factory.maakStandaardSchoonmakers(hotel, hotel.breedte, hotel.hoogte, 2);
        long schoonmakers = hotel.personen.stream().filter(p -> p instanceof Schoonmaker).count();
        assertEquals(2, schoonmakers);
    }

    // maakStandaardSchoonmakers: één schoonmaker is noodschoonmaker
    @Test void testMaakStandaardSchoonmakersNoodschoonmaker() {
        Hotel hotel = maakHotel();
        PersonenFactory factory = new PersonenFactory();
        factory.maakStandaardSchoonmakers(hotel, hotel.breedte, hotel.hoogte, 2);
        long nood = hotel.personen.stream()
                .filter(p -> p instanceof Schoonmaker && ((Schoonmaker) p).isNoodSchoonmaker())
                .count();
        assertEquals(1, nood);
    }

    // maakStandaardSchoonmakers: null hotel geeft geen crash
    @Test void testMaakStandaardSchoonmakersNullHotel() {
        assertDoesNotThrow(() -> new PersonenFactory().maakStandaardSchoonmakers(null, 8, 6, 2));
    }

    // maakStandaardSchoonmakers: hotel zonder layout geeft geen crash
    @Test void testMaakStandaardSchoonmakersZonderLayout() {
        Hotel hotel = new Hotel();
        assertDoesNotThrow(() -> new PersonenFactory().maakStandaardSchoonmakers(hotel, 8, 6, 2));
    }
}
