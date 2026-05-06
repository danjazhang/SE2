import Model.Hotel;
import Model.ruimte.Kamer;
import Model.persoon.Gast;
import Model.ModelListener;
import Controller.LayoutController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Hotel: ik test hoteldata, personenlijst en model-listeners.
public class HotelTest {

    // Ik maak een nieuw Hotel; ik verwacht lege ruimte- en personenlijsten.
    @Test void testConstructor() {
        Hotel h = new Hotel();
        assertTrue(h.ruimtes.isEmpty());
        assertTrue(h.personen.isEmpty());
    }

    // Ik voeg een Gast toe; ik verwacht dat de personenlijst groter wordt.
    @Test void testVoegPersoonToe() {
        Hotel h = new Hotel();
        Gast g = new Gast(1, 2);
        h.voegPersoonToe(g);
        assertEquals(1, h.personen.size());
    }

    // Ik voeg een ModelListener toe en notify; ik verwacht dat de listener wordt aangeroepen.
    @Test void testNotifyListeners() {
        Hotel h = new Hotel();
        boolean[] called = {false};
        h.voegListenerToe(() -> called[0] = true);
        h.notifyListeners();
        assertTrue(called[0]);
    }

    // Ik laad een layout en vraag een ruimte op; ik verwacht dat daar een Kamer ligt.
    @Test void testKrijgRuimteOpMetLayout() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.krijgRuimteOp(2, 1));
        assertTrue(h.krijgRuimteOp(2, 1) instanceof Kamer);
    }
}
