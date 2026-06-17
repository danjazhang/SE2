import Model.layout.Layout;
import Model.layout.Vakje;
import Model.ruimte.Kamer;
import Model.ruimte.Ruimte;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Layout: grid aanmaken, ruimtes plaatsen, vakjes opvragen
public class LayoutTest2 {

    // nieuw grid heeft de juiste breedte en hoogte
    @Test void testAfmetingen() {
        Layout l = new Layout(5, 3);
        assertEquals(5, l.breedte);
        assertEquals(3, l.hoogte);
    }

    // elk vakje in het grid heeft de juiste x,y-coördinaat (1-geïndexeerd)
    @Test void testVakjeCoordinaten() {
        Layout l = new Layout(3, 3);
        assertEquals(1, l.krijgVakje(1, 1).x);
        assertEquals(1, l.krijgVakje(1, 1).y);
        assertEquals(3, l.krijgVakje(3, 3).x);
        assertEquals(3, l.krijgVakje(3, 3).y);
    }

    // vakje buiten het grid geeft null terug
    @Test void testBuitenGridGeeftNull() {
        Layout l = new Layout(3, 3);
        assertNull(l.krijgVakje(0, 1));
        assertNull(l.krijgVakje(4, 1));
        assertNull(l.krijgVakje(1, 0));
        assertNull(l.krijgVakje(1, 4));
    }

    // vakje op de rand van het grid is niet null
    @Test void testRandVakjesNietNull() {
        Layout l = new Layout(4, 4);
        assertNotNull(l.krijgVakje(1, 1));
        assertNotNull(l.krijgVakje(4, 4));
        assertNotNull(l.krijgVakje(1, 4));
        assertNotNull(l.krijgVakje(4, 1));
    }

    // een geplaatste ruimte is terug te vinden op alle vakjes die hij beslaat
    @Test void testPlaatsRuimteVultVakjes() {
        Layout l = new Layout(5, 5);
        Kamer k = new Kamer();
        k.posX = 2; k.posY = 2; k.breedte = 2; k.hoogte = 2;
        l.plaatsRuimte(k);
        assertSame(k, l.krijgVakje(2, 2).ruimte);
        assertSame(k, l.krijgVakje(3, 2).ruimte);
        assertSame(k, l.krijgVakje(2, 3).ruimte);
        assertSame(k, l.krijgVakje(3, 3).ruimte);
    }

    // vakjes buiten de ruimte zijn niet gevuld
    @Test void testPlaatsRuimteVultNietBuiten() {
        Layout l = new Layout(5, 5);
        Kamer k = new Kamer();
        k.posX = 2; k.posY = 2; k.breedte = 1; k.hoogte = 1;
        l.plaatsRuimte(k);
        assertNull(l.krijgVakje(1, 2).ruimte);
        assertNull(l.krijgVakje(3, 2).ruimte);
    }

    // een 1x1 ruimte vult precies één vakje
    @Test void testPlaatsRuimteEenVakje() {
        Layout l = new Layout(3, 3);
        Ruimte r = new Ruimte();
        r.posX = 2; r.posY = 2; r.breedte = 1; r.hoogte = 1;
        l.plaatsRuimte(r);
        assertSame(r, l.krijgVakje(2, 2).ruimte);
        assertNull(l.krijgVakje(1, 2).ruimte);
    }

    // twee ruimtes naast elkaar vullen elk hun eigen vakjes
    @Test void testTweeRuimtesNaastElkaar() {
        Layout l = new Layout(5, 3);
        Kamer k1 = new Kamer(); k1.posX = 1; k1.posY = 1; k1.breedte = 1; k1.hoogte = 1;
        Kamer k2 = new Kamer(); k2.posX = 2; k2.posY = 1; k2.breedte = 1; k2.hoogte = 1;
        l.plaatsRuimte(k1);
        l.plaatsRuimte(k2);
        assertSame(k1, l.krijgVakje(1, 1).ruimte);
        assertSame(k2, l.krijgVakje(2, 1).ruimte);
    }

    // een leeg grid heeft overal null als ruimte
    @Test void testLeegGridHeeftNullRuimtes() {
        Layout l = new Layout(3, 3);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                assertNull(l.krijgVakje(x, y).ruimte);
            }
        }
    }

    // id en naam zijn standaard 0 en null
    @Test void testIdEnNaamStandaard() {
        Layout l = new Layout(2, 2);
        assertEquals(0, l.id);
        assertNull(l.naam);
    }

    // id en naam kunnen worden ingesteld
    @Test void testIdEnNaamInstelbaar() {
        Layout l = new Layout(2, 2);
        l.id = 5;
        l.naam = "test";
        assertEquals(5, l.id);
        assertEquals("test", l.naam);
    }
}
