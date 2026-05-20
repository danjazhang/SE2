import Model.ILogger;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class SchoonmakerTest {

    // hulpklasse om logs op te vangen in de test
    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override
        public void log(String bericht) { logs.add(bericht); }
    }

    // een nieuwe schoonmaker is niet bezig en heeft geen kamer
    @Test void testConstructor() {
        Schoonmaker s = new Schoonmaker();
        assertFalse(s.bezig);
        assertNull(s.kamer);
    }

    // bezig mag handmatig op true gezet worden
    @Test void testZetBezig() {
        Schoonmaker s = new Schoonmaker();
        s.bezig = true;
        assertTrue(s.bezig);
    }

    // kamer mag handmatig gekoppeld worden aan de schoonmaker
    @Test void testKoppelKamer() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        s.kamer = k;
        assertEquals(k, s.kamer);
    }

    // schoonmaker erft van Persoon: huidigVakje en doelVakje beginnen op null
    @Test void testErftVanPersoon() {
        Schoonmaker s = new Schoonmaker();
        assertNull(s.huidigVakje);
        assertNull(s.doelVakje);
    }

    // maakKamerSchoon: bezig wordt true en kamer wordt gekoppeld
    @Test void testMaakKamerSchoonZetBezig() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        s.maakKamerSchoon(k);
        assertTrue(s.bezig);
        assertEquals(k, s.kamer);
    }

    // maakKamerSchoon: constructor met logger geeft geen crash
    @Test void testConstructorMetLogger() {
        TestLogger logger = new TestLogger();
        Schoonmaker s = new Schoonmaker(logger);
        assertFalse(s.bezig);
        assertNull(s.kamer);
    }

    // setLogger: geen crash
    @Test void testSetLogger() {
        Schoonmaker s = new Schoonmaker();
        assertDoesNotThrow(() -> s.setLogger(bericht -> {}));
    }

    // setWachtVakje: geen crash
    @Test void testSetWachtVakje() {
        Schoonmaker s = new Schoonmaker();
        assertDoesNotThrow(() -> s.setWachtVakje(new Model.layout.Vakje()));
    }

    // gaNaarOptimalePositie: geen crash
    @Test void testGaNaarOptimalePositieCrashetNiet() {
        assertDoesNotThrow(() -> new Schoonmaker().gaNaarOptimalePositie());
    }

    // beweeg: geen crash zonder positie
    @Test void testBeweegZonderPositieCrashetNiet() {
        assertDoesNotThrow(() -> new Schoonmaker().beweeg());
    }

    // zetRouteNaarKamer: geen crash met null
    @Test void testZetRouteNaarKamerNullCrashetNiet() {
        assertDoesNotThrow(() -> new Schoonmaker().zetRouteNaarKamer(null));
    }
}
