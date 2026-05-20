package tests;

import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Vakje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathfinderTest {

    private Hotel hotel;
    private Pathfinder pathfinder;

    @BeforeEach
    void setup() {

        hotel = new Hotel();

        pathfinder = new Pathfinder(hotel);
    }

    @Test
    void testNullHuidig() {

        Vakje doel = new Vakje(5, 1);

        assertNull(pathfinder.volgendeStap(null, doel));
    }

    @Test
    void testNullDoel() {

        Vakje huidig = new Vakje(1, 1);

        assertNull(pathfinder.volgendeStap(huidig, null));
    }

    @Test
    void testHorizontaleBewegingRechts() {

        Vakje huidig = new Vakje(1, 1);
        Vakje doel = new Vakje(5, 1);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(2, stap.x);
    }

    @Test
    void testHorizontaleBewegingLinks() {

        Vakje huidig = new Vakje(5, 1);
        Vakje doel = new Vakje(1, 1);

        Vakje stap = pathfinder.volgendeStap(huidig, doel);

        assertEquals(4, stap.x);
    }
}