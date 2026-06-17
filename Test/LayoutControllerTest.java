import Controller.LayoutController;
import Model.Hotel;
import Model.persoon.Schoonmaker;
import Model.ruimte.Bioscoop;
import Model.ruimte.Fitnessruimte;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Restaurant;
import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor LayoutController: maakHandmatigeLayout, getHotel, getHotelManager
public class LayoutControllerTest {

    private Path schrijfLayoutBestand(String json) throws Exception {
        Path bestand = Files.createTempFile("layout-controller-test", ".json");
        Files.write(bestand, json.getBytes(StandardCharsets.UTF_8));
        bestand.toFile().deleteOnExit();
        return bestand;
    }

    private String volledigeLayoutJson() {
        return "[" +
                "{\"AreaType\":\"Room\",\"Position\":\"1,1\",\"Dimension\":\"2,1\",\"Classification\":\"3 sterren\"}," +
                "{\"AreaType\":\"Restaurant\",\"Position\":\"3,2\",\"Dimension\":\"2,1\",\"Capacity\":12}," +
                "{\"AreaType\":\"Cinema\",\"Position\":\"1,3\",\"Dimension\":\"1,1\"}," +
                "{\"AreaType\":\"Fitness\",\"Position\":\"2,3\",\"Dimension\":\"1,1\"}" +
                "]";
    }

    // maakHandmatigeLayout: geeft een geldig id terug
    @Test void testMaakHandmatigeLayoutGeeftId() {
        LayoutController lc = new LayoutController();
        int id = lc.maakHandmatigeLayout("test", 5, 4);
        assertTrue(id >= 1);
    }

    // maakHandmatigeLayout: hotel heeft de juiste afmetingen
    @Test void testMaakHandmatigeLayoutAfmetingen() {
        LayoutController lc = new LayoutController();
        int id = lc.maakHandmatigeLayout("test", 6, 5);
        Hotel hotel = lc.getHotel(id);
        assertEquals(6, hotel.breedte);
        assertEquals(5, hotel.hoogte);
    }

    // maakHandmatigeLayout: hotel heeft een layout
    @Test void testMaakHandmatigeLayoutHeeftLayout() {
        LayoutController lc = new LayoutController();
        int id = lc.maakHandmatigeLayout("test", 4, 4);
        assertNotNull(lc.getHotel(id).layout);
    }

    // getHotel: geeft null voor onbekend id
    @Test void testGetHotelOnbekendId() {
        assertNull(new LayoutController().getHotel(999));
    }

    // meerdere layouts: ids zijn uniek
    @Test void testMeerdereLayoutsUniekIds() {
        LayoutController lc = new LayoutController();
        int id1 = lc.maakHandmatigeLayout("a", 3, 3);
        int id2 = lc.maakHandmatigeLayout("b", 4, 4);
        assertNotEquals(id1, id2);
        assertNotSame(lc.getHotel(id1), lc.getHotel(id2));
    }

    // setLogger: geen crash
    @Test void testSetLogger() {
        assertDoesNotThrow(() -> new LayoutController().setLogger(bericht -> {}));
    }

    // laadVanBestand met niet-bestaand bestand: geeft -1 terug
    @Test void testLaadVanNietBestaandBestand() {
        LayoutController lc = new LayoutController();
        int result = lc.laadVanBestand("niet_bestaand.json", "test");
        assertEquals(-1, result);
    }

    // laadVanBestand: succesvol laden geeft id en slaat hotel op
    @Test void testLaadVanBestandGeeftIdEnHotel() throws Exception {
        LayoutController lc = new LayoutController();
        Path bestand = schrijfLayoutBestand(volledigeLayoutJson());

        int id = lc.laadVanBestand(bestand.toString(), "geladen-layout");
        Hotel hotel = lc.getHotel(id);

        assertTrue(id >= 1);
        assertNotNull(hotel);
        assertNotNull(hotel.layout);
        assertEquals(id, hotel.layout.id);
        assertEquals("geladen-layout", hotel.layout.naam);
    }

