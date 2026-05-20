package tests;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.Lift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LiftTest {

    private Hotel hotel;
    private Lift lift;

    @BeforeEach
    void setup() {

        hotel = new Hotel();
        lift = new Lift(hotel);

        hotel.lift = lift;

        lift.initWachtrijen(10);
    }

    @Test
    void testInitVerdieping() {

        assertEquals(1, lift.getHuidigeVerdieping());
    }

    @Test
    void testRoepLift() {

        Gast g = new Gast(1, 3);

        lift.roep(g, 5);

        assertEquals(1, lift.aantalWachtend(5));
    }

    @Test
    void testGeenDubbeleWachtrij() {

        Gast g = new Gast(1, 3);

        lift.roep(g, 5);
        lift.roep(g, 5);

        assertEquals(1, lift.aantalWachtend(5));
    }

    @Test
    void testLiftBeweegtOmhoog() {

        Gast g = new Gast(1, 3);

        lift.roep(g, 5);

        lift.tik();

        assertEquals(2, lift.getHuidigeVerdieping());
    }

    @Test
    void testAantalWachtendLeeg() {

        assertEquals(0, lift.aantalWachtend(3));
    }
}