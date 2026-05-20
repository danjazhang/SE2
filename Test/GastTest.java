package tests;

import Model.persoon.Gast;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GastTest {

    @Test
    void testGastConstructor() {

        Gast g = new Gast(1, 5);

        assertEquals(1, g.gastId);
        assertEquals(5, g.gewensteSterren);
    }

    @Test
    void testLiftFlags() {

        Gast g = new Gast(1, 5);

        assertFalse(g.inLift);
        assertFalse(g.gebruiktLift);
        assertFalse(g.wachtOpLift);
    }

    @Test
    void testGewensteVerdiepingDefault() {

        Gast g = new Gast(1, 5);

        assertEquals(1, g.gewensteVerdieping);
    }
}