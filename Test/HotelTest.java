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

    // Hotel start met lege lijsten
    @Test
    void testConstructor() {

        Hotel h = new Hotel();

        assertTrue(h.ruimtes.isEmpty());

        assertTrue(h.personen.isEmpty());
    }

    // -------------------------------------------------
    // Persoon toevoegen
    // -------------------------------------------------

    // 1 persoon toevoegen
    @Test
    void testVoegPersoonToe() {

        Hotel h = new Hotel();

        Gast g = new Gast(1, 2);

        h.voegPersoonToe(g);

        assertEquals(1, h.personen.size());

        assertTrue(h.personen.contains(g));
    }

    // meerdere personen toevoegen (branch uitbreiding)
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

    // kamer op correcte positie
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

    // lege plek -> null return branch
    @Test
    void testKrijgRuimteOpLeegVakje() {

        Hotel h = new Hotel();

        h.layout = new Layout(5, 5);

        assertNull(h.krijgRuimteOp(3, 3));
    }

    // layout is null -> extra branch coverage (null check in methode)
    @Test
    void testKrijgRuimteOpZonderLayout() {

        Hotel h = new Hotel();

        // layout is null

        assertThrows(NullPointerException.class, () -> {

            h.krijgRuimteOp(1, 1);
        });
    }

    // meerdere ruimtes maar andere positie
    @Test
    void testKrijgRuimteOpVerkeerdePositie() {

        Hotel h = new Hotel();

        h.layout = new Layout(5, 5);

        Kamer k = new Kamer();

        k.posX = 1;

        k.posY = 1;

        h.ruimtes.add(k);

        h.layout.plaatsRuimte(k);

        // andere positie -> null branch
        assertNull(h.krijgRuimteOp(2, 2));
    }

    // -------------------------------------------------
    // Pathfinder tests
    // -------------------------------------------------

    // pathfinder wordt correct gezet
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

    // pathfinder null branch scenario
    @Test
    void testPathfinderNull() {

        Hotel h = new Hotel();

        // nog geen layout of ruimtes

        assertNull(h.pathfinder);
    }

    // -------------------------------------------------
    // Ruimtes lijst branches
    // -------------------------------------------------

    // lege ruimtes lijst branch
    @Test
    void testLegeRuimtes() {

        Hotel h = new Hotel();

        h.layout = new Layout(5, 5);

        assertTrue(h.ruimtes.isEmpty());
    }

    // -------------------------------------------------
    // Personen null/edge cases
    // -------------------------------------------------

    // null persoon toevoegen (branch in add)
    @Test
    void testVoegNullPersoonToe() {

        Hotel h = new Hotel();

        h.voegPersoonToe(null);

        // lijst accepteert null (Java List gedrag)
        assertTrue(h.personen.contains(null));
    }

    // meerdere null + echte personen
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

    // layout niet gezet -> crash branch
    @Test
    void testLayoutNullBranch() {

        Hotel h = new Hotel();

        Kamer k = new Kamer();

        k.posX = 1;

        k.posY = 1;

        h.ruimtes.add(k);

        assertThrows(NullPointerException.class, () -> {

            h.krijgRuimteOp(1, 1);
        });
    }
}