import Controller.LayoutController;
import Model.Hotel;
import Model.HotelManager;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import Model.ruimte.Lobby;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor LayoutController: ik test het laden en maken van hotels/layouts.
public class LayoutControllerTest {

    // Ik laad een geldig layoutbestand; ik verwacht een positief id.
    @Test void testLaadGeldigBestand() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        assertTrue(id > 0);
    }

    // Ik laad een ongeldig bestand; ik verwacht -1 als foutwaarde.
    @Test void testLaadOngeldigBestandGeeftMinEen() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("bestaat_niet.json", "test");
        assertEquals(-1, id);
    }

    // Ik laad een layout en vraag het Hotel op; ik verwacht dat het Hotel bestaat.
    @Test void testGetHotelNaLaden() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h);
    }

    // Ik laad een layout; ik verwacht dat het Hotel ruimtes bevat.
    @Test void testHotelHeeftRuimtes() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertFalse(h.ruimtes.isEmpty());
    }

    // Ik laad een layout; ik verwacht dat er automatisch een Lift is toegevoegd.
    @Test void testHotelHeeftLift() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.lift);
        assertTrue(h.lift instanceof Lift);
    }

    // Ik laad een layout; ik verwacht dat er automatisch een Trap is toegevoegd.
    @Test void testHotelHeeftTrap() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.trap);
        assertTrue(h.trap instanceof Trap);
    }

    // Ik laad een layout; ik verwacht dat er automatisch een Lobby is toegevoegd.
    @Test void testHotelHeeftLobby() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.lobby);
        assertTrue(h.lobby instanceof Lobby);
    }

    // Ik laad een layout; ik verwacht dat er minstens een Kamer is aangemaakt.
    @Test void testHotelHeeftKamers() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertTrue(h.ruimtes.stream().anyMatch(r -> r instanceof Kamer));
    }

    // Ik maak handmatig een layout; ik verwacht een Hotel met dezelfde afmetingen.
    @Test void testMaakHandmatigeLayout() {
        LayoutController lc = new LayoutController();
        int id = lc.maakHandmatigeLayout("test", 5, 5);
        Hotel h = lc.getHotel(id);
        assertNotNull(h);
        assertEquals(5, h.breedte);
        assertEquals(5, h.hoogte);
    }

    // Ik vraag de HotelManager op; ik verwacht dat die bestaat.
    @Test void testGetHotelManager() {
        LayoutController lc = new LayoutController();
        assertNotNull(lc.getHotelManager());
        assertTrue(lc.getHotelManager() instanceof HotelManager);
    }

    // Ik zet een logger; ik verwacht geen exception.
    @Test void testSetLogger() {
        LayoutController lc = new LayoutController();
        assertDoesNotThrow(() -> lc.setLogger(bericht -> {}));
    }

    // Ik laad twee layouts; ik verwacht verschillende id's en twee Hotels.
    @Test void testMeerdereLayoutsLaden() {
        LayoutController lc = new LayoutController();
        int id1 = lc.laadVanBestand("layout.json", "layout1");
        int id2 = lc.laadVanBestand("layout.json", "layout2");
        assertNotEquals(id1, id2);
        assertNotNull(lc.getHotel(id1));
        assertNotNull(lc.getHotel(id2));
    }
}
