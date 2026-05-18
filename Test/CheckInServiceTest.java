import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.Kamer;
import Model.service.CheckInService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor CheckInService: ik test de volledige check-in flow,
// dus gastaanmaak via de factory, kamerkoppeling en het resultaatobject.
public class CheckInServiceTest {

    // Ik doe een check-in in een hotel met een vrije kamer; ik verwacht dat de service
    // via de factory een gast laat aanmaken, een kamer koppelt en een eerste route klaarzet.
    @Test void testCheckInGastMetVrijeKamer() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 6);
        hotel.breedte = 6;
        hotel.hoogte = 6;

        Kamer kamer = new Kamer();
        kamer.posX = 3;
        kamer.posY = 6;
        kamer.breedte = 1;
        kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        hotel.pathfinder = new Pathfinder(hotel);

        CheckInService service = new CheckInService(hotel, 2);
        CheckInService.CheckInResult result = service.checkInGast(7);

        assertNotNull(result.getGast());
        assertEquals(kamer, result.getKamer());
        assertEquals(1, hotel.personen.size());
        assertTrue(kamer.isBezet());
        assertEquals(kamer, result.getGast().kamer);
        assertNotNull(result.getGast().doelVakje);
    }

    // Ik doe een check-in in een hotel zonder vrije kamer; ik verwacht dat de service
    // nog steeds een gast toevoegt, maar dat er geen kamer toegewezen wordt.
    @Test void testCheckInGastZonderVrijeKamer() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 6);
        hotel.breedte = 6;
        hotel.hoogte = 6;
        hotel.pathfinder = new Pathfinder(hotel);

        CheckInService service = new CheckInService(hotel, 2);
        CheckInService.CheckInResult result = service.checkInGast(8);

        assertNotNull(result.getGast());
        assertNull(result.getKamer());
        assertEquals(1, hotel.personen.size());
    }

    // Ik maak zelf een CheckInResult aan; ik verwacht dat dit object
    // precies dezelfde gast en kamer via de getters teruggeeft.
    @Test void testCheckInResultBewaartWaarden() {
        Gast gast = new Gast(9, 1);
        Kamer kamer = new Kamer();

        CheckInService.CheckInResult result = new CheckInService.CheckInResult(gast, kamer);

        assertEquals(gast, result.getGast());
        assertEquals(kamer, result.getKamer());
    }
}
