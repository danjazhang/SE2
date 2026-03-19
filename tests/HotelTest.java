import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HotelTest {
    @Test
    void testLaadLayoutBestand(){
        Hotel hotel = new Hotel();
        hotel.laadLayoutBestand("layout.json");
        assertEquals(29,hotel.ruimtes.size());
        assertEquals(6,hotel.breedte);
        assertEquals(8,hotel.hoogte);
    }
}
