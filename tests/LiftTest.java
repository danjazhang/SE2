import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LiftTest {

    // lift begint op verdieping 0 met een lege passagierslijst
    @Test
    void testConstructor() {
        Lift l = new Lift();
        assertEquals(0, l.huidigeverdieping);
        assertNotNull(l.passagiers);
        assertTrue(l.passagiers.isEmpty());
    }

    // een passagier kan aan de lijst toegevoegd worden
    @Test
    void testVoegPassagierToe() {
        Lift l = new Lift();
        Persoon p = new Persoon();
        l.passagiers.add(p);
        assertEquals(1, l.passagiers.size());
        assertEquals(p, l.passagiers.get(0));
    }

    // verdieping kan handmatig gezet worden
    @Test
    void testZetVerdieping() {
        Lift l = new Lift();
        l.huidigeverdieping = 3;
        assertEquals(3, l.huidigeverdieping);
    }
}
