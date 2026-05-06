import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.Kamer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Vakje: ik test personen, ruimte en coordinaten op een gridvakje.
public class VakjeTest {

    // Ik maak een nieuw Vakje; ik verwacht dat er nog geen personen op staan.
    @Test void testNieuwVakjeLeeg() { assertTrue(new Vakje().krijgPersonen().isEmpty()); }

    // Ik voeg een Gast toe aan een Vakje; ik verwacht dat de personenlijst groter wordt.
    @Test void testVoegPersoonToe() {
        Vakje v = new Vakje();
        Gast g = new Gast(1, 2);
        v.voegPersoonToe(g);
        assertEquals(1, v.krijgPersonen().size());
    }

    // Ik verwijder een Gast uit een Vakje; ik verwacht dat de personenlijst leeg wordt.
    @Test void testVerwijderPersoon() {
        Vakje v = new Vakje();
        Gast g = new Gast(1, 2);
        v.voegPersoonToe(g);
        v.verwijderPersoon(g);
        assertEquals(0, v.krijgPersonen().size());
    }

    // Ik zet een Kamer op een Vakje; ik verwacht dat getRuimte die Kamer teruggeeft.
    @Test void testSetRuimte() {
        Vakje v = new Vakje();
        Kamer k = new Kamer();
        v.setRuimte(k);
        assertEquals(k, v.getRuimte());
    }

    // Ik zet x en y op een Vakje; ik verwacht dat de getters dezelfde waarden geven.
    @Test void testCoordinaten() {
        Vakje v = new Vakje();
        v.x = 3; v.y = 4;
        assertEquals(3, v.getX());
        assertEquals(4, v.getY());
    }

    // Ik voeg twee Gasten toe; ik verwacht dat beide op hetzelfde Vakje kunnen staan.
    @Test void testMeerderePersoonOpVakje() {
        Vakje v = new Vakje();
        v.voegPersoonToe(new Gast(1, 2));
        v.voegPersoonToe(new Gast(2, 3));
        assertEquals(2, v.krijgPersonen().size());
    }
}
