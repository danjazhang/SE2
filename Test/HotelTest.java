import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;
import Controller.*;

class HotelTest {

    private Hotel laadHotel() {
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().laadVanBestand("layout.json", "layout.json");
        return hc.getLayoutController().getHotel(id);
    }

    @Test
    void testLaadLayoutBestand() {
        Hotel hotel = laadHotel();
        assertNotNull(hotel);
        assertEquals(30, hotel.ruimtes.size());
        assertEquals(6, hotel.breedte);
        assertEquals(8, hotel.hoogte);
    }

    @Test
    void testKrijgRuimteOp() {
        Hotel hotel = laadHotel();
        assertNotNull(hotel.krijgRuimteOp(1, 1));
        assertTrue(hotel.krijgRuimteOp(1, 1) instanceof Kamer);
    }

    @Test
    void testKrijgRuimteOpBuitenGrid() {
        Hotel hotel = laadHotel();
        assertNull(hotel.krijgRuimteOp(0, 0));
        assertNull(hotel.krijgRuimteOp(99, 99));
    }

    @Test
    void testVoegPersoonToe() {
        Hotel hotel = new Hotel();
        Gast g = new Gast(1, 3);
        hotel.voegPersoonToe(g);
        assertEquals(1, hotel.personen.size());
    }

    @Test
    void testVoegListenerToeEnNotify() {
        Hotel hotel = new Hotel();
        boolean[] genotificeerd = {false};
        hotel.voegListenerToe(() -> genotificeerd[0] = true);
        hotel.notifyListeners();
        assertTrue(genotificeerd[0]);
    }

    @Test
    void testConstructorLegeHotel() {
        Hotel hotel = new Hotel();
        assertNotNull(hotel.ruimtes);
        assertNotNull(hotel.personen);
        assertTrue(hotel.ruimtes.isEmpty());
        assertTrue(hotel.personen.isEmpty());
    }
}
