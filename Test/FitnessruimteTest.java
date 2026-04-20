import Model.ruimte.Fitnessruimte;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FitnessruimteTest {

    @Test void testConstructor() {
        Fitnessruimte f = new Fitnessruimte();
        assertTrue(f.gasten.isEmpty());
    }

    @Test void testGotoFitnessCrashetNiet() {
        assertDoesNotThrow(() -> new Fitnessruimte().onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 1, -1)));
    }

    @Test void testGastKlaarNaSportduur() {
        boolean[] logged = {false};
        Fitnessruimte f = new Fitnessruimte(bericht -> logged[0] = true);
        f.onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 1, -1));
        f.onEvent(new HotelEvent(21, HotelEventType.NONE, -1, -1));
        assertTrue(logged[0]);
    }

    @Test void testNoneCrashetNiet() {
        assertDoesNotThrow(() -> new Fitnessruimte().onEvent(new HotelEvent(1, HotelEventType.NONE, -1, -1)));
    }
}
