import Model.PersonenFactory;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.layout.Layout;
import Model.layout.Vakje;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersonenFactoryTest {

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

    @Test void testMaakGastZonderStartVakje() {
        PersonenFactory f = new PersonenFactory();
        Gast g = f.maakGast(1, 2, null, null);
        assertNull(g.huidigVakje);
    }

    @Test void testMaakSchoonmaker() {
        PersonenFactory f = new PersonenFactory();
        Layout l = new Layout(3, 3);
        Vakje v = l.krijgVakje(1, 1);
        Schoonmaker s = f.maakSchoonmaker(l, v);
        assertEquals(v, s.huidigVakje);
        assertEquals(l, s.layout);
    }

    @Test void testMaakSchoonmakerZonderStartVakje() {
        PersonenFactory f = new PersonenFactory();
        Schoonmaker s = f.maakSchoonmaker(null, null);
        assertNull(s.huidigVakje);
    }
}
