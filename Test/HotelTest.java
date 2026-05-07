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

    // constructor: ruimtes en personen zijn lege lijsten
    @Test void testConstructor() {
        Hotel h = new Hotel();
        assertTrue(h.ruimtes.isEmpty());
        assertTrue(h.personen.isEmpty());
    }

    // voegPersoonToe: persoon wordt toegevoegd aan lijst
    @Test void testVoegPersoonToe() {
        Hotel h = new Hotel();
        Gast g = new Gast(1, 2);
        h.voegPersoonToe(g);
        assertEquals(1, h.personen.size());
        assertTrue(h.personen.contains(g));
    }

    // krijgRuimteOp: geeft ruimte terug op juiste positie
    @Test void testKrijgRuimteOp() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        Kamer k = new Kamer();
        k.posX = 2; k.posY = 2; k.breedte = 1; k.hoogte = 1;
        h.ruimtes.add(k);
        h.layout.plaatsRuimte(k);
        assertEquals(k, h.krijgRuimteOp(2, 2));
    }

    // krijgRuimteOp: geeft null terug als er geen ruimte is
    @Test void testKrijgRuimteOpLeegVakje() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        assertNull(h.krijgRuimteOp(3, 3));
    }

    // pathfinder: kan worden aangemaakt en ingesteld
    @Test void testPathfinderInstellen() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 3);
        h.breedte = 5;
        h.hoogte = 3;
        Lift lift = new Lift();
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
