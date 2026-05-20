import Model.events.FilmEindEvent;
import Model.events.FitnessEindEvent;
import Model.events.InternEvent;
import Model.events.RestaurantEindEvent;
import Model.events.SchoonmaakEindEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InternEventTest {

    // FilmEindEvent: tijd en gastId worden correct opgeslagen
    @Test void testFilmEindEventTijdEnGastId() {
        FilmEindEvent e = new FilmEindEvent(50, 3);
        assertEquals(50, e.getTijd());
        assertEquals(3, e.getGastId());
    }

    // FitnessEindEvent: tijd en gastId worden correct opgeslagen
    @Test void testFitnessEindEventTijdEnGastId() {
        FitnessEindEvent e = new FitnessEindEvent(80, 7);
        assertEquals(80, e.getTijd());
        assertEquals(7, e.getGastId());
    }

    // RestaurantEindEvent: tijd en gastId worden correct opgeslagen
    @Test void testRestaurantEindEventTijdEnGastId() {
        RestaurantEindEvent e = new RestaurantEindEvent(100, 2);
        assertEquals(100, e.getTijd());
        assertEquals(2, e.getGastId());
    }

    // SchoonmaakEindEvent: tijd en gastId worden correct opgeslagen
    @Test void testSchoonmaakEindEventTijdEnGastId() {
        SchoonmaakEindEvent e = new SchoonmaakEindEvent(30, 5);
        assertEquals(30, e.getTijd());
        assertEquals(5, e.getGastId());
    }

    // alle event klassen erven van InternEvent
    @Test void testFilmEindEventErftVanInternEvent() { assertTrue(new FilmEindEvent(1, 1) instanceof InternEvent); }
    @Test void testFitnessEindEventErftVanInternEvent() { assertTrue(new FitnessEindEvent(1, 1) instanceof InternEvent); }
    @Test void testRestaurantEindEventErftVanInternEvent() { assertTrue(new RestaurantEindEvent(1, 1) instanceof InternEvent); }
    @Test void testSchoonmaakEindEventErftVanInternEvent() { assertTrue(new SchoonmaakEindEvent(1, 1) instanceof InternEvent); }
}
