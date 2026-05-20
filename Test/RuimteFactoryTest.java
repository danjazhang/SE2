import Model.RuimteFactory;
import Model.ruimte.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RuimteFactoryTest {

    // Ik vraag de factory om een kamer te maken uit JSON; ik verwacht dat het resultaat een kamer met het juiste aantal sterren is.
    @Test void testMaakKamer() {
        RuimteFactory f = new RuimteFactory(null);
        JSONObject obj = new JSONObject();
        obj.put("Classification", "3 Star");
        Ruimte r = f.maakRuimte("Room", obj);
        assertTrue(r instanceof Kamer);
        assertEquals(3, ((Kamer) r).sterren);
    }

    // Ik maak twee kamers na elkaar; ik verwacht dat de kamernummers oplopend toegekend worden.
    @Test void testKamernummerOplopend() {
        RuimteFactory f = new RuimteFactory(null);
        JSONObject obj = new JSONObject();
        obj.put("Classification", "1 Star");
        Kamer k1 = (Kamer) f.maakRuimte("Room", obj);
        Kamer k2 = (Kamer) f.maakRuimte("Room", obj);
        assertEquals(101, k1.kamernummer);
        assertEquals(102, k2.kamernummer);
    }

    // Ik vraag de factory om een restaurant te maken; ik verwacht dat de capaciteit uit de JSON wordt overgenomen.
    @Test void testMaakRestaurant() {
        RuimteFactory f = new RuimteFactory(null);
        JSONObject obj = new JSONObject();
        obj.put("Capacity", 5);
        Ruimte r = f.maakRuimte("Restaurant", obj);
        assertTrue(r instanceof Restaurant);
        assertEquals(5, ((Restaurant) r).capaciteit);
    }

    // Ik vraag de factory om een bioscoop te maken; ik verwacht dat ik een Bioscoop-object terugkrijg.
    @Test void testMaakBioscoop() {
        Ruimte r = new RuimteFactory(null).maakRuimte("Cinema", new JSONObject());
        assertTrue(r instanceof Bioscoop);
    }

    // Ik vraag de factory om een fitnessruimte te maken; ik verwacht dat ik een Fitnessruimte-object terugkrijg.
    @Test void testMaakFitnessruimte() {
        Ruimte r = new RuimteFactory(null).maakRuimte("Fitness", new JSONObject());
        assertTrue(r instanceof Fitnessruimte);
    }

    // Ik geef een onbekend type door aan de factory; ik verwacht dat ik een gewone Ruimte terugkrijg.
    @Test void testOnbekendTypeGeeftRuimte() {
        Ruimte r = new RuimteFactory(null).maakRuimte("Onbekend", new JSONObject());
        assertEquals(Ruimte.class, r.getClass());
    }
}
