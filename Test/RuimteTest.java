import Model.persoon.Gast;
import Model.ruimte.Ruimte;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Ruimte basisklasse
public class RuimteTest {

    // constructor met positie en afmetingen
    @Test void testConstructorMetPositie() {
        Ruimte r = new Ruimte(2, 3, 4, 5);
        assertEquals(2, r.posX);
        assertEquals(3, r.posY);
        assertEquals(4, r.breedte);
        assertEquals(5, r.hoogte);
    }

    // lege constructor
    @Test void testLegeConstructor() {
        Ruimte r = new Ruimte();
        assertEquals(0, r.posX);
        assertEquals(0, r.posY);
    }

    // betreed: persoon wordt toegevoegd aan aanwezigen
    @Test void testBetreed() {
        Ruimte r = new Ruimte();
        Gast g = new Gast(1, 1);
        r.betreed(g);
        assertTrue(r.getAanwezigen().contains(g));
    }

    // verlaat: persoon wordt verwijderd uit aanwezigen
    @Test void testVerlaat() {
        Ruimte r = new Ruimte();
        Gast g = new Gast(1, 1);
        r.betreed(g);
        r.verlaat(g);
        assertFalse(r.getAanwezigen().contains(g));
    }

    // getAanwezigen: beginnen leeg
    @Test void testAanwezigenLeeg() {
        assertTrue(new Ruimte().getAanwezigen().isEmpty());
    }

    // getAanwezigen: geeft kopie terug
    @Test void testGetAanwezigenGeeftKopie() {
        Ruimte r = new Ruimte();
        assertNotSame(r.getAanwezigen(), r.getAanwezigen());
    }

    // getVrijeKamer: standaard null
    @Test void testGetVrijeKamerNull() {
        assertNull(new Ruimte().getVrijeKamer());
    }

    // isKamer: standaard false
    @Test void testIsKamerFalse() {
        assertFalse(new Ruimte().isKamer());
    }

    // isFaciliteit: standaard false
    @Test void testIsFaciliteitFalse() {
        assertFalse(new Ruimte().isFaciliteit());
    }

    // setFilmDuur: geen crash (no-op in basis Ruimte)
    @Test void testSetFilmDuurGeenCrash() {
        assertDoesNotThrow(() -> new Ruimte().setFilmDuur(50));
    }

    // setTijdPerVerdieping: geen crash (no-op in basis Ruimte)
    @Test void testSetTijdPerVerdiepingGeenCrash() {
        assertDoesNotThrow(() -> new Ruimte().setTijdPerVerdieping(3));
    }

    // getStatusTekst: standaard lege string
    @Test void testGetStatusTekstLeeg() {
        assertEquals("", new Ruimte().getStatusTekst());
    }

    // getNaam: geeft klassenaam in kleine letters
    @Test void testGetNaam() {
        assertEquals("ruimte", new Ruimte().getNaam());
    }
}
