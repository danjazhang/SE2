import Model.persoon.Persoon;
import Model.layout.Vakje;
import Model.layout.Layout;
import Model.ruimte.Ruimte;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Persoon: ik test positie, doelen en stap-voor-stap bewegen.
public class PersoonTest {

    // Ik maak een Persoon; ik verwacht nog geen positie en geen doel.
    @Test void testConstructor() {
        Persoon p = new Persoon();
        assertNull(p.huidigVakje);
        assertNull(p.doelVakje);
    }

    // Ik zet een doelvakje; ik verwacht dat doelVakje daarna dat vakje is.
    @Test void testZetDoel() {
        Persoon p = new Persoon();
        Vakje v = new Vakje();
        p.zetDoel(v);
        assertEquals(v, p.doelVakje);
    }

    // Ik zet een startpositie; ik verwacht dat de Persoon ook in het Vakje staat.
    @Test void testZetStartPositie() {
        Persoon p = new Persoon();
        Vakje v = new Vakje();
        p.zetStartPositie(v);
        assertEquals(v, p.huidigVakje);
        assertTrue(v.krijgPersonen().contains(p));
    }

    // Ik laat een Persoon zonder doel bewegen; ik verwacht geen exception.
    @Test void testBeweegZonderDoelCrashetNiet() {
        assertDoesNotThrow(() -> new Persoon().beweeg());
    }

    // Ik laat een Persoon zonder layout bewegen; ik verwacht dat dit veilig stopt.
    @Test void testBeweegZonderLayoutCrashetNiet() {
        Persoon p = new Persoon();
        Vakje v = new Vakje();
        p.zetStartPositie(v);
        p.zetDoel(new Vakje());
        assertDoesNotThrow(() -> p.beweeg());
    }

    // Ik laat een Persoon naar een doel bewegen; ik verwacht dat hij een stap richting doel zet.
    @Test void testBeweegNaarDoel() {
        Layout layout = new Layout(3, 3);
        Persoon p = new Persoon();
        p.layout = layout;
        p.zetStartPositie(layout.krijgVakje(1, 1));
        p.zetDoel(layout.krijgVakje(3, 1));
        p.beweeg();
        assertEquals(2, p.huidigVakje.x);
    }

    // Ik voeg een tussendoel toe; ik verwacht dat het huidige doel behouden blijft.
    @Test void testVoegTussendoelToe() {
        Persoon p = new Persoon();
        Vakje v1 = new Vakje();
        Vakje v2 = new Vakje();
        p.zetDoel(v1);
        p.voegTussendoelToe(v2);
        assertEquals(v1, p.doelVakje);
    }

    // Ik laat een Persoon bij zijn doel starten; ik verwacht dat het volgende tussendoel actief wordt.
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
