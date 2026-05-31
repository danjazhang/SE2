import Model.persoon.Gast;
import Model.ruimte.Kamer;
import Model.layout.Vakje;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GastTest {

    // Ik maak een nieuwe gast aan; ik verwacht dat id, gewenste sterren en kamer correct starten.
    @Test
    void testConstructor() {
        Gast g = new Gast(1, 3);
        assertEquals(1, g.gastId);
        assertEquals(3, g.gewensteSterren);
        assertNull(g.kamer);
    }

    // Ik maak een nieuwe gast aan; ik verwacht dat hij nog geen huidig vakje heeft.
    @Test void testHuidigVakjeNull() { assertNull(new Gast(1, 2).huidigVakje); }
    // Ik maak een nieuwe gast aan; ik verwacht dat hij nog geen doelvakje heeft.
    @Test void testDoelVakjeNull() { assertNull(new Gast(1, 2).doelVakje); }

    // Ik laat een gast zonder kamer naar binnen gaan; ik verwacht dat dit geen crash geeft.
    @Test
    void testGaNaarkamerZonderKamerCrashetNiet() {
        assertDoesNotThrow(() -> new Gast(1, 2).gaNaarkamer());
    }

    // Ik laat een gast zonder kamer een kamer verlaten; ik verwacht dat dit geen crash geeft.
    @Test
    void testVerlaatKamerZonderKamerCrashetNiet() {
        assertDoesNotThrow(() -> new Gast(1, 2).verlaatKamer());
    }

    // Ik koppel een gast aan een kamer en laat hem de kamer binnen gaan;
    // ik verwacht dat de kamer de gast daarna als aanwezig ziet.
    @Test
    void testGaNaarkamerMetKamer() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        assertDoesNotThrow(() -> g.gaNaarkamer());
        assertTrue(k.isGastAanwezig(g));
    }

    // Ik laat een gast die al in een kamer zit weer vertrekken;
    // ik verwacht dat de kamer hem daarna niet meer als aanwezig ziet.
    @Test
    void testVerlaatKamerMetKamer() {
        Kamer k = new Kamer();
        Gast g = new Gast(1, 2);
        k.koppelGast(g);
        k.gastKomtBinnen(g);
        g.verlaatKamer();
        assertFalse(k.isGastAanwezig(g));
    }

    // Ik geef een gast een doelvakje; ik verwacht dat dit doel correct bewaard wordt.
    @Test
    void testZetDoel() {
        Gast g = new Gast(1, 2);
        Vakje v = new Vakje();
        g.zetDoel(v);
        assertEquals(v, g.doelVakje);
    }
}
