import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class FitnesruimteTest {

    // gastenlijst is leeg na aanmaken
    @Test
    void testConstructor() {
        Fitnesruimte f = new Fitnesruimte();
        assertNotNull(f.gasten);
        assertTrue(f.gasten.isEmpty());
    }

    // fitnesruimte erft van Ruimte, posX en posY beginnen op 0
    @Test
    void testErftVanRuimte() {
        Fitnesruimte f = new Fitnesruimte();
        assertEquals(0, f.posX);
        assertEquals(0, f.posY);
    }

    // een gast kan aan de gastenlijst toegevoegd worden
    @Test
    void testVoegGastToe() {
        Fitnesruimte f = new Fitnesruimte();
        Gast g = new Gast(2);
        f.gasten.add(g);
        assertEquals(1, f.gasten.size());
        assertEquals(g, f.gasten.get(0));
    }
}
