import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class TrapTest {

    @Test
    void testConstructor() {
        Trap t = new Trap(5);
        assertEquals(5, t.tijdperverdieping);
    }

    @Test
    void testConstructorVerschillendeWaarden() {
        Trap t1 = new Trap(1);
        Trap t2 = new Trap(10);
        assertEquals(1, t1.tijdperverdieping);
        assertEquals(10, t2.tijdperverdieping);
    }

    @Test
    void testGebruikTrapCrashetNiet() {
        Trap t = new Trap(3);
        Persoon p = new Persoon();
        assertDoesNotThrow(() -> t.gebruikTrap(p));
    }
}
