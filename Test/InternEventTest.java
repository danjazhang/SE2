import Model.events.FilmEindEvent;
import Model.events.FitnessEindEvent;
import Model.events.InternEvent;
import Model.events.RestaurantEindEvent;
import Model.events.SchoonmaakEindEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor InternEvent en alle subklassen
public class InternEventTest {

    // FilmEindEvent: getTijd en getGastId correct
    @Test void testFilmEindEvent() {
        FilmEindEvent e = new FilmEindEvent(42, 7);
        assertEquals(42, e.getTijd());
        assertEquals(7, e.getGastId());
    }

    // FitnessEindEvent: getTijd en getGastId correct
    @Test void testFitnessEindEvent() {
        FitnessEindEvent e = new FitnessEindEvent(10, 3);
        assertEquals(10, e.getTijd());
        assertEquals(3, e.getGastId());
    }

    // RestaurantEindEvent: getTijd en getGastId correct
    @Test void testRestaurantEindEvent() {
        RestaurantEindEvent e = new RestaurantEindEvent(99, 15);
        assertEquals(99, e.getTijd());
        assertEquals(15, e.getGastId());
    }

    // SchoonmaakEindEvent: getTijd en getGastId correct
    @Test void testSchoonmaakEindEvent() {
        SchoonmaakEindEvent e = new SchoonmaakEindEvent(5, 2);
        assertEquals(5, e.getTijd());
        assertEquals(2, e.getGastId());
    }

    // tijdstip 0 is geldig
    @Test void testTijdstip0() {
        FilmEindEvent e = new FilmEindEvent(0, 0);
        assertEquals(0, e.getTijd());
        assertEquals(0, e.getGastId());
    }

    // negatieve gastId is technisch geldig (library gebruikt -1 voor "alle gasten")
    @Test void testNegatieveGastId() {
        FilmEindEvent e = new FilmEindEvent(100, -1);
        assertEquals(-1, e.getGastId());
    }
}
