import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrapTest {

    @Test void testConstructor() {
        Trap t = new Trap(5);
        assertEquals(5, t.tijdperverdieping);
    }

    @Test void testErftVanRuimte() {
        Trap t = new Trap(2);
        assertEquals(0, t.posX);
        assertEquals(0, t.posY);
    }

    @Test void testGebruikTrapCrashetNiet() {
        assertDoesNotThrow(() -> new Trap(2).gebruikTrap(null));
    }
}