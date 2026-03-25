import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

class HotelTest {
    @Test
    void testLaadLayoutBestand(){
        //maak nieuw hotel
        Hotel hotel = new Hotel();
        //laad hotel layout
        hotel.laadLayoutBestand("layout.json");
        //aantal ruimtes moet gelijk zijn aan 29
        assertEquals(29,hotel.ruimtes.size());
        //hotel is 6 breed en 8 hoog
        assertEquals(6,hotel.breedte);
        assertEquals(8,hotel.hoogte);
    }

    //controleer of er op 1,1 een kamer is
    @Test
    void testKrijgRuimteOp(){
        Hotel hotel = new Hotel();
        hotel.laadLayoutBestand("layout.json");
        assertNotNull(hotel.krijgRuimteOp(1,1));
        assertTrue(hotel.krijgRuimteOp(1,1) instanceof Kamer);
    }
}
