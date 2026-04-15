import Model.persoon.Gast;
import Model.ruimte.Kamer;
import Model.layout.Vakje;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GastTest {

    @Test
    void testConstructor() {
        Gast g = new Gast(1, 3);
        assertEquals(1, g.gastId);
        assertEquals(3, g.gewensteSterren);
        assertNull(g.kamer);
    }

    @Test void testHuidigVakjeNull() { assertNull(new Gast(1, 2).huidigVakje); }
    @Test void testDoelVakjeNull() { assertNull(new Gast(1, 2).doelVakje); }

    @Test
    void testGaNaarkamerZonderKamerCrashetNiet() {
        assertDoesNotThrow(() -> new Gast(1, 2).gaNaarkamer());
    }

    @Test
    void testVerlaatKamerZonderKamerCrashetNiet() {
        assertDoesNotThrow(() -> new Gast(1, 2).verlaatKamer());
    }

    @Test
    void testGaNaarkamerMetKamer() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertDoesNotThrow(() -> g.gaNaarkamer());
        assertTrue(k.isGastAanwezig(g));
    }

    @Test
    void testVerlaatKamerMetKamer() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        g.verlaatKamer();
        assertFalse(k.isGastAanwezig(g));
    }

    @Test
    void testZetDoel() {
        Gast g = new Gast(1, 2);
        Vakje v = new Vakje();
        g.zetDoel(v);
        assertEquals(v, g.doelVakje);
    }
}