    // laadVanBestand: grid krijgt extra ruimte voor lift, trap, lobby en buitenrij
    @Test void testLaadVanBestandGridAfmetingenMetExtraRuimte() throws Exception {
        LayoutController lc = new LayoutController();
        Path bestand = schrijfLayoutBestand(volledigeLayoutJson());

        int id = lc.laadVanBestand(bestand.toString(), "grid");
        Hotel hotel = lc.getHotel(id);

        assertEquals(7, hotel.breedte);
        assertEquals(6, hotel.hoogte);
        assertEquals(7, hotel.layout.breedte);
        assertEquals(6, hotel.layout.hoogte);
    }

    // laadVanBestand: JSON-ruimtes worden met x+1 en y+2 offset geplaatst
    @Test void testLaadVanBestandPlaatstJsonRuimtesMetOffset() throws Exception {
        LayoutController lc = new LayoutController();
        Path bestand = schrijfLayoutBestand(volledigeLayoutJson());

        int id = lc.laadVanBestand(bestand.toString(), "offset");
        Hotel hotel = lc.getHotel(id);

        assertInstanceOf(Kamer.class, hotel.layout.krijgVakje(2, 3).ruimte);
        assertInstanceOf(Restaurant.class, hotel.layout.krijgVakje(4, 4).ruimte);
        assertInstanceOf(Bioscoop.class, hotel.layout.krijgVakje(2, 5).ruimte);
        assertInstanceOf(Fitnessruimte.class, hotel.layout.krijgVakje(3, 5).ruimte);
    }

    // laadVanBestand: restaurantcapaciteit uit JSON wordt overgenomen
    @Test void testLaadVanBestandRestaurantCapaciteit() throws Exception {
        LayoutController lc = new LayoutController();
        Path bestand = schrijfLayoutBestand(volledigeLayoutJson());

        int id = lc.laadVanBestand(bestand.toString(), "restaurant");
        Hotel hotel = lc.getHotel(id);
        Restaurant restaurant = (Restaurant) hotel.layout.krijgVakje(4, 4).ruimte;

        assertEquals(12, restaurant.capaciteit);
    }

    // laadVanBestand: lift, trap en lobby worden automatisch aangemaakt
    @Test void testLaadVanBestandMaaktLiftTrapEnLobby() throws Exception {
        LayoutController lc = new LayoutController();
        Path bestand = schrijfLayoutBestand(volledigeLayoutJson());

        int id = lc.laadVanBestand(bestand.toString(), "basisruimtes");
        Hotel hotel = lc.getHotel(id);

        assertInstanceOf(Lift.class, hotel.lift);
        assertInstanceOf(Trap.class, hotel.trap);
        assertInstanceOf(Lobby.class, hotel.lobby);
        assertSame(hotel.lift, hotel.layout.krijgVakje(1, 3).ruimte);
        assertSame(hotel.trap, hotel.layout.krijgVakje(6, 3).ruimte);
        assertSame(hotel.lobby, hotel.layout.krijgVakje(3, 2).ruimte);
    }

    // laadVanBestand: services en standaard schoonmakers worden gekoppeld
    @Test void testLaadVanBestandKoppeltServicesEnSchoonmakers() throws Exception {
        LayoutController lc = new LayoutController();
        Path bestand = schrijfLayoutBestand(volledigeLayoutJson());

        int id = lc.laadVanBestand(bestand.toString(), "services");
        Hotel hotel = lc.getHotel(id);

        assertNotNull(hotel.pathfinder);
        assertNotNull(hotel.brandalarmService);
        long schoonmakers = hotel.personen.stream().filter(p -> p instanceof Schoonmaker).count();
        assertEquals(2, schoonmakers);
    }

    // laadVanBestand: layout zonder kamers gebruikt de false-branch van kamer-detectie
    @Test void testLaadVanBestandZonderKamers() throws Exception {
        LayoutController lc = new LayoutController();
        Path bestand = schrijfLayoutBestand("[" +
                "{\"AreaType\":\"Restaurant\",\"Position\":\"1,1\",\"Dimension\":\"1,1\",\"Capacity\":4}" +
                "]");

        int id = lc.laadVanBestand(bestand.toString(), "zonder-kamers");
        Hotel hotel = lc.getHotel(id);

        assertNotNull(hotel);
        assertInstanceOf(Restaurant.class, hotel.layout.krijgVakje(2, 3).ruimte);
        assertNotNull(hotel.lobby);
        assertNotNull(hotel.lift);
        assertNotNull(hotel.trap);
    }
}
