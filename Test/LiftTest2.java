import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Lift: wachtrijen, instappen, uitstappen, brandalarm
public class LiftTest2 {

    private Hotel hotel;
    private Lift lift;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(6, 5);
        hotel.breedte = 6;
        hotel.hoogte = 5;

        lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 5;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);
        trap.posX = 6; trap.posY = 1; trap.breedte = 1; trap.hoogte = 5;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);
        lift.initWachtrijen(5);
    }

    // lift start op verdieping 1
    @Test void testStartVerdieping() {
        assertEquals(1, lift.getHuidigeVerdieping());
    }

    // lift heeft geen passagiers bij aanvang
    @Test void testGeenPassagiersAanvang() {
        assertTrue(lift.getPassagiers().isEmpty());
    }

    // roep voegt persoon toe aan wachtrij
    @Test void testRoepVoegtToeAanWachtrij() {
        Gast g = new Gast(1, 1);
        lift.roep(g, 3);
        assertEquals(1, lift.aantalWachtend(3));
    }

    // roep voegt dezelfde persoon niet twee keer toe
    @Test void testRoepDubbelNietToegevoegd() {
        Gast g = new Gast(1, 1);
        lift.roep(g, 3);
        lift.roep(g, 3);
        assertEquals(1, lift.aantalWachtend(3));
    }

    // roep wordt genegeerd als lift buiten gebruik is
    @Test void testRoepGenegeerdBijBrandalarm() {
        lift.zetUitBedrijf(true);
        Gast g = new Gast(1, 1);
        lift.roep(g, 3);
        assertEquals(0, lift.aantalWachtend(3));
    }

    // aantalWachtend geeft 0 voor lege verdieping
    @Test void testAantalWachtendLeeg() {
        assertEquals(0, lift.aantalWachtend(2));
    }

    // aantalWachtend geeft 0 voor onbekende verdieping
    @Test void testAantalWachtendOnbekend() {
        assertEquals(0, lift.aantalWachtend(99));
    }

    // tik beweegt lift omhoog naar wachtende gast
    @Test void testTikBeweegOmhoog() {
        Gast g = new Gast(1, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 3));
        lift.roep(g, 3);
        lift.tik(); // lift gaat van 1 naar 2
        assertEquals(2, lift.getHuidigeVerdieping());
    }

    // tik laadt gast in als lift op zijn verdieping aankomt
    @Test void testTikLaadtGastIn() {
        Gast g = new Gast(1, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        g.gewensteVerdieping = 3;
        lift.roep(g, 1);
        lift.tik(); // lift staat al op 1, laadt in
        assertTrue(g.inLift);
        assertEquals(1, lift.getPassagiers().size());
    }

    // tik zet gast op lift-vakje na inladen
    @Test void testTikZetGastOpLiftVakje() {
        Gast g = new Gast(1, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        g.gewensteVerdieping = 3;
        lift.roep(g, 1);
        lift.tik();
        assertNotNull(g.huidigVakje);
        assertEquals(1, g.huidigVakje.x); // lift staat op x=1
    }

    // tik laat gast uitstappen op doelverdieping
    @Test void testTikLaatGastUitstappen() {
        Gast g = new Gast(1, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        g.gewensteVerdieping = 1;
        lift.roep(g, 1);
        lift.tik(); // inladen op verdieping 1
        lift.tik(); // uitladen want gewensteVerdieping == huidigeVerdieping
        assertFalse(g.inLift);
        assertTrue(g.moetUitstappen);
    }

    // tik doet niets als lift buiten gebruik is en geen passagiers heeft
    @Test void testTikBuitenGebruikZonderPassagiers() {
        lift.zetUitBedrijf(true);
        assertDoesNotThrow(() -> lift.tik());
        assertEquals(1, lift.getHuidigeVerdieping()); // niet bewogen
    }

    // zetUitBedrijf true en false werkt correct
    @Test void testZetUitBedrijf() {
        lift.zetUitBedrijf(true);
        // roep wordt genegeerd
        Gast g = new Gast(1, 1);
        lift.roep(g, 2);
        assertEquals(0, lift.aantalWachtend(2));

        lift.zetUitBedrijf(false);
        lift.roep(g, 2);
        assertEquals(1, lift.aantalWachtend(2));
    }

    // lift erft van Ruimte: posX is correct ingesteld
    @Test void testErftVanRuimte() {
        assertEquals(1, lift.posX);
        assertEquals(1, lift.posY);
        assertEquals(1, lift.breedte);
        assertEquals(5, lift.hoogte);
    }

    // meerdere gasten in wachtrij worden allemaal ingeladen
    @Test void testMeerdereGastenIngeladen() {
        Gast g1 = new Gast(1, 1);
        Gast g2 = new Gast(2, 1);
        g1.setPathfinder(hotel.pathfinder);
        g2.setPathfinder(hotel.pathfinder);
        g1.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        g2.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        g1.gewensteVerdieping = 3;
        g2.gewensteVerdieping = 4;
        lift.roep(g1, 1);
        lift.roep(g2, 1);
        lift.tik();
        assertEquals(2, lift.getPassagiers().size());
    }
}
