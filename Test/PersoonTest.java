import Model.persoon.Persoon;
import Model.layout.Vakje;
import Model.layout.Layout;
import Model.ruimte.Ruimte;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersoonTest {

    @Test void testConstructor() {
        Persoon p = new Persoon();
        assertNull(p.huidigVakje);
        assertNull(p.doelVakje);
    }

    @Test void testZetDoel() {
        Persoon p = new Persoon();
        Vakje v = new Vakje();
        p.zetDoel(v);
        assertEquals(v, p.doelVakje);
    }

    @Test void testZetStartPositie() {
        Persoon p = new Persoon();
        Vakje v = new Vakje();
        p.zetStartPositie(v);
        assertEquals(v, p.huidigVakje);
        assertTrue(v.krijgPersonen().contains(p));
    }

    @Test void testBeweegZonderDoelCrashetNiet() {
        assertDoesNotThrow(() -> new Persoon().beweeg());
    }

    @Test void testBeweegZonderLayoutCrashetNiet() {
        Persoon p = new Persoon();
        Vakje v = new Vakje();
        p.zetStartPositie(v);
        p.zetDoel(new Vakje());
        assertDoesNotThrow(() -> p.beweeg());
    }

    @Test void testBeweegNaarDoel() {
        Layout layout = new Layout(3, 3);
        Persoon p = new Persoon();
        p.layout = layout;
        p.zetStartPositie(layout.krijgVakje(1, 1));
        p.zetDoel(layout.krijgVakje(3, 1));
        p.beweeg();
        assertEquals(2, p.huidigVakje.x);
    }

    @Test void testVoegTussendoelToe() {
        Persoon p = new Persoon();
        Vakje v1 = new Vakje();
        Vakje v2 = new Vakje();
        p.zetDoel(v1);
        p.voegTussendoelToe(v2);
        assertEquals(v1, p.doelVakje);
    }

    @Test void testTussendoelWordtDoelNaAankomen() {
        Layout layout = new Layout(3, 3);
        Persoon p = new Persoon();
        p.layout = layout;
        Vakje start = layout.krijgVakje(1, 1);
        Vakje tussendoel = layout.krijgVakje(2, 1);
        Vakje eind = layout.krijgVakje(3, 1);
        p.zetStartPositie(start);
        p.zetDoel(start);
        p.voegTussendoelToe(tussendoel);
        p.voegTussendoelToe(eind);
        p.beweeg();
        assertEquals(tussendoel, p.doelVakje);
    }
}
