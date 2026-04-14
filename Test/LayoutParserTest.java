import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;
import Controller.*;

public class LayoutParserTest {

    private Hotel laadHotel() {
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().laadVanBestand("layout.json", "layout.json");
        return hc.getLayoutController().getHotel(id);
    }

    @Test
    void testLaadVultBreedteEnHoogte() {
        Hotel hotel = laadHotel();
        assertEquals(6, hotel.breedte);
        assertEquals(8, hotel.hoogte);
    }

    @Test
    void testLaadVultRuimtes() {
        Hotel hotel = laadHotel();
        assertEquals(30, hotel.ruimtes.size());
    }

    @Test
    void testLaadMaaktLayoutAan() {
        Hotel hotel = laadHotel();
        assertNotNull(hotel.layout);
    }

    @Test
    void testLaadMetOngeldigPadGeeftMinusEen() {
        HotelController hc = new HotelController();
        int id = hc.getLayoutController().laadVanBestand("bestaat_niet.json", "bestaat_niet.json");
        assertEquals(-1, id);
    }

    @Test
    void testLaadMaaktJuisteRuimteTypes() {
        Hotel hotel = laadHotel();
        assertNotNull(hotel.krijgRuimteOp(1, 1));
        assertTrue(hotel.krijgRuimteOp(1, 1) instanceof Kamer);
    }

    @Test
    void testLaadMaaktBioscoopAan() {
        Hotel hotel = laadHotel();
        // bioscoop staat op positie 1,3 in layout.json
        assertNotNull(hotel.krijgRuimteOp(1, 3));
        assertTrue(hotel.krijgRuimteOp(1, 3) instanceof Bioscoop);
    }

    @Test
    void testLaadMaaktRestaurantAan() {
        Hotel hotel = laadHotel();
        // restaurant staat op positie 5,4 in layout.json
        assertNotNull(hotel.krijgRuimteOp(5, 4));
        assertTrue(hotel.krijgRuimteOp(5, 4) instanceof Restaurant);
    }

    @Test
    void testLaadMaaktFitnessAan() {
        Hotel hotel = laadHotel();
        // fitness staat op positie 4,2 in layout.json
        assertNotNull(hotel.krijgRuimteOp(4, 2));
        assertTrue(hotel.krijgRuimteOp(4, 2) instanceof Fitnessruimte);
    }
}
