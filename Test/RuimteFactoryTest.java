import Model.RuimteFactory;
import Model.ruimte.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor RuimteFactory: ik test dat JSON-types de juiste Ruimte-subklasse maken.
public class RuimteFactoryTest {

    // Ik geef type Room mee; ik verwacht een Kamer met de juiste sterren.
    @Test void testMaakKamer() {
        RuimteFactory f = new RuimteFactory(null);
        JSONObject obj = new JSONObject();
        obj.put("Classification", "3 Star");
        Ruimte r = f.maakRuimte("Room", obj);
        assertTrue(r instanceof Kamer);
        assertEquals(3, ((Kamer) r).sterren);
    }

    // Ik maak twee Kamers op dezelfde verdieping; ik verwacht oplopende kamernummers op die verdieping.
    @Test void testKamernummerOplopend() {
        RuimteFactory f = new RuimteFactory(null);
        JSONObject obj = new JSONObject();
        obj.put("Classification", "1 Star");
        obj.put("_posY", 1);
        Kamer k1 = (Kamer) f.maakRuimte("Room", obj);
        Kamer k2 = (Kamer) f.maakRuimte("Room", obj);
        assertEquals(101, k1.kamernummer);
        assertEquals(102, k2.kamernummer);
    }

    // Ik maak Kamers op verschillende y-posities; ik verwacht dat de onderste verdieping bij 101 begint.
    @Test void testKamernummerPerVerdieping() {
        RuimteFactory f = new RuimteFactory(null, 3);
        JSONObject bovensteVerdieping = new JSONObject();
        bovensteVerdieping.put("Classification", "1 Star");
        bovensteVerdieping.put("_posY", 1);
        JSONObject ondersteVerdieping = new JSONObject();
        ondersteVerdieping.put("Classification", "1 Star");
        ondersteVerdieping.put("_posY", 3);

        Kamer k1 = (Kamer) f.maakRuimte("Room", ondersteVerdieping);
        Kamer k2 = (Kamer) f.maakRuimte("Room", bovensteVerdieping);
        Kamer k3 = (Kamer) f.maakRuimte("Room", bovensteVerdieping);

        assertEquals(101, k1.kamernummer);
        assertEquals(301, k2.kamernummer);
        assertEquals(302, k3.kamernummer);
    }

    // Ik geef type Restaurant mee; ik verwacht een Restaurant met capaciteit.
    @Test void testMaakRestaurant() {
        RuimteFactory f = new RuimteFactory(null);
        JSONObject obj = new JSONObject();
        obj.put("Capacity", 5);
        Ruimte r = f.maakRuimte("Restaurant", obj);
        assertTrue(r instanceof Restaurant);
        assertEquals(5, ((Restaurant) r).capaciteit);
    }

    // Ik geef type Cinema mee; ik verwacht een Bioscoop.
    @Test void testMaakBioscoop() {
        Ruimte r = new RuimteFactory(null).maakRuimte("Cinema", new JSONObject());
        assertTrue(r instanceof Bioscoop);
    }

    // Ik geef type Fitness mee; ik verwacht een Fitnessruimte.
    @Test void testMaakFitnessruimte() {
        Ruimte r = new RuimteFactory(null).maakRuimte("Fitness", new JSONObject());
        assertTrue(r instanceof Fitnessruimte);
    }

    // Ik geef een onbekend type mee; ik verwacht een gewone Ruimte.
    @Test void testOnbekendTypeGeeftRuimte() {
        Ruimte r = new RuimteFactory(null).maakRuimte("Onbekend", new JSONObject());
        assertEquals(Ruimte.class, r.getClass());
    }
}
