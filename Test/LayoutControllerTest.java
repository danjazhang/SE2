import Controller.LayoutController;
import Model.Hotel;
import Model.HotelManager;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import Model.ruimte.Lobby;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LayoutControllerTest {

    // laadGeldigBestand: id groter dan 0 bij succes
    @Test void testLaadGeldigBestand() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        assertTrue(id > 0);
    }

    // laadOngeldigBestand: geeft -1 terug bij fout
    @Test void testLaadOngeldigBestandGeeftMinEen() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("bestaat_niet.json", "test");
        assertEquals(-1, id);
    }

    // getHotel: hotel is niet null na laden
    @Test void testGetHotelNaLaden() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h);
    }

    // hotel heeft ruimtes na laden
    @Test void testHotelHeeftRuimtes() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertFalse(h.ruimtes.isEmpty());
    }

    // hotel heeft een lift na laden
    @Test void testHotelHeeftLift() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.lift);
        assertTrue(h.lift instanceof Lift);
    }

    // hotel heeft een trap na laden
    @Test void testHotelHeeftTrap() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.trap);
        assertTrue(h.trap instanceof Trap);
    }

    // hotel heeft een lobby na laden
    @Test void testHotelHeeftLobby() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.lobby);
        assertTrue(h.lobby instanceof Lobby);
    }

    // hotel heeft kamers na laden
    @Test void testHotelHeeftKamers() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertTrue(h.ruimtes.stream().anyMatch(r -> r instanceof Kamer));
    }

    // maakHandmatigeLayout: hotel heeft juiste afmetingen
    @Test void testMaakHandmatigeLayout() {
        LayoutController lc = new LayoutController();
        int id = lc.maakHandmatigeLayout("test", 5, 5);
        Hotel h = lc.getHotel(id);
        assertNotNull(h);
        assertEquals(5, h.breedte);
        assertEquals(5, h.hoogte);
    }

    // getHotelManager: geeft een HotelManager terug
    @Test void testGetHotelManager() {
        LayoutController lc = new LayoutController();
        assertNotNull(lc.getHotelManager());
        assertTrue(lc.getHotelManager() instanceof HotelManager);
    }

    // setLogger: geen crash
    @Test void testSetLogger() {
        LayoutController lc = new LayoutController();
        assertDoesNotThrow(() -> lc.setLogger(bericht -> {}));
    }

    // meerdere layouts laden: ids zijn verschillend
    @Test void testMeerdereLayoutsLaden() {
        LayoutController lc = new LayoutController();
        int id1 = lc.laadVanBestand("layout.json", "layout1");
        int id2 = lc.laadVanBestand("layout.json", "layout2");
        assertNotEquals(id1, id2);
        assertNotNull(lc.getHotel(id1));
        assertNotNull(lc.getHotel(id2));
    }
}
