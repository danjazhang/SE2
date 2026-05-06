import Model.PersonenFactory;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.layout.Layout;
import Model.layout.Vakje;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor PersonenFactory: ik test het aanmaken van Gast en Schoonmaker.
public class PersonenFactoryTest {

    // Ik maak een Gast via de factory; ik verwacht juiste velden, layout en startpositie.
    @Test void testMaakGast() {
        PersonenFactory f = new PersonenFactory();
        Layout l = new Layout(3, 3);
        Vakje v = l.krijgVakje(1, 1);
        Gast g = f.maakGast(1, 3, l, v);
        assertEquals(1, g.gastId);
        assertEquals(3, g.gewensteSterren);
        assertEquals(v, g.huidigVakje);
        assertEquals(l, g.layout);
    }

    // Ik maak een Gast zonder startvakje; ik verwacht dat huidigVakje null blijft.
    @Test void testMaakGastZonderStartVakje() {
        PersonenFactory f = new PersonenFactory();
        Gast g = f.maakGast(1, 2, null, null);
        assertNull(g.huidigVakje);
    }

    // Ik maak een Schoonmaker via de factory; ik verwacht juiste layout en startpositie.
    @Test void testMaakSchoonmaker() {
        PersonenFactory f = new PersonenFactory();
        Layout l = new Layout(3, 3);
        Vakje v = l.krijgVakje(1, 1);
        Schoonmaker s = f.maakSchoonmaker(l, v);
        assertEquals(v, s.huidigVakje);
        assertEquals(l, s.layout);
    }

    // Ik maak een Schoonmaker zonder startvakje; ik verwacht dat huidigVakje null blijft.
    @Test void testMaakSchoonmakerZonderStartVakje() {
        PersonenFactory f = new PersonenFactory();
        Schoonmaker s = f.maakSchoonmaker(null, null);
        assertNull(s.huidigVakje);
    }
}
