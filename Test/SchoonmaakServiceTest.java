import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.service.SchoonmaakService;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor SchoonmaakService: ik test de beslislogica bij schoonmaaknoodgevallen,
// dus gast zoeken, de juiste kamer nemen en een vrije schoonmaker kiezen.
public class SchoonmaakServiceTest {

    // Ik stuur een schoonmaaknoodgeval voor een gast met een kamer; ik verwacht dat de service
    // de juiste kamer aan een vrije schoonmaker toewijst en ook een looproute klaarzet.
    @Test void testCleaningEmergencyWijstKamerToe() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 6);
        hotel.breedte = 6;
        hotel.hoogte = 6;
        hotel.pathfinder = new Pathfinder(hotel);

        Kamer kamer = new Kamer();
        kamer.posX = 5;
        kamer.posY = 6;
        kamer.breedte = 1;
        kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Gast gast = new Gast(12, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 6));
        hotel.voegPersoonToe(gast);
        kamer.koppelGast(gast);

        Schoonmaker schoonmaker = new Schoonmaker();
        schoonmaker.setPathfinder(hotel.pathfinder);
        schoonmaker.setHotel(hotel);
        schoonmaker.zetStartPositie(hotel.layout.krijgVakje(1, 6));
        hotel.voegPersoonToe(schoonmaker);

        SchoonmaakService service = new SchoonmaakService(hotel, null);
        service.onEvent(new HotelEvent(1, HotelEventType.CLEANING_EMERGENCY, 12, -1));

        assertEquals(kamer, schoonmaker.kamer);
        assertTrue(schoonmaker.bezig);
        assertNotNull(schoonmaker.doelVakje);
    }

    // Ik stuur een ander event dan een schoonmaaknoodgeval; ik verwacht dat de service
    // dit negeert en dat de schoonmaker geen nieuwe taak krijgt.
    @Test void testAndereEventWordtGenegeerd() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(4, 4);
        hotel.breedte = 4;
        hotel.hoogte = 4;
        hotel.pathfinder = new Pathfinder(hotel);

        Schoonmaker schoonmaker = new Schoonmaker();
        schoonmaker.setPathfinder(hotel.pathfinder);
        schoonmaker.zetStartPositie(hotel.layout.krijgVakje(1, 4));
        hotel.voegPersoonToe(schoonmaker);

        SchoonmaakService service = new SchoonmaakService(hotel, null);
        service.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 12, -1));

        assertNull(schoonmaker.kamer);
        assertFalse(schoonmaker.bezig);
    }
}
