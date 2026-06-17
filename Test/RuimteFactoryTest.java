import Model.ILogger;
import Model.RuimteFactory;
import Model.ruimte.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor RuimteFactory: maakRuimte voor alle types
public class RuimteFactoryTest {

    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    // hulpmethode: maak een basis JSON object
    static JSONObject maakJson(String areaType) {
        JSONObject obj = new JSONObject();
        obj.put("AreaType", areaType);
        obj.put("_posX", 2);
        obj.put("_posY", 3);
        obj.put("_breedte", 2);
        obj.put("_hoogte", 1);
        return obj;
    }

    // maakRuimte Room: geeft Kamer terug
    @Test void testMaakRoom() {
        RuimteFactory factory = new RuimteFactory(new TestLogger());
        JSONObject obj = maakJson("Room");
        obj.put("Classification", "3 sterren");
        Ruimte r = factory.maakRuimte("Room", obj);
        assertInstanceOf(Kamer.class, r);
        assertEquals(3, ((Kamer) r).sterren);
    }

    // maakRuimte Room: kamernummer wordt correct berekend (begint bij 101 voor verdieping 1)
    @Test void testMaakRoomKamernummer() {
        RuimteFactory factory = new RuimteFactory(new TestLogger());
        JSONObject obj = maakJson("Room");
        obj.put("Classification", "2 sterren");
        Kamer k = (Kamer) factory.maakRuimte("Room", obj);
        assertTrue(k.kamernummer >= 101);
    }

    // maakRuimte Restaurant: geeft Restaurant terug met capaciteit
    @Test void testMaakRestaurant() {
        RuimteFactory factory = new RuimteFactory(new TestLogger());
        JSONObject obj = maakJson("Restaurant");
        obj.put("Capacity", 10);
        Ruimte r = factory.maakRuimte("Restaurant", obj);
        assertInstanceOf(Restaurant.class, r);
        assertEquals(10, ((Restaurant) r).capaciteit);
    }

    // maakRuimte Restaurant: zonder Capacity default naar 0
    @Test void testMaakRestaurantZonderCapaciteit() {
        RuimteFactory factory = new RuimteFactory(new TestLogger());
        JSONObject obj = maakJson("Restaurant");
        Ruimte r = factory.maakRuimte("Restaurant", obj);
        assertInstanceOf(Restaurant.class, r);
        assertEquals(0, ((Restaurant) r).capaciteit);
    }

    // maakRuimte Cinema: geeft Bioscoop terug
    @Test void testMaakCinema() {
        RuimteFactory factory = new RuimteFactory(new TestLogger());
        Ruimte r = factory.maakRuimte("Cinema", maakJson("Cinema"));
        assertInstanceOf(Bioscoop.class, r);
    }

    // maakRuimte Fitness: geeft Fitnessruimte terug
    @Test void testMaakFitness() {
        RuimteFactory factory = new RuimteFactory(new TestLogger());
        Ruimte r = factory.maakRuimte("Fitness", maakJson("Fitness"));
        assertInstanceOf(Fitnessruimte.class, r);
    }

    // maakRuimte onbekend type: geeft basis Ruimte terug
    @Test void testMaakOnbekendType() {
        RuimteFactory factory = new RuimteFactory(new TestLogger());
        Ruimte r = factory.maakRuimte("Lounge", maakJson("Lounge"));
        assertEquals(Ruimte.class, r.getClass());
    }

    // meerdere kamers: kamernummers zijn oplopend per verdieping
    @Test void testMeerdereKamersOplopendNummer() {
        RuimteFactory factory = new RuimteFactory(new TestLogger());
        JSONObject obj1 = maakJson("Room");
        obj1.put("Classification", "1 sterren");
        JSONObject obj2 = maakJson("Room");
        obj2.put("Classification", "1 sterren");
        Kamer k1 = (Kamer) factory.maakRuimte("Room", obj1);
        Kamer k2 = (Kamer) factory.maakRuimte("Room", obj2);
        assertTrue(k2.kamernummer > k1.kamernummer);
    }

    // constructor met ondersteKamerPosY: geen crash
    @Test void testConstructorMetOndersteKamerPosY() {
        assertDoesNotThrow(() -> new RuimteFactory(new TestLogger(), 5));
    }

    // null logger: geen crash
    @Test void testNullLogger() {
        RuimteFactory factory = new RuimteFactory(null);
        JSONObject obj = maakJson("Room");
        obj.put("Classification", "1 sterren");
        assertDoesNotThrow(() -> factory.maakRuimte("Room", obj));
    }
}
