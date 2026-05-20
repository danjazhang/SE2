package tests;

import Model.layout.Vakje;
import Model.persoon.Gast;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersoonTest {

    @Test
    void testZetDoel() {

        Gast g = new Gast(1, 3);

        Vakje v = new Vakje(1, 1);

        g.zetDoel(v);

        assertEquals(v, g.doelVakje);
    }

    @Test
    void testStartPositie() {

        Gast g = new Gast(1, 3);

        Vakje v = new Vakje(2, 2);

        g.zetStartPositie(v);

        assertEquals(v, g.huidigVakje);
    }

    @Test
    void testWisRoute() {

        Gast g = new Gast(1, 3);

        Vakje v = new Vakje(1, 1);

        g.zetDoel(v);

        g.wisRoute();

        assertNull(g.doelVakje);
    }

    @Test
    void testGeenBewegingZonderDoel() {

        Gast g = new Gast(1, 3);

        g.beweeg();

        assertNull(g.doelVakje);
    }
}