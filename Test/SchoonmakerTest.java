import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SchoonmakerTest {

    @Test void testConstructor() {
        Schoonmaker s = new Schoonmaker();
        assertFalse(s.bezig);
        assertNull(s.kamer);
    }

    @Test void testErftVanPersoon() {
        Schoonmaker s = new Schoonmaker();
        assertNull(s.huidigVakje);
        assertNull(s.doelVakje);
    }

    @Test void testOnEventCleaningEmergency() {
        Schoonmaker s = new Schoonmaker();
        HotelEvent evt = new HotelEvent(1, HotelEventType.CLEANING_EMERGENCY, 1, -1);
        s.onEvent(evt);
        assertTrue(s.bezig);
    }

    @Test void testOnEventAndereEventNegeren() {
        Schoonmaker s = new Schoonmaker();
        HotelEvent evt = new HotelEvent(1, HotelEventType.CHECK_IN, 1, -1);
        s.onEvent(evt);
        assertFalse(s.bezig);
    }
}
