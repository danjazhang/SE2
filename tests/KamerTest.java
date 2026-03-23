import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KamerTest {

    // een nieuwe kamer is schoon en heeft geen gast
    @Test
    void testConstructor() {
        Kamer k = new Kamer();
        assertTrue(k.schoon);
        assertNull(k.Gast);
    }

    // sterren beginnen op 0 want ze worden niet in de constructor gezet
    @Test
    void testSterrenStandaard() {
        Kamer k = new Kamer();
        assertEquals(0, k.sterren);
    }

    // kamer erft van Ruimte, posX en posY beginnen op 0
    @Test
    void testErftVanRuimte() {
        Kamer k = new Kamer();
        assertEquals(0, k.posX);
        assertEquals(0, k.posY);
    }

    // sterren kunnen handmatig gezet worden
    @Test
    void testZetSterren() {
        Kamer k = new Kamer();
        k.sterren = 4;
        assertEquals(4, k.sterren);
    }

    // schoon kan op false gezet worden
    @Test
    void testZetSchoonOpFalse() {
        Kamer k = new Kamer();
        k.schoon = false;
        assertFalse(k.schoon);
    }
}
