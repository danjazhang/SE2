import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Lift: constructor, roep, tik, wachtrijen, instappen, uitstappen, brandalarm
public class LiftTest {

    private Hotel hotel;
    private Lift lift;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(6, 5);
        hotel.breedte = 6;
        hotel.hoogte = 5;

        Trap trap = new Trap(2);
        trap.posX = 6; trap.posY = 1; trap.breedte = 1; trap.hoogte = 5;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 5;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        hotel.pathfinder = new Pathfinder(hotel);
        lift.initWachtrijen(5);
        lift.setLobbyVerdieping(1);
    }

    // constructor: lift begint op lobby-verdieping
    @Test void testConstructorStartPositie() {
        assertEquals(1, lift.getHuidigeVerdieping());
    }

    // constructor: passagierslijst is leeg
    @Test void testConstructorGeenPassagiers() {
        assertTrue(lift.getPassagiers().isEmpty());
    }

    // erft van Ruimte: posX/posY/breedte/hoogte correct
    @Test void testErftVanRuimte() {
        assertEquals(1, lift.posX);
        assertEquals(1, lift.posY);
        assertEquals(1, lift.breedte);
        assertEquals(5, lift.hoogte);
    }

    // setLobbyVerdieping: lift start op de ingestelde lobby
    @Test void testSetLobbyVerdieping() {
        lift.setLobbyVerdieping(2);
        assertEquals(2, lift.getHuidigeVerdieping());
    }

    // roep: gast wordt toegevoegd aan wachtrij
    @Test void testRoepVoegtToeAanWachtrij() {
        Gast g = maakGast(1, 2, 1);
        lift.roep(g, 1);
        assertEquals(1, lift.aantalWachtend(1));
    }

    // roep: dubbele oproep wordt genegeerd
    @Test void testRoepDubbelWordtGenegeerd() {
        Gast g = maakGast(1, 2, 1);
        lift.roep(g, 1);
        lift.roep(g, 1);
        assertEquals(1, lift.aantalWachtend(1));
    }

    // roep: lift buiten gebruik accepteert geen oproepen
    @Test void testRoepGeweigerdAlsUitBedrijf() {
        lift.zetUitBedrijf(true);
        lift.roep(maakGast(1, 2, 1), 1);
        assertEquals(0, lift.aantalWachtend(1));
    }

    // aantalWachtend: geeft 0 voor onbekende verdieping
    @Test void testAantalWachtendOnbekend() {
        assertEquals(0, lift.aantalWachtend(99));
    }

    // aantalWachtend: geeft 0 voor lege wachtrij
    @Test void testAantalWachtendLeeg() {
        assertEquals(0, lift.aantalWachtend(2));
    }

    // tik: lift beweegt omhoog naar wachtende gast
    @Test void testTikBeweegNaarWachtende() {
        Gast g = maakGast(1, 2, 3);
        lift.roep(g, 3);
        lift.tik(); // 1 → 2
        assertEquals(2, lift.getHuidigeVerdieping());
    }

    // tik: lift staat stil als niemand wacht
    @Test void testTikStilZonderVraag() {
        lift.tik();
        assertEquals(1, lift.getHuidigeVerdieping());
    }

    // tik: gast op verdieping 1 stapt in als lift er al staat
    @Test void testTikGastStaptIn() {
        Gast g = maakGast(1, 2, 1);
        g.gewensteVerdieping = 3;
        lift.roep(g, 1);
        lift.tik(); // INSTAPPEN
        assertTrue(g.inLift);
        assertEquals(1, lift.getPassagiers().size());
    }

    // tik: gast staat op lift-vakje na inladen
    @Test void testTikZetGastOpLiftVakje() {
        Gast g = maakGast(1, 2, 1);
        g.gewensteVerdieping = 3;
        lift.roep(g, 1);
        lift.tik();
        assertNotNull(g.huidigVakje);
        assertEquals(1, g.huidigVakje.x);
    }

    // tik: gast stapt uit op doelverdieping
    @Test void testTikLaatGastUitstappen() {
        Gast g = maakGast(1, 2, 1);
        g.gewensteVerdieping = 1;
        lift.roep(g, 1);
        lift.tik(); // inladen
        lift.tik(); // uitladen (gewenste == huidige)
        assertFalse(g.inLift);
        assertTrue(g.moetUitstappen);
    }

    // tik uitBedrijf: lift zonder passagiers beweegt niet
    @Test void testTikUitBedrijfZonderPassagiers() {
        lift.zetUitBedrijf(true);
        lift.tik();
        assertEquals(1, lift.getHuidigeVerdieping());
    }

    // meerdere gasten op dezelfde verdieping worden allemaal ingeladen
    @Test void testMeerdereGastenIngeladen() {
        Gast g1 = maakGast(1, 2, 1);
        Gast g2 = maakGast(2, 2, 1);
        g1.gewensteVerdieping = 3;
        g2.gewensteVerdieping = 4;
        lift.roep(g1, 1);
        lift.roep(g2, 1);
        lift.tik();
        assertEquals(2, lift.getPassagiers().size());
    }

    // resetWachtrijen: alle wachtrijen worden leeggemaakt
    @Test void testResetWachtrijen() {
        lift.roep(maakGast(1, 2, 2), 2);
        lift.resetWachtrijen();
        assertEquals(0, lift.aantalWachtend(2));
    }

    // verwijderUitWachtrij: gast wordt verwijderd
    @Test void testVerwijderUitWachtrij() {
        Gast g = maakGast(1, 2, 1);
        lift.roep(g, 1);
        lift.verwijderUitWachtrij(g);
        assertEquals(0, lift.aantalWachtend(1));
    }

    // getPassagiers: geeft kopie terug
    @Test void testGetPassagiersGeeftKopie() {
        assertNotSame(lift.getPassagiers(), lift.getPassagiers());
    }

    // zetUitBedrijf: kan aan- en uitgezet worden
    @Test void testZetUitBedrijfAanEnUit() {
        lift.zetUitBedrijf(true);
        lift.zetUitBedrijf(false);
        Gast g = maakGast(1, 2, 1);
        lift.roep(g, 1);
        assertEquals(1, lift.aantalWachtend(1));
    }

    // initWachtrijen: wachtrijen worden aangemaakt voor alle verdiepingen
    @Test void testInitWachtrijen() {
        lift.initWachtrijen(5);
        assertEquals(0, lift.aantalWachtend(1));
        assertEquals(0, lift.aantalWachtend(5));
    }

    // tik: lift beweegt naar wachtende gast op hogere verdieping
    @Test void testTikBeweegNaarHogeVerdieping() {
        Gast g = maakGast(1, 2, 4);
        lift.roep(g, 4);
        lift.tik(); // 1 → 2
        assertEquals(2, lift.getHuidigeVerdieping());
    }

    // hulpmethode
    private Gast maakGast(int id, int x, int y) {
        Gast g = new Gast(id, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(x, y));
        hotel.voegPersoonToe(g);
        return g;
    }
}
