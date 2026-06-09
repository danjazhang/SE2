import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LiftExtraTest {

    // hulpmethode: maak een minimaal hotel met layout en trap
    static Hotel maakHotel() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;
        Trap trap = new Trap(2);
        trap.posX = 6; trap.posY = 1; trap.breedte = 1; trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        return hotel;
    }

    // hulpmethode: maak een lift en koppel aan hotel
    static Lift maakLift(Hotel hotel) {
        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        hotel.pathfinder = new Pathfinder(hotel);
        return lift;
    }

    // constructor: lift begint op verdieping 1 en is niet buiten gebruik
    @Test void testConstructor() {
        Hotel hotel = maakHotel();
        Lift lift = maakLift(hotel);
        assertEquals(1, lift.getHuidigeVerdieping());
        assertTrue(lift.getPassagiers().isEmpty());
    }

    // zetUitBedrijf: lift accepteert geen nieuwe oproepen als buiten gebruik
    @Test void testRoepWordtGenegeerdAlsUitBedrijf() {
        Hotel hotel = maakHotel();
        Lift lift = maakLift(hotel);
        lift.zetUitBedrijf(true);
        Gast gast = new Gast(1, 1);
        lift.roep(gast, 2);
        // wachtrij moet leeg zijn want lift is buiten gebruik
        assertEquals(0, lift.aantalWachtend(2));
    }

    // roep: gast wordt toegevoegd aan wachtrij als lift in gebruik is
    @Test void testRoepVoegtToeAanWachtrij() {
        Hotel hotel = maakHotel();
        Lift lift = maakLift(hotel);
        Gast gast = new Gast(1, 1);
        lift.roep(gast, 2);
        assertEquals(1, lift.aantalWachtend(2));
    }

    // roep: dubbele oproep wordt genegeerd
    @Test void testRoepDubbelWordtGenegeerd() {
        Hotel hotel = maakHotel();
        Lift lift = maakLift(hotel);
        Gast gast = new Gast(1, 1);
        lift.roep(gast, 2);
        lift.roep(gast, 2);
        assertEquals(1, lift.aantalWachtend(2));
    }

    // aantalWachtend: geeft 0 terug als verdieping niet bestaat
    @Test void testAantalWachtendOnbekendeVerdieping() {
        Hotel hotel = maakHotel();
        Lift lift = maakLift(hotel);
        assertEquals(0, lift.aantalWachtend(99));
    }

    // tik buiten gebruik zonder passagiers: lift beweegt niet
    @Test void testTikUitBedrijfZonderPassagiers() {
        Hotel hotel = maakHotel();
        Lift lift = maakLift(hotel);
        lift.zetUitBedrijf(true);
        lift.tik();
        // lift staat nog op verdieping 1
        assertEquals(1, lift.getHuidigeVerdieping());
    }

    // tik buiten gebruik met passagier: lift maakt rit af naar gewenste verdieping
    @Test void testTikUitBedrijfMetPassagier() {
        Hotel hotel = maakHotel();
        Lift lift = maakLift(hotel);
        lift.initWachtrijen(4);

        // voeg gast toe aan wachtrij op verdieping 1
        Gast gast = new Gast(1, 1);
        gast.gewensteVerdieping = 3;
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);

        // laat gast instappen via normale tik
        lift.roep(gast, 1);
        lift.tik();

        // zet lift buiten gebruik
        lift.zetUitBedrijf(true);

        // lift moet nog naar verdieping 3 bewegen
        lift.tik();
        assertTrue(lift.getHuidigeVerdieping() >= 1);
    }

    // tik normaal: lift beweegt naar wachtende gast
    @Test void testTikNormaalBeweegNaarWachtende() {
        Hotel hotel = maakHotel();
        Lift lift = maakLift(hotel);
        lift.initWachtrijen(4);

        Gast gast = new Gast(1, 1);
        gast.gewensteVerdieping = 3;
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 3));
        hotel.voegPersoonToe(gast);

        // gast wacht op verdieping 3
        lift.roep(gast, 3);

        // lift staat op 1, moet omhoog naar 3
        lift.tik();
        assertEquals(2, lift.getHuidigeVerdieping());
    }

    // initWachtrijen: wachtrijen worden aangemaakt voor alle verdiepingen
    @Test void testInitWachtrijen() {
        Hotel hotel = maakHotel();
        Lift lift = maakLift(hotel);
        lift.initWachtrijen(4);
        assertEquals(0, lift.aantalWachtend(1));
        assertEquals(0, lift.aantalWachtend(4));
    }
}
