import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Schoonmaker: ik test hier alleen het uitvoerende gedrag.
// De eventlogica hoort nu niet meer bij de schoonmaker zelf,
// maar bij de aparte SchoonmaakService.
public class SchoonmakerTest {

    // Ik maak een nieuwe schoonmaker aan; ik verwacht dat hij nog niet bezig is
    // en nog geen kamer toegewezen heeft gekregen.
    @Test void testConstructor() {
        Schoonmaker s = new Schoonmaker();
        assertFalse(s.bezig);
        assertNull(s.kamer);
    }

    // Ik gebruik een schoonmaker als subklasse van Persoon; ik verwacht dat hij
    // nog geen startpositie of doel heeft zolang ik niets instel.
    @Test void testErftVanPersoon() {
        Schoonmaker s = new Schoonmaker();
        assertNull(s.huidigVakje);
        assertNull(s.doelVakje);
    }

    // Ik geef de schoonmaker een kamer via maakKamerSchoon; ik verwacht dat hij
    // die kamer bewaart en vanaf dan als bezig gemarkeerd staat.
    @Test void testMaakKamerSchoonZetTaak() {
        Schoonmaker s = new Schoonmaker();
        Kamer kamer = new Kamer();

        s.maakKamerSchoon(kamer);

        assertTrue(s.bezig);
        assertEquals(kamer, s.kamer);
    }
}
