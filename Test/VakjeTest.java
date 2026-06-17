import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.Ruimte;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Vakje: personen toevoegen/verwijderen, ruimte koppelen
public class VakjeTest {

    // constructor: personen lijst is leeg
    @Test void testConstructorPersonenLeeg() {
        assertTrue(new Vakje().personen.isEmpty());
    }

    // voegPersoonToe: persoon zit in de lijst
    @Test void testVoegPersoonToe() {
        Vakje v = new Vakje();
        Gast g = new Gast(1, 1);
        v.voegPersoonToe(g);
        assertTrue(v.personen.contains(g));
    }

    // verwijderPersoon: persoon is niet meer in de lijst
    @Test void testVerwijderPersoon() {
        Vakje v = new Vakje();
        Gast g = new Gast(1, 1);
        v.voegPersoonToe(g);
        v.verwijderPersoon(g);
        assertFalse(v.personen.contains(g));
    }

    // verwijderPersoon: niet-aanwezige persoon geeft geen crash
    @Test void testVerwijderNietAanwezig() {
        Vakje v = new Vakje();
        assertDoesNotThrow(() -> v.verwijderPersoon(new Gast(1, 1)));
    }

    // krijgPersonen: geeft kopie terug
    @Test void testKrijgPersonenGeeftKopie() {
        Vakje v = new Vakje();
        v.voegPersoonToe(new Gast(1, 1));
        assertNotSame(v.krijgPersonen(), v.krijgPersonen());
    }

    // krijgPersonen: bevat de toegevoegde personen
    @Test void testKrijgPersonenBevat() {
        Vakje v = new Vakje();
        Gast g = new Gast(1, 1);
        v.voegPersoonToe(g);
        assertTrue(v.krijgPersonen().contains(g));
    }

    // setRuimte en getRuimte
    @Test void testSetEnGetRuimte() {
        Vakje v = new Vakje();
        Ruimte r = new Ruimte();
        v.setRuimte(r);
        assertEquals(r, v.getRuimte());
    }

    // getX en getY: geven de ingestelde coördinaten terug
    @Test void testGetXEnY() {
        Vakje v = new Vakje();
        v.x = 3;
        v.y = 7;
        assertEquals(3, v.getX());
        assertEquals(7, v.getY());
    }

    // meerdere personen op hetzelfde vakje
    @Test void testMeerderePersonenOpVakje() {
        Vakje v = new Vakje();
        v.voegPersoonToe(new Gast(1, 1));
        v.voegPersoonToe(new Gast(2, 2));
        assertEquals(2, v.personen.size());
    }
}
