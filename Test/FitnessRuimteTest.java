import Model.ruimte.Fitnessruimte;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FitnessRuimteTest {

    // Ik maak een nieuwe fitnessruimte aan; ik verwacht dat de gastenlijst leeg begint.
    @Test void testConstructor() {
        Fitnessruimte f = new Fitnessruimte();
        assertTrue(f.gasten.isEmpty());
    }

    // Ik stuur een GOTO_FITNESS event; ik verwacht dat dit zonder crash verwerkt wordt.
    @Test void testGotoFitnessCrashetNiet() {
        assertDoesNotThrow(() -> new Fitnessruimte().onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 1, -1)));
    }

    // Ik stuur eerst een gast naar de fitnessruimte en daarna een NONE event na de sportduur;
    // ik verwacht dat de ruimte aangeeft dat de gast klaar is.
    @Test void testGastKlaarNaSportduur() {
        boolean[] logged = {false};
        Fitnessruimte f = new Fitnessruimte(bericht -> logged[0] = true);
        f.onEvent(new HotelEvent(1, HotelEventType.GOTO_FITNESS, 1, -1));
        f.onEvent(new HotelEvent(21, HotelEventType.NONE, -1, -1));
        assertTrue(logged[0]);
    }

    // Ik stuur een NONE event zonder extra voorbereiding; ik verwacht dat dit geen crash geeft.
    @Test void testNoneCrashetNiet() {
        assertDoesNotThrow(() -> new Fitnessruimte().onEvent(new HotelEvent(1, HotelEventType.NONE, -1, -1)));
    }
}
