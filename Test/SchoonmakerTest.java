import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Schoonmaker: ik test schoonmaken, noodsituaties en overerving van Persoon.
public class SchoonmakerTest {

    // Ik maak een Schoonmaker; ik verwacht dat hij niet bezig is en geen Kamer heeft.
    @Test void testConstructor() {
        Schoonmaker s = new Schoonmaker();
        assertFalse(s.bezig);
        assertNull(s.kamer);
    }

    // Ik maak een Schoonmaker; ik verwacht dat hij Persoon-velden zoals huidigVakje heeft.
    @Test void testErftVanPersoon() {
        Schoonmaker s = new Schoonmaker();
        assertNull(s.huidigVakje);
        assertNull(s.doelVakje);
    }

    // Ik laat een Schoonmaker een vieze Kamer schoonmaken; ik verwacht een schone Kamer en vrije Schoonmaker.
    @Test void testMaakKamerSchoon() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        k.schoon = false;
        s.maakKamerSchoon(k);
        assertTrue(k.isSchoon());
        assertFalse(s.bezig);
        assertNull(s.kamer);
    }

    // Ik roep handelEmergency aan; ik verwacht dat de Kamer schoon wordt.
    @Test void testHandelEmergency() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        k.schoon = false;
        s.handelEmergency(k);
        assertTrue(k.isSchoon());
    }

    // Ik stuur CLEANING_EMERGENCY; ik verwacht dat de Schoonmaker bezig wordt.
    @Test void testOnEventCleaningEmergency() {
        Schoonmaker s = new Schoonmaker();
        HotelEvent evt = new HotelEvent(1, HotelEventType.CLEANING_EMERGENCY, 1, -1);
        s.onEvent(evt);
        assertTrue(s.bezig);
    }

    // Ik stuur een ander event; ik verwacht dat de Schoonmaker niet bezig wordt.
    @Test void testOnEventAndereEventNegeren() {
        Schoonmaker s = new Schoonmaker();
        HotelEvent evt = new HotelEvent(1, HotelEventType.CHECK_IN, 1, -1);
        s.onEvent(evt);
        assertFalse(s.bezig);
    }
}
