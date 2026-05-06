import Model.RuimteFactory;
import Model.ruimte.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RuimteFactoryTest {

    @Test void testMaakKamer() {
        RuimteFactory f = new RuimteFactory(null);
        JSONObject obj = new JSONObject();
        obj.put("Classification", "3 Star");
        Ruimte r = f.maakRuimte("Room", obj);
        assertTrue(r instanceof Kamer);
        assertEquals(3, ((Kamer) r).sterren);
    }

    @Test void testKamernummerOplopend() {
        RuimteFactory f = new RuimteFactory(null);
        JSONObject obj = new JSONObject();
        obj.put("Classification", "1 Star");
        Kamer k1 = (Kamer) f.maakRuimte("Room", obj);
        Kamer k2 = (Kamer) f.maakRuimte("Room", obj);
        assertEquals(101, k1.kamernummer);
        assertEquals(102, k2.kamernummer);
    }

    @Test void testMaakRestaurant() {
        RuimteFactory f = new RuimteFactory(null);
        JSONObject obj = new JSONObject();
        obj.put("Capacity", 5);
        Ruimte r = f.maakRuimte("Restaurant", obj);
        assertTrue(r instanceof Restaurant);
        assertEquals(5, ((Restaurant) r).capaciteit);
    }

    @Test void testMaakBioscoop() {
        Ruimte r = new RuimteFactory(null).maakRuimte("Cinema", new JSONObject());
        assertTrue(r instanceof Bioscoop);
    }

    @Test void testMaakFitnessruimte() {
        Ruimte r = new RuimteFactory(null).maakRuimte("Fitness", new JSONObject());
        assertTrue(r instanceof Fitnessruimte);
    }

    @Test void testOnbekendTypeGeeftRuimte() {
        Ruimte r = new RuimteFactory(null).maakRuimte("Onbekend", new JSONObject());
        assertEquals(Ruimte.class, r.getClass());
    }
}