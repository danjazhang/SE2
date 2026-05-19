import Model.layout.Layout;
import Model.ruimte.Kamer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LayoutTest {

    // Ik maak een layout aan; ik verwacht dat geldige vakjes binnen de layout bestaan.
    @Test void testAanmaak() {
        Layout l = new Layout(6, 8);
        assertNotNull(l.krijgVakje(1, 1));
        assertNotNull(l.krijgVakje(6, 8));
    }

    // Ik vraag vakjes buiten het grid op; ik verwacht dat de layout daar null teruggeeft.
    @Test void testBuitenGridNull() {
        Layout l = new Layout(4, 4);
        assertNull(l.krijgVakje(0, 0));
        assertNull(l.krijgVakje(5, 5));
        assertNull(l.krijgVakje(-1, 1));
    }

    // Ik lees de coördinaten van vakjes uit; ik verwacht dat die overeenkomen met hun positie in de layout.
    @Test void testVakjesHebbenJuisteCoordinaten() {
        Layout l = new Layout(3, 3);
        assertEquals(1, l.krijgVakje(1, 1).x);
        assertEquals(3, l.krijgVakje(3, 3).y);
    }

    // Ik plaats een kamer in de layout; ik verwacht dat de juiste vakjes naar die kamer verwijzen.
    @Test void testPlaatsRuimte() {
        Layout l = new Layout(6, 8);
        Kamer k = new Kamer();
        k.posX = 1; k.posY = 1; k.breedte = 2; k.hoogte = 2;
        l.plaatsRuimte(k);
        assertEquals(k, l.krijgVakje(1, 1).ruimte);
        assertEquals(k, l.krijgVakje(2, 2).ruimte);
    }

    // Ik plaats een te grote kamer deels buiten het grid; ik verwacht dat dit geen crash geeft.
    @Test void testPlaatsRuimteBuitenGridCrashetNiet() {
        Layout l = new Layout(3, 3);
        Kamer k = new Kamer();
        k.posX = 3; k.posY = 3; k.breedte = 2; k.hoogte = 2;
        assertDoesNotThrow(() -> l.plaatsRuimte(k));
    }
}
