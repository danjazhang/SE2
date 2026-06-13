import Model.Hotel;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Ruimte;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Hotel: personen toevoegen, ruimtes opvragen, brandalarm
public class HotelTest2 {

    // nieuw hotel heeft lege lijsten
    @Test void testNieuwHotelLeeg() {
        Hotel h = new Hotel();
        assertTrue(h.ruimtes.isEmpty());
        assertTrue(h.personen.isEmpty());
    }

    // brandalarm is standaard niet actief
    @Test void testBrandalarmStandaardUit() {
        assertFalse(new Hotel().brandalarmActief);
    }

    // voegPersoonToe voegt persoon toe aan lijst
    @Test void testVoegPersoonToe() {
        Hotel h = new Hotel();
        Gast g = new Gast(1, 1);
        h.voegPersoonToe(g);
        assertEquals(1, h.personen.size());
        assertTrue(h.personen.contains(g));
    }

    // meerdere personen toevoegen
    @Test void testMeerderePersonenToevoegen() {
        Hotel h = new Hotel();
        h.voegPersoonToe(new Gast(1, 1));
        h.voegPersoonToe(new Gast(2, 2));
        assertEquals(2, h.personen.size());
    }

    // krijgRuimteOp zonder layout geeft null
    @Test void testKrijgRuimteOpZonderLayout() {
        Hotel h = new Hotel();
        assertNull(h.krijgRuimteOp(1, 1));
    }

    // krijgRuimteOp geeft de juiste ruimte terug
    @Test void testKrijgRuimteOpGeeftRuimte() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        Kamer k = new Kamer();
        k.posX = 2; k.posY = 2; k.breedte = 1; k.hoogte = 1;
        h.layout.plaatsRuimte(k);
        assertSame(k, h.krijgRuimteOp(2, 2));
    }

    // krijgRuimteOp geeft null voor leeg vakje
    @Test void testKrijgRuimteOpLeegVakje() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        assertNull(h.krijgRuimteOp(3, 3));
    }

    // krijgRuimteOp geeft null buiten het grid
    @Test void testKrijgRuimteOpBuitenGrid() {
        Hotel h = new Hotel();
        h.layout = new Layout(3, 3);
        assertNull(h.krijgRuimteOp(10, 10));
    }

    // brandalarm kan worden ingesteld
    @Test void testBrandalarmInstelbaar() {
        Hotel h = new Hotel();
        h.brandalarmActief = true;
        assertTrue(h.brandalarmActief);
    }

    // lift, trap en lobby zijn standaard null
    @Test void testLiftTrapLobbyNull() {
        Hotel h = new Hotel();
        assertNull(h.lift);
        assertNull(h.trap);
        assertNull(h.lobby);
    }

    // breedte en hoogte kunnen worden ingesteld
    @Test void testBreedteHoogte() {
        Hotel h = new Hotel();
        h.breedte = 10;
        h.hoogte = 8;
        assertEquals(10, h.breedte);
        assertEquals(8, h.hoogte);
    }

    // ruimtes kunnen worden toegevoegd aan de lijst
    @Test void testRuimtesToevoegen() {
        Hotel h = new Hotel();
        Ruimte r = new Ruimte();
        h.ruimtes.add(r);
        assertEquals(1, h.ruimtes.size());
    }
}
