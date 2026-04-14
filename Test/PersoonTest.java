import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class PersoonTest {

    @Test
    void testConstructor() {
        Persoon p = new Persoon();
        assertNull(p.huidigVakje);
        assertNull(p.doelVakje);
    }

    @Test
    void testZetDoel() {
        Persoon p = new Persoon();
        Vakje v = new Vakje();
        p.zetDoel(v);
        assertEquals(v, p.doelVakje);
    }

    @Test
    void testBeweegZonderLayoutCrashetNiet() {
        Persoon p = new Persoon();
        assertDoesNotThrow(() -> p.beweeg());
    }

    @Test
    void testBeweegZonderDoelCrashetNiet() {
        Persoon p = new Persoon();
        Layout layout = new Layout(5, 5);
        p.layout = layout;
        Vakje start = layout.krijgVakje(1, 1);
        p.zetStartPositie(start);
        assertDoesNotThrow(() -> p.beweeg());
    }

    @Test
    void testBeweegNaarDoel() {
        Layout layout = new Layout(5, 5);
        Persoon p = new Persoon();
        p.layout = layout;
        Vakje start = layout.krijgVakje(1, 1);
        Vakje doel = layout.krijgVakje(3, 1);
        p.zetStartPositie(start);
        p.zetDoel(doel);
        p.beweeg();
        // na 1 stap moet persoon op (2,1) staan
        assertEquals(2, p.huidigVakje.x);
        assertEquals(1, p.huidigVakje.y);
    }

    @Test
    void testBeweegAlOpDoel() {
        Layout layout = new Layout(5, 5);
        Persoon p = new Persoon();
        p.layout = layout;
        Vakje start = layout.krijgVakje(2, 2);
        p.zetStartPositie(start);
        p.zetDoel(start);
        p.beweeg();
        // persoon staat al op doel, mag niet bewegen
        assertEquals(start, p.huidigVakje);
    }

    @Test
    void testZetStartPositie() {
        Layout layout = new Layout(5, 5);
        Persoon p = new Persoon();
        Vakje v = layout.krijgVakje(2, 3);
        p.zetStartPositie(v);
        assertEquals(v, p.huidigVakje);
        assertTrue(v.krijgPersonen().contains(p));
    }

    @Test
    void testVoerTaakUitCrashetNiet() {
        Persoon p = new Persoon();
        assertDoesNotThrow(() -> p.voerTaakUit());
    }

    @Test
    void testHuidigVakjeInstelbaar() {
        Persoon p = new Persoon();
        Vakje v = new Vakje();
        p.huidigVakje = v;
        assertEquals(v, p.huidigVakje);
    }
}
