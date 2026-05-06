import Model.persoon.Gast;
import Model.ruimte.Kamer;
import Model.layout.Vakje;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Gast: ik test gastgegevens en kamer-acties.
public class GastTest {

    // Ik maak een Gast; ik verwacht dat gastId, gewenste sterren en kamer goed starten.
    @Test
    void testConstructor() {
        Gast g = new Gast(1, 3);
        assertEquals(1, g.gastId);
        assertEquals(3, g.gewensteSterren);
        assertNull(g.kamer);
    }

    // Ik maak een nieuwe Gast; ik verwacht dat hij nog geen huidig vakje heeft.
    @Test void testHuidigVakjeNull() { assertNull(new Gast(1, 2).huidigVakje); }
    // Ik maak een nieuwe Gast; ik verwacht dat hij nog geen doelvakje heeft.
    @Test void testDoelVakjeNull() { assertNull(new Gast(1, 2).doelVakje); }

    // Ik laat een Gast zonder kamer naar kamer gaan; ik verwacht geen exception.
    @Test
    void testGaNaarkamerZonderKamerCrashetNiet() {
        assertDoesNotThrow(() -> new Gast(1, 2).gaNaarkamer());
    }

    // Ik laat een Gast zonder kamer de kamer verlaten; ik verwacht geen exception.
    @Test
    void testVerlaatKamerZonderKamerCrashetNiet() {
        assertDoesNotThrow(() -> new Gast(1, 2).verlaatKamer());
    }

    // Ik koppel een Gast aan een Kamer en laat hem naar kamer gaan; ik verwacht dat hij aanwezig is.
    @Test
    void testGaNaarkamerMetKamer() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertDoesNotThrow(() -> g.gaNaarkamer());
        assertTrue(k.isGastAanwezig(g));
    }

    // Ik zet een Gast in een Kamer en laat hem vertrekken; ik verwacht dat hij niet meer aanwezig is.
    @Test
    void testVerlaatKamerMetKamer() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        g.verlaatKamer();
        assertFalse(k.isGastAanwezig(g));
    }

    // Ik zet een doelvakje; ik verwacht dat dit doel in de Gast wordt opgeslagen.
    @Test
    void testZetDoel() {
        Gast g = new Gast(1, 2);
        Vakje v = new Vakje();
        g.zetDoel(v);
        assertEquals(v, g.doelVakje);
    }
}
