import Model.ruimte.Fitnessruimte;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Fitnessruimte: ik test fitness-events en het interne eindmoment.
public class FitnessruimteTest {

    // Ik maak een nieuwe Fitnessruimte; ik verwacht dat de gastenlijst leeg is.
    @Test void testConstructor() {
        Fitnessruimte f = new Fitnessruimte();
        assertTrue(f.gasten.isEmpty());
    }

    // Ik stuur een GOTO_FITNESS-event; ik verwacht dat dit geen exception geeft.
    @Test void testGotoFitnessCrashetNiet() {
        assertDoesNotThrow(() -> new Fitnessruimte().onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 1, -1)));
    }

    // Ik laat een gast sporten en stuur later een NONE-tick; ik verwacht een klaar-log.
    @Test void testGastKlaarNaSportduur() {
        boolean[] logged = {false};
        Fitnessruimte f = new Fitnessruimte(bericht -> logged[0] = true);
        f.onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 1, -1));
        f.onEvent(new HotelEvent(21, HotelEventType.NONE, -1, -1));
        assertTrue(logged[0]);
    }

    // Ik stuur een NONE-tick zonder actieve gasten; ik verwacht geen exception.
    @Test void testNoneCrashetNiet() {
        assertDoesNotThrow(() -> new Fitnessruimte().onEvent(new HotelEvent(1, HotelEventType.NONE, -1, -1)));
    }
}
