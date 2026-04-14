import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

class LayoutTest {

    @Test
    void testLayoutAanmaak() {
        Layout layout = new Layout(6, 8);
        assertNotNull(layout.krijgVakje(1, 1));
        assertNull(layout.krijgVakje(0, 0));
        assertNull(layout.krijgVakje(7, 9));
    }

    @Test
    void testPlaatsRuimte() {
        Layout layout = new Layout(6, 8);
        Kamer kamer = new Kamer();
        kamer.posX = 1; kamer.posY = 1;
        kamer.breedte = 2; kamer.hoogte = 2;
        layout.plaatsRuimte(kamer);
        assertEquals(kamer, layout.krijgVakje(1, 1).ruimte);
        assertEquals(kamer, layout.krijgVakje(2, 2).ruimte);
    }

    @Test
    void testKrijgVakjeRandwaarden() {
        Layout layout = new Layout(6, 8);
        assertNotNull(layout.krijgVakje(6, 8));
        assertNull(layout.krijgVakje(7, 8));
        assertNull(layout.krijgVakje(6, 9));
    }

    @Test
    void testVakjesHebbenJuisteCoordinaten() {
        Layout layout = new Layout(3, 3);
        assertEquals(1, layout.krijgVakje(1, 1).x);
        assertEquals(1, layout.krijgVakje(1, 1).y);
        assertEquals(3, layout.krijgVakje(3, 3).x);
        assertEquals(3, layout.krijgVakje(3, 3).y);
    }

    @Test
    void testPlaatsRuimteBuitenGridCrashetNiet() {
        Layout layout = new Layout(3, 3);
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 3;
        kamer.breedte = 2; kamer.hoogte = 2;
        // ruimte valt deels buiten grid, mag niet crashen
        assertDoesNotThrow(() -> layout.plaatsRuimte(kamer));
    }
}
