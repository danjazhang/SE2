import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;
import Controller.*;

// Test de LayoutController en HotelManager als vervanging voor niet-bestaande Scenario klasse
public class ScenarioTest {

    @Test
    void testLayoutControllerMaakHandmatigeLayout() {
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().maakHandmatigeLayout("test", 5, 5);
        assertNotEquals(-1, id);
        assertNotNull(hc.getLayoutController().getHotel(id));
    }

    @Test
    void testHandmatigeLayoutHeeftJuisteAfmetingen() {
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().maakHandmatigeLayout("test", 4, 6);
        Hotel hotel = hc.getLayoutController().getHotel(id);
        assertEquals(4, hotel.breedte);
        assertEquals(6, hotel.hoogte);
    }

    @Test
    void testMeerdereLayoutsLaden() {
        HotelController hc = new HotelController();
        int id1 = hc.getLayoutController().laadVanBestand("layout.json", "layout1");
        int id2 = hc.getLayoutController().laadVanBestand("layout.json", "layout2");
        assertNotEquals(id1, id2);
        assertNotNull(hc.getLayoutController().getHotel(id1));
        assertNotNull(hc.getLayoutController().getHotel(id2));
    }

    @Test
    void testHotelManagerGetHotelManager() {
        HotelController hc = new HotelController();
        assertNotNull(hc.getLayoutController().getHotelManager());
    }

    @Test
    void testHandmatigeLayoutHeeftLegeLijstRuimtes() {
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().maakHandmatigeLayout("leeg", 3, 3);
        Hotel hotel = hc.getLayoutController().getHotel(id);
        assertTrue(hotel.ruimtes.isEmpty());
    }
}
