import Model.persoon.Gast;
import Model.ruimte.Fitnessruimte;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FitnessruimteTest {

    // gastenlijst is leeg na aanmaken
    @Test
    void testConstructor() {
        Fitnessruimte f = new Fitnessruimte();
        assertNotNull(f.gasten);
        assertTrue(f.gasten.isEmpty());
    }

    // fitnesruimte erft van Ruimte, posX en posY beginnen op 0
    @Test
    void testErftVanRuimte() {
        Fitnessruimte f = new Fitnessruimte();
        assertEquals(0, f.posX);
        assertEquals(0, f.posY);
    }

    // een gast kan aan de gastenlijst toegevoegd worden
    @Test
    void testVoegGastToe() {
        Fitnessruimte f = new Fitnessruimte();
        Gast g = new Gast(2);
        f.gasten.add(g);
        assertEquals(1, f.gasten.size());
        assertEquals(g, f.gasten.get(0));
    }
}
