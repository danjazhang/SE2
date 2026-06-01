import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import Model.ruimte.Ruimte;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HotelTest {

    // -------------------------------------------------
    // Constructor tests
    // -------------------------------------------------

    // ik maak een nieuw Hotel object aan; ik verwacht dat de lijsten ruimtes en personen leeg zijn
    @Test
    void testConstructor() {

        Hotel h = new Hotel();

        assertTrue(h.ruimtes.isEmpty());

        assertTrue(h.personen.isEmpty());
    }

    // -------------------------------------------------
    // Persoon toevoegen
    // -------------------------------------------------

    // ik voeg één gast toe aan het hotel; ik verwacht dat de personenlijst 1 persoon bevat en dat de gast erin zit
    @Test
    void testVoegPersoonToe() {

        Hotel h = new Hotel();

        Gast g = new Gast(1, 2);

        h.voegPersoonToe(g);

        assertEquals(1, h.personen.size());

        assertTrue(h.personen.contains(g));
    }

    // ik voeg meerdere gasten toe aan het hotel; ik verwacht dat de personenlijst groeit naar 2
    @Test
    void testVoegMeerderePersonenToe() {

        Hotel h = new Hotel();

        Gast g1 = new Gast(1, 2);

        Gast g2 = new Gast(2, 3);

        h.voegPersoonToe(g1);

        h.voegPersoonToe(g2);

        assertEquals(2, h.personen.size());
    }

    // -------------------------------------------------
    // Ruimte ophalen (krijgRuimteOp)
    // -------------------------------------------------

    // ik plaats een kamer op een specifieke positie in de layout; ik verwacht dat die kamer wordt teruggegeven bij dezelfde coördinaten
    @Test
    void testKrijgRuimteOp() {

        Hotel h = new Hotel();

        h.layout = new Layout(5, 5);

        Kamer k = new Kamer();

        k.posX = 2;

        k.posY = 2;

        k.breedte = 1;

        k.hoogte = 1;

        h.ruimtes.add(k);

        h.layout.plaatsRuimte(k);

        assertEquals(k, h.krijgRuimteOp(2, 2));
    }

    // ik vraag een vakje op waar geen ruimte ligt; ik verwacht null als resultaat
    @Test
    void testKrijgRuimteOpLeegVakje() {

        Hotel h = new Hotel();

        h.layout = new Layout(5, 5);

        assertNull(h.krijgRuimteOp(3, 3));
    }



    // extra uitleg:
    // deze test laat zien dat alleen exact geplaatste ruimtes worden gevonden,
    // andere posities geven null terug

    // ik plaats een kamer op positie (1,1) maar vraag (2,2) op; ik verwacht null
    @Test
    void testKrijgRuimteOpVerkeerdePositie() {

        Hotel h = new Hotel();

        h.layout = new Layout(5, 5);

        Kamer k = new Kamer();

        k.posX = 1;

        k.posY = 1;

        h.ruimtes.add(k);

        h.layout.plaatsRuimte(k);

        assertNull(h.krijgRuimteOp(2, 2));
    }

    // -------------------------------------------------
    // Pathfinder tests
    // -------------------------------------------------

    // ik initialiseer een pathfinder met een geldig hotel; ik verwacht dat dit zonder errors lukt en niet null is
    @Test
    void testPathfinderInstellen() {

        Hotel h = new Hotel();

        h.layout = new Layout(5, 3);

        h.breedte = 5;

        h.hoogte = 3;

        Lift lift = new Lift(h);

        lift.posX = 1;

        lift.posY = 1;

        lift.breedte = 1;

        lift.hoogte = 3;

        h.ruimtes.add(lift);

        h.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);

        trap.posX = 5;

        trap.posY = 1;

        trap.breedte = 1;

        trap.hoogte = 3;

        h.ruimtes.add(trap);

        h.layout.plaatsRuimte(trap);

        assertDoesNotThrow(() -> {

            h.pathfinder = new Pathfinder(h);
        });

        assertNotNull(h.pathfinder);
    }

    // ik heb nog geen pathfinder gezet in het hotel; ik verwacht dat de waarde null is
    @Test
    void testPathfinderNull() {

        Hotel h = new Hotel();

        assertNull(h.pathfinder);
    }

    // -------------------------------------------------
    // Ruimtes lijst branches
    // -------------------------------------------------

    // ik maak een hotel zonder ruimtes toe te voegen; ik verwacht dat de ruimteslijst leeg is
    @Test
    void testLegeRuimtes() {

        Hotel h = new Hotel();

        h.layout = new Layout(5, 5);

        assertTrue(h.ruimtes.isEmpty());
    }

    // -------------------------------------------------
    // Personen null/edge cases
    // -------------------------------------------------

    // ik voeg een null persoon toe aan het hotel; ik verwacht dat de lijst dit accepteert en null bevat
    @Test
    void testVoegNullPersoonToe() {

        Hotel h = new Hotel();

        h.voegPersoonToe(null);

        assertTrue(h.personen.contains(null));
    }

    // ik voeg een geldige gast en een null waarde toe; ik verwacht dat de lijst beide entries bevat
    @Test
    void testGemengdePersonen() {

        Hotel h = new Hotel();

        Gast g = new Gast(1, 2);

        h.voegPersoonToe(g);

        h.voegPersoonToe(null);

        assertEquals(2, h.personen.size());
    }

    // -------------------------------------------------
    // Layout null branches
    // -------------------------------------------------


}