package tests;

import Model.Hotel;
import Model.layout.Layout;
import Model.layout.Vakje;

import Model.persoon.Gast;
import Model.persoon.Persoon;

import Model.ruimte.Lift;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LiftTest {

    // -------------------------------------------------
    // Hulpmethode:
    // Maak een testhotel met layout
    // -------------------------------------------------

    private Hotel maakHotel() {

        Hotel hotel = new Hotel();

        hotel.layout = new Layout(10, 10);

        hotel.breedte = 10;
        hotel.hoogte = 10;

        return hotel;
    }

    // -------------------------------------------------
    // Constructor tests
    // -------------------------------------------------

    // ik maak een nieuwe Lift aan met een geldig hotel; ik verwacht dat de lift correct wordt aangemaakt (niet null)
    @Test
    void testConstructor() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        assertNotNull(lift);
    }

    // ik maak een lift aan en controleer de beginstatus; ik verwacht dat de lift start op verdieping 1
    @Test
    void testStartVerdieping() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        assertEquals(1, lift.getHuidigeVerdieping());
    }

    // -------------------------------------------------
    // Wachtrij tests
    // -------------------------------------------------

    // ik initialiseer wachtrijen voor meerdere verdiepingen; ik verwacht dat alle wachtrijen leeg zijn
    @Test
    void testInitWachtrijen() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        lift.initWachtrijen(5);

        assertEquals(0, lift.aantalWachtend(1));

        assertEquals(0, lift.aantalWachtend(5));
    }

    // ik roep een gast naar een verdieping; ik verwacht dat deze gast in de wachtrij komt
    @Test
    void testRoepLift() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        lift.initWachtrijen(5);

        Gast gast = new Gast(1, 2);

        lift.roep(gast, 3);

        assertEquals(1, lift.aantalWachtend(3));
    }

    // ik roep dezelfde gast meerdere keren op dezelfde verdieping; ik verwacht dat de gast maar één keer wordt toegevoegd
    @Test
    void testGeenDubbeleOproep() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        lift.initWachtrijen(5);

        Gast gast = new Gast(1, 2);

        lift.roep(gast, 2);

        lift.roep(gast, 2);

        assertEquals(1, lift.aantalWachtend(2));
    }

    // ik roep een gast naar een niet-bestaande verdieping; ik verwacht dat de wachtrij automatisch wordt aangemaakt
    @Test
    void testOnbekendeVerdieping() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        Gast gast = new Gast(1, 2);

        lift.roep(gast, 99);

        assertEquals(1, lift.aantalWachtend(99));
    }

    // -------------------------------------------------
    // Tik (simulatie van lift beweging)
    // -------------------------------------------------

    // ik voer een tik uit zonder wachtende gasten; ik verwacht dat er geen crash optreedt
    @Test
    void testTikGeenWachtenden() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        assertDoesNotThrow(() -> lift.tik());
    }

    // ik roep een gast op een hogere verdieping en laat de lift tikken; ik verwacht dat de lift omhoog beweegt
    @Test
    void testLiftBeweegtOmhoog() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        lift.initWachtrijen(10);

        Gast gast = new Gast(1, 1);

        lift.roep(gast, 4);

        lift.tik();

        assertEquals(2, lift.getHuidigeVerdieping());
    }

    // ik laat de lift meerdere ticks uitvoeren; ik verwacht dat de lift meerdere verdiepingen stijgt
    @Test
    void testLiftBeweegtMeerdereVerdiepingen() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        lift.initWachtrijen(10);

        Gast gast = new Gast(1, 1);

        lift.roep(gast, 5);

        lift.tik();
        lift.tik();
        lift.tik();

        assertEquals(4, lift.getHuidigeVerdieping());
    }

    // -------------------------------------------------
    // Passagiers in de lift
    // -------------------------------------------------

    // ik controleer de passagierslijst bij start; ik verwacht dat deze leeg is
    @Test
    void testPassagiersLeeg() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        assertEquals(0, lift.getPassagiers().size());
    }

    // ik laat een gast instappen via een liftoproep; ik verwacht dat de gast in de lift komt en in de passagierslijst staat
    @Test
    void testGastStaptIn() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.posX = 1;

        lift.initWachtrijen(5);

        Gast gast = new Gast(1, 1);

        Vakje vakje = hotel.layout.krijgVakje(2, 1);

        gast.huidigVakje = vakje;

        lift.roep(gast, 1);

        lift.tik();

        assertTrue(gast.inLift);

        assertEquals(1, lift.getPassagiers().size());
    }

    // ik laat een gast wachten op de lift; ik verwacht dat de wachtstatus wordt gereset na instappen
    @Test
    void testWachtFlagReset() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.posX = 1;

        lift.initWachtrijen(5);

        Gast gast = new Gast(1, 1);

        gast.wachtOpLift = true;

        lift.roep(gast, 1);

        lift.tik();

        assertFalse(gast.wachtOpLift);
    }

    // -------------------------------------------------
    // Uitstappen
    // -------------------------------------------------

    // ik laat een gast in de lift naar zijn bestemming gaan; ik verwacht dat hij uitstapt en uit de liftstatus gaat
    @Test
    void testUitstappen() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.posX = 1;

        lift.initWachtrijen(10);

        Gast gast = new Gast(1, 1);

        gast.gewensteVerdieping = 2;

        gast.inLift = true;

        lift.getPassagiers().add(gast);

        lift.tik();

        assertFalse(gast.inLift);

        assertTrue(gast.moetUitstappen);
    }

    // ik controleer of de liftstatus van een gast wordt gereset na gebruik; ik verwacht dat gebruiktLift false wordt
    @Test
    void testGebruiktLiftReset() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.initWachtrijen(10);

        Gast gast = new Gast(1, 1);

        gast.gewensteVerdieping = 2;

        gast.gebruiktLift = true;

        gast.inLift = true;

        lift.getPassagiers().add(gast);

        lift.tik();

        assertFalse(gast.gebruiktLift);
    }

    // -------------------------------------------------
    // Positie updates
    // -------------------------------------------------

    // ik laat een gast in de lift bewegen; ik verwacht dat zijn positie wordt bijgewerkt naar de liftpositie
    @Test
    void testPassagierPositieUpdate() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.posX = 1;

        lift.initWachtrijen(10);

        Gast gast = new Gast(1, 1);

        gast.gewensteVerdieping = 5;

        gast.inLift = true;

        lift.getPassagiers().add(gast);

        lift.tik();

        assertNotNull(gast.huidigVakje);

        assertEquals(lift.posX, gast.huidigVakje.x);
    }

    // -------------------------------------------------
    // Branch coverage tests
    // -------------------------------------------------

    // ik roep een algemene persoon op de lift; ik verwacht dat dit geen crash veroorzaakt
    @Test
    void testNormalePersoon() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.initWachtrijen(5);

        Persoon p = new Persoon() {};

        lift.roep(p, 1);

        assertDoesNotThrow(() -> lift.tik());
    }

    // ik vraag wachtrij op van een niet-bestaande verdieping; ik verwacht dat dit 0 teruggeeft
    @Test
    void testAantalWachtendenNull() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        assertEquals(0, lift.aantalWachtend(999));
    }

    // ik laat de lift draaien op een ongeldige positie; ik verwacht dat dit geen crash geeft
    @Test
    void testLiftZonderVakje() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.posX = 999;

        lift.initWachtrijen(5);

        Gast gast = new Gast(1, 1);

        gast.inLift = true;

        lift.getPassagiers().add(gast);

        assertDoesNotThrow(() -> lift.tik());
    }

    // ik controleer een lege passagierslijst; ik verwacht dat deze leeg is
    @Test
    void testLegePassagierslijst() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        assertTrue(lift.getPassagiers().isEmpty());
    }
}