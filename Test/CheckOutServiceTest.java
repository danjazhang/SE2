import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.service.CheckOutService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor CheckOutService: ik test alleen de check-out flow zelf.
// Deze service hoort dus de gast te vinden en de kamer vrij te maken,
// maar niet meer zelf een schoonmaker aan te sturen.
public class CheckOutServiceTest {

    // Ik doe een check-out van een gast die aan een kamer gekoppeld is.
    // Ik verwacht dat de service de juiste kamer teruggeeft, de kamer vuil laat worden
    // en de koppeling met de gast verbreekt, zonder zelf een schoonmaker toe te wijzen.
    @Test void testCheckOutGastMaaktKamerVrijZonderSchoonmakerToeTeWijzen() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 6);
        hotel.breedte = 6;
        hotel.hoogte = 6;
        hotel.pathfinder = new Pathfinder(hotel);

        Kamer kamer = new Kamer();
        kamer.posX = 4;
        kamer.posY = 6;
        kamer.breedte = 1;
        kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Gast gast = new Gast(3, 1);
        Vakje gastStart = hotel.layout.krijgVakje(2, 6);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(gastStart);
        hotel.voegPersoonToe(gast);
        kamer.koppelGast(gast);

        CheckOutService service = new CheckOutService(hotel);
        CheckOutService.CheckOutResult result = service.checkOutGast(3);

        assertEquals(kamer, result.getKamer());
        assertNull(result.getSchoonmaker());
        assertFalse(kamer.isSchoon());
        assertFalse(kamer.isBezet());
    }

    // Ik doe een check-out voor een onbekende gast; ik verwacht dat de service
    // geen kamer en geen schoonmaker in het resultaat teruggeeft.
    @Test void testCheckOutGastOnbekend() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(4, 4);
        hotel.breedte = 4;
        hotel.hoogte = 4;
        hotel.pathfinder = new Pathfinder(hotel);

        CheckOutService service = new CheckOutService(hotel);
        CheckOutService.CheckOutResult result = service.checkOutGast(99);

        assertNull(result.getKamer());
        assertNull(result.getSchoonmaker());
    }

    // Ik maak zelf een CheckOutResult aan; ik verwacht dat dit object
    // exact dezelfde kamer en schoonmaker via de getters teruggeeft.
    @Test void testCheckOutResultBewaartWaarden() {
        Kamer kamer = new Kamer();
        Schoonmaker schoonmaker = new Schoonmaker();

        CheckOutService.CheckOutResult result = new CheckOutService.CheckOutResult(kamer, schoonmaker);

        assertEquals(kamer, result.getKamer());
        assertEquals(schoonmaker, result.getSchoonmaker());
    }
}
