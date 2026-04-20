import Model.Hotel;
import Model.ruimte.Kamer;
import Model.persoon.Gast;
import Model.ModelListener;
import Controller.LayoutController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotelTest {

    @Test void testConstructor() {
        Hotel h = new Hotel();
        assertTrue(h.ruimtes.isEmpty());
        assertTrue(h.personen.isEmpty());
    }

    @Test void testVoegPersoonToe() {
        Hotel h = new Hotel();
        Gast g = new Gast(1, 2);
        h.voegPersoonToe(g);
        assertEquals(1, h.personen.size());
    }

    @Test void testNotifyListeners() {
        Hotel h = new Hotel();
        boolean[] called = {false};
        h.voegListenerToe(() -> called[0] = true);
        h.notifyListeners();
        assertTrue(called[0]);
    }

    @Test void testKrijgRuimteOpMetLayout() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.krijgRuimteOp(2, 1));
        assertTrue(h.krijgRuimteOp(2, 1) instanceof Kamer);
    }
}
