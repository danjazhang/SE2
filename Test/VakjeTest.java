import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.Kamer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VakjeTest {

    // Ik maak een nieuw vakje aan; ik verwacht dat er nog geen personen op staan.
    @Test void testNieuwVakjeLeeg() { assertTrue(new Vakje().krijgPersonen().isEmpty()); }

    // Ik voeg een persoon toe aan een vakje; ik verwacht dat het vakje daarna één persoon bevat.
    @Test void testVoegPersoonToe() {
        Vakje v = new Vakje();
        Gast g = new Gast(1, 2);
        v.voegPersoonToe(g);
        assertEquals(1, v.krijgPersonen().size());
    }

    // Ik voeg een persoon toe en verwijder hem weer; ik verwacht dat het vakje daarna leeg is.
    @Test void testVerwijderPersoon() {
        Vakje v = new Vakje();
        Gast g = new Gast(1, 2);
        v.voegPersoonToe(g);
        v.verwijderPersoon(g);
        assertEquals(0, v.krijgPersonen().size());
    }

    // Ik koppel een ruimte aan een vakje; ik verwacht dat het vakje die ruimte teruggeeft.
    @Test void testSetRuimte() {
        Vakje v = new Vakje();
        Kamer k = new Kamer();
        v.setRuimte(k);
        assertEquals(k, v.getRuimte());
    }

    // Ik stel handmatig coördinaten op een vakje in; ik verwacht dat de getters die waarden teruggeven.
    @Test void testCoordinaten() {
        Vakje v = new Vakje();
        v.x = 3; v.y = 4;
        assertEquals(3, v.getX());
        assertEquals(4, v.getY());
    }

    // Ik zet twee personen op hetzelfde vakje; ik verwacht dat beide in de lijst staan.
    @Test void testMeerderePersoonOpVakje() {
        Vakje v = new Vakje();
        v.voegPersoonToe(new Gast(1, 2));
        v.voegPersoonToe(new Gast(2, 3));
        assertEquals(2, v.krijgPersonen().size());
    }
}
