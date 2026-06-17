import Model.layout.Layout;
import Model.layout.Vakje;
import Model.ruimte.Kamer;
import Model.ruimte.Ruimte;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Layout: grid aanmaken, plaatsRuimte, krijgVakje, id/naam, randgevallen
public class LayoutTest {

    // constructor: grid correct aangemaakt
    @Test void testConstructorGrid() {
        Layout l = new Layout(5, 4);
        assertEquals(5, l.breedte);
        assertEquals(4, l.hoogte);
        assertNotNull(l.vakjes);
    }

    // constructor: alle vakjes zijn aangemaakt
    @Test void testConstructorAlleVakjesAangemaakt() {
        Layout l = new Layout(3, 3);
        for (int x = 1; x <= 3; x++)
            for (int y = 1; y <= 3; y++)
                assertNotNull(l.krijgVakje(x, y));
    }

    // constructor: vakjes hebben de juiste x/y coördinaten
    @Test void testConstructorVakjeCoordinaten() {
        Layout l = new Layout(4, 4);
        Vakje v = l.krijgVakje(3, 2);
        assertEquals(3, v.x);
        assertEquals(2, v.y);
    }

    // alle vakjes in een grid hebben correcte coördinaten
    @Test void testAlleCoordinaten() {
        Layout l = new Layout(4, 3);
        for (int x = 1; x <= 4; x++)
            for (int y = 1; y <= 3; y++) {
                Vakje v = l.krijgVakje(x, y);
                assertEquals(x, v.x);
                assertEquals(y, v.y);
            }
    }

    // id en naam kunnen worden ingesteld
    @Test void testIdEnNaam() {
        Layout l = new Layout(3, 3);
        l.id = 5;
        l.naam = "testlayout";
        assertEquals(5, l.id);
        assertEquals("testlayout", l.naam);
    }

    // krijgVakje: geeft null buiten grid (x=0)
    @Test void testKrijgVakjeBuitenGridX0() {
        assertNull(new Layout(5, 5).krijgVakje(0, 1));
    }

    // krijgVakje: geeft null buiten grid (y=0)
    @Test void testKrijgVakjeBuitenGridY0() {
        assertNull(new Layout(5, 5).krijgVakje(1, 0));
    }

    // krijgVakje: geeft null als x of y te groot is
    @Test void testKrijgVakjeBuitenGridTeGroot() {
        Layout l = new Layout(5, 5);
        assertNull(l.krijgVakje(6, 1));
        assertNull(l.krijgVakje(1, 6));
    }

    // krijgVakje: alle hoekpunten van het grid zijn bereikbaar
    @Test void testKrijgVakjeGrenzen() {
        Layout l = new Layout(5, 5);
        assertNotNull(l.krijgVakje(1, 1));
        assertNotNull(l.krijgVakje(5, 5));
        assertNotNull(l.krijgVakje(1, 5));
        assertNotNull(l.krijgVakje(5, 1));
    }

    // groot grid: vakje op max positie bereikbaar
    @Test void testGrootGrid() {
        Layout l = new Layout(100, 100);
        assertNotNull(l.krijgVakje(100, 100));
        assertNull(l.krijgVakje(101, 100));
    }

    // 1x1 grid: enige vakje bereikbaar, buiten geeft null
    @Test void testKleinGrid() {
        Layout l = new Layout(1, 1);
        assertNotNull(l.krijgVakje(1, 1));
        assertNull(l.krijgVakje(0, 0));
        assertNull(l.krijgVakje(2, 1));
    }

    // plaatsRuimte: alle vakjes in het bereik worden gekoppeld
    @Test void testPlaatsRuimteKoppeltVakjes() {
        Layout l = new Layout(5, 5);
        Ruimte r = new Ruimte(2, 2, 2, 2);
        l.plaatsRuimte(r);
        assertEquals(r, l.krijgVakje(2, 2).ruimte);
        assertEquals(r, l.krijgVakje(3, 2).ruimte);
        assertEquals(r, l.krijgVakje(2, 3).ruimte);
        assertEquals(r, l.krijgVakje(3, 3).ruimte);
    }

    // plaatsRuimte: vakjes buiten de ruimte worden niet gekoppeld
    @Test void testPlaatsRuimteKoppeltNietBuiten() {
        Layout l = new Layout(5, 5);
        Ruimte r = new Ruimte(2, 2, 1, 1);
        l.plaatsRuimte(r);
        assertNull(l.krijgVakje(3, 2).ruimte);
        assertNull(l.krijgVakje(2, 3).ruimte);
    }

    // plaatsRuimte: ruimte groter dan grid crasht niet
    @Test void testPlaatsRuimteBijGrens() {
        Layout l = new Layout(4, 4);
        assertDoesNotThrow(() -> l.plaatsRuimte(new Ruimte(3, 3, 3, 3)));
    }

    // plaatsRuimte: kamer wordt correct geplaatst
    @Test void testPlaatsKamer() {
        Layout l = new Layout(6, 4);
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 2; kamer.breedte = 2; kamer.hoogte = 1;
        l.plaatsRuimte(kamer);
        assertEquals(kamer, l.krijgVakje(3, 2).ruimte);
        assertEquals(kamer, l.krijgVakje(4, 2).ruimte);
    }

    // twee ruimtes naast elkaar worden correct geplaatst
    @Test void testTweeRuimtesNaastElkaar() {
        Layout l = new Layout(6, 3);
        Ruimte r1 = new Ruimte(1, 1, 2, 1);
        Ruimte r2 = new Ruimte(3, 1, 2, 1);
        l.plaatsRuimte(r1);
        l.plaatsRuimte(r2);
        assertEquals(r1, l.krijgVakje(1, 1).ruimte);
        assertEquals(r1, l.krijgVakje(2, 1).ruimte);
        assertEquals(r2, l.krijgVakje(3, 1).ruimte);
        assertEquals(r2, l.krijgVakje(4, 1).ruimte);
    }

    // plaatsRuimte: tweede plaatsing op zelfde positie overschrijft eerste
    @Test void testPlaatsRuimteOverschrijft() {
        Layout l = new Layout(4, 4);
        Ruimte r1 = new Ruimte(2, 2, 1, 1);
        Ruimte r2 = new Ruimte(2, 2, 1, 1);
        l.plaatsRuimte(r1);
        l.plaatsRuimte(r2);
        assertEquals(r2, l.krijgVakje(2, 2).ruimte);
    }
}
