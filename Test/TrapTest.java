import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class TrapTest {

    // tijdperverdieping wordt correct opgeslagen via de constructor
    @Test
    void testConstructor() {
        Trap t = new Trap(5);
        assertEquals(5, t.tijdperverdieping);
    }

    // verschillende waarden voor tijdperverdieping moeten correct opgeslagen worden
    @Test
    void testConstructorVerschillendeWaarden() {
        Trap t1 = new Trap(1);
        Trap t2 = new Trap(10);
        assertEquals(1, t1.tijdperverdieping);
        assertEquals(10, t2.tijdperverdieping);
    }
}
