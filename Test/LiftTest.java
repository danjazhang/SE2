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

        // Layout maken
        hotel.layout = new Layout(10, 10);

        // Breedte en hoogte
        hotel.breedte = 10;
        hotel.hoogte = 10;

        return hotel;
    }

    // -------------------------------------------------
    // Constructor tests
    // -------------------------------------------------

    // Test constructor
    @Test
    void testConstructor() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        // Lift moet bestaan
        assertNotNull(lift);
    }

    // Test standaard verdieping
    @Test
    void testStartVerdieping() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        // Lift start op verdieping 1
        assertEquals(
                1,
                lift.getHuidigeVerdieping()
        );
    }

    // -------------------------------------------------
    // Wachtrij tests
    // -------------------------------------------------

    // Test wachtrijen initialiseren
    @Test
    void testInitWachtrijen() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        // Maak wachtrijen
        lift.initWachtrijen(5);

        // Alle wachtrijen moeten leeg zijn
        assertEquals(
                0,
                lift.aantalWachtend(1)
        );

        assertEquals(
                0,
                lift.aantalWachtend(5)
        );
    }

    // Test oproepen lift
    @Test
    void testRoepLift() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        lift.initWachtrijen(5);

        // Gast maken
        Gast gast = new Gast(1, 2);

        // Roep lift
        lift.roep(gast, 3);

        // Er moet 1 wachtende zijn
        assertEquals(
                1,
                lift.aantalWachtend(3)
        );
    }

    // Test dubbele oproep
    @Test
    void testGeenDubbeleOproep() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        lift.initWachtrijen(5);

        Gast gast = new Gast(1, 2);

        // Zelfde gast 2x toevoegen
        lift.roep(gast, 2);

        lift.roep(gast, 2);

        // Nog steeds maar 1
        assertEquals(
                1,
                lift.aantalWachtend(2)
        );
    }

    // Test onbekende verdieping
    @Test
    void testOnbekendeVerdieping() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        Gast gast = new Gast(1, 2);

        // Verdieping bestond nog niet
        lift.roep(gast, 99);

        // Wachtrij moet automatisch aangemaakt zijn
        assertEquals(
                1,
                lift.aantalWachtend(99)
        );
    }

    // -------------------------------------------------
    // Tik tests
    // -------------------------------------------------

    // Test tik zonder wachtenden
    @Test
    void testTikGeenWachtenden() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        // Geen crash verwacht
        assertDoesNotThrow(() -> {

            lift.tik();
        });
    }

    // Test lift beweegt omhoog
    @Test
    void testLiftBeweegtOmhoog() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        lift.initWachtrijen(10);

        Gast gast = new Gast(1, 1);

        // Roep lift op verdieping 4
        lift.roep(gast, 4);

        // Tick uitvoeren
        lift.tik();

        // Lift moet omhoog gegaan zijn
        assertEquals(
                2,
                lift.getHuidigeVerdieping()
        );
    }

    // Test lift beweegt meerdere keren
    @Test
    void testLiftBeweegtMeerdereVerdiepingen() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        lift.initWachtrijen(10);

        Gast gast = new Gast(1, 1);

        lift.roep(gast, 5);

        // Meerdere ticks
        lift.tik();
        lift.tik();
        lift.tik();

        // Lift moet op 4 staan
        assertEquals(
                4,
                lift.getHuidigeVerdieping()
        );
    }

    // -------------------------------------------------
    // Passagier tests
    // -------------------------------------------------

    // Test passagierslijst leeg
    @Test
    void testPassagiersLeeg() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        // Geen passagiers
        assertEquals(
                0,
                lift.getPassagiers().size()
        );
    }

    // Test instappen
    @Test
    void testGastStaptIn() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.posX = 1;

        lift.initWachtrijen(5);

        // Gast maken
        Gast gast = new Gast(1, 1);

        // Zet gast op vakje
        Vakje vakje =
                hotel.layout.krijgVakje(2, 1);

        gast.huidigVakje = vakje;

        // Roep lift op huidige verdieping
        lift.roep(gast, 1);

        // Tick uitvoeren
        lift.tik();

        // Gast moet in lift zitten
        assertTrue(gast.inLift);

        // Passagierslijst moet 1 bevatten
        assertEquals(
                1,
                lift.getPassagiers().size()
        );
    }

    // Test wachtflag reset
    @Test
    void testWachtFlagReset() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.posX = 1;

        lift.initWachtrijen(5);

        Gast gast = new Gast(1, 1);

        // Gast wacht op lift
        gast.wachtOpLift = true;

        lift.roep(gast, 1);

        // Tick uitvoeren
        lift.tik();

        // Wachtflag moet false zijn
        assertFalse(gast.wachtOpLift);
    }

    // -------------------------------------------------
    // Uitstappen tests
    // -------------------------------------------------

    // Test uitstappen op bestemming
    @Test
    void testUitstappen() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.posX = 1;

        lift.initWachtrijen(10);

        // Gast maken
        Gast gast = new Gast(1, 1);

        // Gast wil naar verdieping 2
        gast.gewensteVerdieping = 2;

        // Gast zit al in lift
        gast.inLift = true;

        // Voeg handmatig toe
        lift.getPassagiers().add(gast);

        // Tick uitvoeren
        lift.tik();

        // Gast moet uit lift zijn
        assertFalse(gast.inLift);

        // Gast moet uitgestapt zijn
        assertTrue(gast.moetUitstappen);
    }

    // Test gebruiktLift reset
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

        // Voeg toe aan passagiers
        lift.getPassagiers().add(gast);

        // Tick uitvoeren
        lift.tik();

        // Flag moet false zijn
        assertFalse(gast.gebruiktLift);
    }

    // -------------------------------------------------
    // Positie tests
    // -------------------------------------------------

    // Test positie update passagier
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

        // Voeg toe aan passagiers
        lift.getPassagiers().add(gast);

        // Tick uitvoeren
        lift.tik();

        // Positie moet bestaan
        assertNotNull(gast.huidigVakje);

        // X positie moet gelijk zijn aan lift
        assertEquals(
                lift.posX,
                gast.huidigVakje.x
        );
    }

    // -------------------------------------------------
    // Branch coverage tests
    // -------------------------------------------------

    // Test niet-gast persoon
    @Test
    void testNormalePersoon() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        lift.initWachtrijen(5);

        // Anonieme persoon
        Persoon p = new Persoon() {
        };

        // Oproepen
        lift.roep(p, 1);

        // Tick uitvoeren
        assertDoesNotThrow(() -> {

            lift.tik();
        });
    }

    // Test aantal wachtenden null
    @Test
    void testAantalWachtendenNull() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        // Geen wachtrij op verdieping 999
        assertEquals(
                0,
                lift.aantalWachtend(999)
        );
    }

    // Test lift zonder layout vakje
    @Test
    void testLiftZonderVakje() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        hotel.lift = lift;

        // Ongeldige positie
        lift.posX = 999;

        lift.initWachtrijen(5);

        Gast gast = new Gast(1, 1);

        gast.inLift = true;

        lift.getPassagiers().add(gast);

        // Geen crash verwacht
        assertDoesNotThrow(() -> {

            lift.tik();
        });
    }

    // Test lege passagierslijst
    @Test
    void testLegePassagierslijst() {

        Hotel hotel = maakHotel();

        Lift lift = new Lift(hotel);

        // Geen passagiers
        assertTrue(
                lift.getPassagiers().isEmpty()
        );
    }
}