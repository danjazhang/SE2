import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotelTest {

    // Ik maak een nieuw hotel aan; ik verwacht dat de lijsten voor ruimtes en personen leeg starten.
    @Test void testConstructor() {
        Hotel h = new Hotel();
        assertTrue(h.ruimtes.isEmpty());
        assertTrue(h.personen.isEmpty());
    }

    // Ik voeg een persoon toe aan het hotel; ik verwacht dat die persoon in de personenlijst terechtkomt.
    @Test void testVoegPersoonToe() {
        Hotel h = new Hotel();
        Gast g = new Gast(1, 2);
        h.voegPersoonToe(g);
        assertEquals(1, h.personen.size());
        assertTrue(h.personen.contains(g));
    }

    // Ik plaats een kamer op een bekende positie; ik verwacht dat het hotel die ruimte op die positie teruggeeft.
    @Test void testKrijgRuimteOp() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        Kamer k = new Kamer();
        k.posX = 2; k.posY = 2; k.breedte = 1; k.hoogte = 1;
        h.ruimtes.add(k);
        h.layout.plaatsRuimte(k);
        assertEquals(k, h.krijgRuimteOp(2, 2));
    }

    // Ik vraag een leeg vakje op; ik verwacht dat het hotel daar geen ruimte teruggeeft.
    @Test void testKrijgRuimteOpLeegVakje() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        assertNull(h.krijgRuimteOp(3, 3));
    }

    // Ik maak een hotel met lift en trap en stel daarna een pathfinder in;
    // ik verwacht dat dat lukt en dat de pathfinder niet null is.
    @Test void testPathfinderInstellen() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 3);
        h.breedte = 5;
        h.hoogte = 3;
        Lift lift = new Lift(h);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 3;
        h.ruimtes.add(lift);
        h.layout.plaatsRuimte(lift);
        Trap trap = new Trap(2);
        trap.posX = 5; trap.posY = 1; trap.breedte = 1; trap.hoogte = 3;
        h.ruimtes.add(trap);
        h.layout.plaatsRuimte(trap);
        assertDoesNotThrow(() -> h.pathfinder = new Pathfinder(h));
        assertNotNull(h.pathfinder);
    }
}