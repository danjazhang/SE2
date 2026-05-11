import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersoonTest {

    // hulpmethode: maak een hotel met layout, lift en trap
    private Hotel maakHotel(int breedte, int hoogte) {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(breedte, hoogte);
        hotel.breedte = breedte;
        hotel.hoogte = hoogte;

        Lift lift = new Lift();
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = hoogte;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);
        trap.posX = breedte; trap.posY = 1; trap.breedte = 1; trap.hoogte = hoogte;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);
        return hotel;
    }

    // constructor: huidigVakje en doelVakje zijn null bij aanmaken
    @Test void testConstructor() {
        Gast p = new Gast(1, 1);
        assertNull(p.huidigVakje);
        assertNull(p.doelVakje);
    }

    // zetDoel: doelVakje wordt correct ingesteld
    @Test void testZetDoel() {
        Gast p = new Gast(1, 1);
        Vakje v = new Vakje();
        p.zetDoel(v);
        assertEquals(v, p.doelVakje);
    }

    // zetStartPositie: huidigVakje wordt ingesteld en persoon staat op vakje
    @Test void testZetStartPositie() {
        Gast p = new Gast(1, 1);
        Vakje v = new Vakje();
        p.zetStartPositie(v);
        assertEquals(v, p.huidigVakje);
        assertTrue(v.krijgPersonen().contains(p));
    }

    // beweeg: geen crash als doelVakje null is
    @Test void testBeweegZonderDoelCrashetNiet() {
        assertDoesNotThrow(() -> new Gast(1, 1).beweeg());
    }

    // beweeg: geen crash als pathfinder null is
    @Test void testBeweegZonderPathfinderCrashetNiet() {
        Gast p = new Gast(1, 1);
        Layout layout = new Layout(3, 3);
        p.zetStartPositie(layout.krijgVakje(1, 1));
        p.zetDoel(layout.krijgVakje(3, 1));
        assertDoesNotThrow(() -> p.beweeg());
    }

    // beweeg: persoon beweegt 1 stap richting doel
    @Test void testBeweegNaarDoel() {
        Hotel hotel = maakHotel(5, 3);
        Gast p = new Gast(1, 1);
        p.setPathfinder(hotel.pathfinder);
        p.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        p.zetDoel(hotel.layout.krijgVakje(4, 1));
        p.beweeg();
        assertEquals(3, p.huidigVakje.x);
    }

    // beweeg: persoon beweegt in y richting als x gelijk is
    @Test void testBeweegInYRichting() {
        Hotel hotel = maakHotel(5, 5);
        Gast p = new Gast(1, 1);
        p.setPathfinder(hotel.pathfinder);
        p.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        p.zetDoel(hotel.layout.krijgVakje(2, 3));
        p.beweeg();
        assertEquals(2, p.huidigVakje.y);
    }

    // voegTussendoelToe: tussendoel wordt als doel gezet als doelVakje null is
    @Test void testVoegTussendoelToeZonderDoel() {
        Gast p = new Gast(1, 1);
        Vakje v = new Vakje();
        p.voegTussendoelToe(v);
        assertEquals(v, p.doelVakje);
    }

    // voegTussendoelToe: tussendoel wordt in queue gezet als doel al bestaat
    @Test void testVoegTussendoelToeMetDoel() {
        Gast p = new Gast(1, 1);
        Vakje v1 = new Vakje();
        Vakje v2 = new Vakje();
        p.zetDoel(v1);
        p.voegTussendoelToe(v2);
        assertEquals(v1, p.doelVakje);
    }

    // beweeg: tussendoel wordt volgend doel na bereiken huidig doel
    @Test void testTussendoelWordtDoelNaAankomen() {
        Hotel hotel = maakHotel(5, 3);
        Gast p = new Gast(1, 1);
        p.setPathfinder(hotel.pathfinder);
        Vakje start = hotel.layout.krijgVakje(2, 1);
        Vakje tussendoel = hotel.layout.krijgVakje(3, 1);
        p.zetStartPositie(start);
        p.zetDoel(start);
        p.voegTussendoelToe(tussendoel);
        p.beweeg();
        assertEquals(tussendoel, p.doelVakje);
    }

    // beweeg: persoon verlaat ruimte bij vertrek van vakje met ruimte
    @Test void testBeweegVerlaatRuimte() {
        Hotel hotel = maakHotel(5, 3);
        Gast p = new Gast(1, 1);
        p.setPathfinder(hotel.pathfinder);
        Kamer kamer = new Kamer();
        kamer.posX = 2; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        p.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        p.zetDoel(hotel.layout.krijgVakje(4, 1));
        p.beweeg();
        assertFalse(kamer.getAanwezigen().contains(p));
    }

    // setPathfinder: pathfinder wordt correct ingesteld
    @Test void testSetPathfinder() {
        Hotel hotel = maakHotel(5, 3);
        Gast p = new Gast(1, 1);
        assertDoesNotThrow(() -> p.setPathfinder(hotel.pathfinder));
    }

    // voerTaakUit: geen crash
    @Test void testVoerTaakUit() {
        assertDoesNotThrow(() -> new Gast(1, 1).voerTaakUit());
    }
}
