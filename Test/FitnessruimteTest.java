import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class FitnessruimteTest {

    @Test
    void testConstructor() {
        Fitnessruimte f = new Fitnessruimte();
        assertNotNull(f.gasten);
        assertTrue(f.gasten.isEmpty());
    }

    @Test
    void testErftVanRuimte() {
        Fitnessruimte f = new Fitnessruimte();
        assertEquals(0, f.posX);
        assertEquals(0, f.posY);
    }

    @Test
    void testVoegGastToe() {
        Fitnessruimte f = new Fitnessruimte();
        Gast g = new Gast(1, 2);
        f.gasten.add(g);
        assertEquals(1, f.gasten.size());
        assertEquals(g, f.gasten.get(0));
    }

    @Test
    void testBreedteFitnessCrashetNiet() {
        Fitnessruimte f = new Fitnessruimte();
        assertDoesNotThrow(() -> f.breedteFitness());
    }

    @Test
    void testVerlaatFitnessCrashetNiet() {
        Fitnessruimte f = new Fitnessruimte();
        assertDoesNotThrow(() -> f.verlaatFitness());
    }
}
