import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersoonTest {

    // huidigVakje en doelVakje beginnen op null na aanmaken
    @Test
    void testConstructor() {
        Persoon p = new Persoon();
        assertNull(p.huidigVakje);
        assertNull(p.doelVakje);
    }

    // zetDoel koppelt een vakje aan doelVakje
    @Test
    void testZetDoel() {
        Persoon p = new Persoon();
        Vakje v = new Vakje();
        p.zetDoel(v);
        assertEquals(v, p.doelVakje);
    }
}
