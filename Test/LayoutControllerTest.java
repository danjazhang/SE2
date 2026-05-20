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

    // Ik laad een geldig layoutbestand; ik verwacht dat ik een bruikbaar id terugkrijg.
    @Test void testLaadGeldigBestand() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        assertTrue(id > 0);
    }

    // Ik laad een ongeldig bestand; ik verwacht dat de controller -1 teruggeeft.
    @Test void testLaadOngeldigBestandGeeftMinEen() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("bestaat_niet.json", "test");
        assertEquals(-1, id);
    }

    // Ik laad eerst een layout en vraag daarna het hotel op; ik verwacht dat dat hotel bestaat.
    @Test void testGetHotelNaLaden() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h);
    }

    // Ik laad een hotel uit bestand; ik verwacht dat het daarna ruimtes bevat.
    @Test void testHotelHeeftRuimtes() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertFalse(h.ruimtes.isEmpty());
    }

    // Ik laad een hotel uit bestand; ik verwacht dat er daarna een lift aanwezig is.
    @Test void testHotelHeeftLift() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.lift);
        assertTrue(h.lift instanceof Lift);
    }

    // Ik laad een hotel uit bestand; ik verwacht dat er daarna een trap aanwezig is.
    @Test void testHotelHeeftTrap() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.trap);
        assertTrue(h.trap instanceof Trap);
    }

    // Ik laad een hotel uit bestand; ik verwacht dat er daarna een lobby aanwezig is.
    @Test void testHotelHeeftLobby() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertNotNull(h.lobby);
        assertTrue(h.lobby instanceof Lobby);
    }

    // Ik laad een hotel uit bestand; ik verwacht dat er daarna minstens één kamer aanwezig is.
    @Test void testHotelHeeftKamers() {
        LayoutController lc = new LayoutController();
        int id = lc.laadVanBestand("layout.json", "layout.json");
        Hotel h = lc.getHotel(id);
        assertTrue(h.ruimtes.stream().anyMatch(r -> r instanceof Kamer));
    }

    // Ik maak handmatig een layout aan; ik verwacht dat het hotel de opgegeven afmetingen krijgt.
    @Test void testMaakHandmatigeLayout() {
        LayoutController lc = new LayoutController();
        int id = lc.maakHandmatigeLayout("test", 5, 5);
        Hotel h = lc.getHotel(id);
        assertNotNull(h);
        assertEquals(5, h.breedte);
        assertEquals(5, h.hoogte);
    }

    // Ik vraag de hotelmanager op; ik verwacht dat die bestaat en van het juiste type is.
    @Test void testGetHotelManager() {
        LayoutController lc = new LayoutController();
        assertNotNull(lc.getHotelManager());
        assertTrue(lc.getHotelManager() instanceof HotelManager);
    }

    // Ik stel een logger in op de layoutcontroller; ik verwacht dat dit geen crash geeft.
    @Test void testSetLogger() {
        LayoutController lc = new LayoutController();
        assertDoesNotThrow(() -> lc.setLogger(bericht -> {}));
    }

    // Ik laad twee layouts na elkaar; ik verwacht dat beide een verschillend id krijgen.
    @Test void testMeerdereLayoutsLaden() {
        LayoutController lc = new LayoutController();
        int id1 = lc.laadVanBestand("layout.json", "layout1");
        int id2 = lc.laadVanBestand("layout.json", "layout2");
        assertNotEquals(id1, id2);
        assertNotNull(lc.getHotel(id1));
        assertNotNull(lc.getHotel(id2));
    }
}
