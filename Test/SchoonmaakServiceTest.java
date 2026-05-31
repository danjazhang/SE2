import Model.Hotel;
import Model.Pathfinder;
import Model.SchoonmaakService;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor SchoonmaakService: ik test hier de beslislogica rond noodgevallen.
// De service hoort dus de juiste gast en kamer te vinden en daarna een vrije schoonmaker
// te kiezen en een route voor hem klaar te zetten.
public class SchoonmaakServiceTest {

    private Hotel hotel;
    private SchoonmaakService service;
    private Kamer kamer;
    private Schoonmaker schoonmaker;

    // Ik maak voor elke test een klein hotel met route-ondersteuning,
    // zodat de service een kamer kan vinden en een route kan zetten.
    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;

        Lift lift = new Lift();
        lift.posX = 1;
        lift.posY = 1;
        lift.breedte = 1;
        lift.hoogte = 4;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);
        trap.posX = 6;
        trap.posY = 1;
        trap.breedte = 1;
        trap.hoogte = 4;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        kamer = new Kamer();
        kamer.posX = 3;
        kamer.posY = 4;
        kamer.breedte = 1;
        kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        hotel.pathfinder = new Pathfinder(hotel);

        Gast gast = new Gast(7, 1);
        gast.setPathfinder(hotel.pathfinder);
        Vakje gastStart = hotel.layout.krijgVakje(2, 4);
        gast.zetStartPositie(gastStart);
        hotel.voegPersoonToe(gast);
        kamer.koppelGast(gast);

        schoonmaker = new Schoonmaker();
        schoonmaker.setPathfinder(hotel.pathfinder);
        Vakje schoonmakerStart = hotel.layout.krijgVakje(2, 1);
        schoonmaker.zetStartPositie(schoonmakerStart);
        hotel.voegPersoonToe(schoonmaker);

        service = new SchoonmaakService(hotel, null);
    }

    // Ik stuur een CLEANING_EMERGENCY event voor een bestaande gast;
    // ik verwacht dat de service de vrije schoonmaker aan die kamer koppelt
    // en hem dus als bezig markeert.
    @Test void testCleaningEmergencyStuurtSchoonmakerNaarKamer() {
        HotelEvent event = new HotelEvent(5, HotelEventType.CLEANING_EMERGENCY, 7, -1);

        service.onEvent(event);

        assertTrue(schoonmaker.bezig);
        assertEquals(kamer, schoonmaker.kamer);
        assertNotNull(schoonmaker.doelVakje);
    }

    // Ik stuur een ander soort event; ik verwacht dat de service dat negeert
    // en dus geen schoonmaaktaak aan de schoonmaker geeft.
    @Test void testAnderEventWordtGenegeerd() {
        HotelEvent event = new HotelEvent(5, HotelEventType.CHECK_IN, 7, -1);

        service.onEvent(event);

        assertFalse(schoonmaker.bezig);
        assertNull(schoonmaker.kamer);
    }
}
