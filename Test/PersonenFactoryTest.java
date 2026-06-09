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

    // Ik maak met deze hulpmethode een klein hotel met route-ondersteuning,
    // zodat de factory gasten en schoonmakers meteen een pathfinder kan meegeven.
    private Hotel maakHotel() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 3);
        hotel.breedte = 5;
        hotel.hoogte = 3;
        Lift lift = new Lift(hotel);
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

    // Ik laat de factory een gast maken; ik verwacht dat id, gewenste sterren en startpositie correct gezet worden.
    @Test void testMaakGast() {
        Hotel hotel = maakHotel();
        PersonenFactory f = new PersonenFactory();
        Vakje v = hotel.layout.krijgVakje(2, 1);
        Gast g = f.maakGast(1, 3, hotel.pathfinder, v);
        assertEquals(1, g.gastId);
        assertEquals(3, g.gewensteSterren);
        assertEquals(v, g.huidigVakje);
    }

    // Ik maak een gast zonder startvakje; ik verwacht dat hij nog geen huidig vakje heeft.
    @Test void testMaakGastZonderStartVakje() {
        Hotel hotel = maakHotel();
        PersonenFactory f = new PersonenFactory();
        Gast g = f.maakGast(1, 2, hotel.pathfinder, null);
        assertNull(g.huidigVakje);
    }

    // Ik maak een gast met een null pathfinder; ik verwacht dat dit geen crash geeft.
    @Test void testMaakGastMetNullPathfinder() {
        PersonenFactory f = new PersonenFactory();
        assertDoesNotThrow(() -> f.maakGast(1, 2, null, null));
    }

    // Ik laat de factory een schoonmaker maken; ik verwacht dat de startpositie correct wordt gezet.
    @Test void testMaakSchoonmaker() {
        Hotel hotel = maakHotel();
        PersonenFactory f = new PersonenFactory();
        Vakje v = hotel.layout.krijgVakje(2, 1);
        Schoonmaker s = f.maakSchoonmaker(hotel.pathfinder, v);
        assertEquals(v, s.huidigVakje);
    }

    // Ik maak een schoonmaker zonder startvakje; ik verwacht dat hij nog geen huidig vakje heeft.
    @Test void testMaakSchoonmakerZonderStartVakje() {
        Hotel hotel = maakHotel();
        PersonenFactory f = new PersonenFactory();
        Schoonmaker s = f.maakSchoonmaker(hotel.pathfinder, null);
        assertNull(s.huidigVakje);
    }

    // Ik maak een schoonmaker met een null pathfinder; ik verwacht dat dit geen crash geeft.
    @Test void testMaakSchoonmakerMetNullPathfinder() {
        PersonenFactory f = new PersonenFactory();
        assertDoesNotThrow(() -> f.maakSchoonmaker(null, null));
    }
}
