import Model.ILogger;
import Model.layout.Vakje;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SchoonmakerTest {

    // Logger om logs op te slaan tijdens tests
    static class TestLogger implements ILogger {

        List<String> logs = new ArrayList<>();

        @Override
        public void log(String bericht) {
            logs.add(bericht);
        }
    }

    // -------------------------------------------------
    // Constructor tests
    // -------------------------------------------------

    // ik doe dit: een nieuwe Schoonmaker aanmaken zonder parameters; ik verwacht dat de standaard status correct is ingesteld
    @Test
    void testConstructor() {

        Schoonmaker s = new Schoonmaker();

        assertFalse(s.bezig);

        assertNull(s.kamer);
    }

    // ik doe dit: een Schoonmaker aanmaken met logger; ik verwacht dat de logger correct geïnjecteerd wordt zonder errors
    @Test
    void testConstructorMetLogger() {

        TestLogger logger = new TestLogger();

        Schoonmaker s =
                new Schoonmaker(logger);

        assertFalse(s.bezig);

        assertNull(s.kamer);
    }

    // -------------------------------------------------
    // Basis property tests
    // -------------------------------------------------

    // ik doe dit: handmatig de status bezig op true zetten; ik verwacht dat de property correct wordt aangepast
    @Test
    void testZetBezig() {

        Schoonmaker s = new Schoonmaker();

        s.bezig = true;

        assertTrue(s.bezig);
    }

    // ik doe dit: een kamer koppelen aan een Schoonmaker; ik verwacht dat de referentie correct opgeslagen wordt
    @Test
    void testKoppelKamer() {

        Schoonmaker s = new Schoonmaker();

        Kamer k = new Kamer();

        s.kamer = k;

        assertEquals(k, s.kamer);
    }

    // ik doe dit: controleren of Schoonmaker erft van Persoon; ik verwacht dat basisvelden bestaan en initieel null zijn
    @Test
    void testErftVanPersoon() {

        Schoonmaker s = new Schoonmaker();

        assertNull(s.huidigVakje);

        assertNull(s.doelVakje);
    }

    // -------------------------------------------------
    // maakKamerSchoon tests
    // -------------------------------------------------

    // ik doe dit: maakKamerSchoon aanroepen; ik verwacht dat schoonmaker busy wordt en kamer wordt gekoppeld
    @Test
    void testMaakKamerSchoonZetBezig() {

        Schoonmaker s = new Schoonmaker();

        Kamer k = new Kamer();

        s.maakKamerSchoon(k);

        assertTrue(s.bezig);

        assertEquals(k, s.kamer);
    }

    // -------------------------------------------------
    // Logger tests
    // -------------------------------------------------

    // ik doe dit: setLogger aanroepen met geldige logger; ik verwacht dat dit geen crash geeft
    @Test
    void testSetLogger() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.setLogger(bericht -> {});
        });
    }

    // ik doe dit: setLogger aanroepen met null; ik verwacht dat systeem dit veilig afhandelt
    @Test
    void testSetLoggerNull() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.setLogger(null);
        });
    }

    // -------------------------------------------------
    // Wachtvakje tests
    // -------------------------------------------------

    // ik doe dit: een wachtvakje instellen; ik verwacht dat dit zonder fouten wordt opgeslagen
    @Test
    void testSetWachtVakje() {

        Schoonmaker s = new Schoonmaker();

        Vakje v = new Vakje();

        assertDoesNotThrow(() -> {

            s.setWachtVakje(v);
        });
    }

    // ik doe dit: null als wachtvakje instellen; ik verwacht dat dit geen crash veroorzaakt
    @Test
    void testSetWachtVakjeNull() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.setWachtVakje(null);
        });
    }

    // -------------------------------------------------
    // beweeg tests
    // -------------------------------------------------

    // ik doe dit: beweeg aanroepen zonder positie; ik verwacht dat het systeem niet crasht
    @Test
    void testBeweegZonderPositieCrashetNiet() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // ik doe dit: beweeg aanroepen terwijl kamer null is; ik verwacht dat dit veilig blijft
    @Test
    void testBeweegZonderKamer() {

        Schoonmaker s = new Schoonmaker();

        s.bezig = true;

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // ik doe dit: bewegen met null huidig vakje; ik verwacht dat logica geen crash veroorzaakt
    @Test
    void testBeweegMetNullVakje() {

        Schoonmaker s = new Schoonmaker();

        s.bezig = true;

        s.kamer = new Kamer();

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // ik doe dit: beweeg aanroepen zonder dat schoonmaker bezig is; ik verwacht veilige no-op
    @Test
    void testBeweegNietBezig() {

        Schoonmaker s = new Schoonmaker();

        s.kamer = new Kamer();

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // -------------------------------------------------
    // zetRouteNaarKamer tests
    // -------------------------------------------------

    // ik doe dit: route zetten met null input; ik verwacht dat dit veilig wordt afgehandeld
    @Test
    void testZetRouteNaarKamerNullCrashetNiet() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.zetRouteNaarKamer(null);
        });
    }

    // ik doe dit: route instellen naar geldig doelvakje; ik verwacht dat doel correct gezet wordt
    @Test
    void testZetRouteNaarKamer() {

        Schoonmaker s = new Schoonmaker();

        Vakje doel = new Vakje();

        s.zetRouteNaarKamer(doel);

        assertEquals(
                doel,
                s.doelVakje
        );
    }

    // ik doe dit: oude route vervangen door nieuwe; ik verwacht dat oude route overschreven wordt
    @Test
    void testZetRouteWistOudeRoute() {

        Schoonmaker s = new Schoonmaker();

        Vakje oud = new Vakje();

        Vakje nieuw = new Vakje();

        s.zetDoel(oud);

        s.zetRouteNaarKamer(nieuw);

        assertEquals(
                nieuw,
                s.doelVakje
        );
    }

    // -------------------------------------------------
    // Branch coverage tests
    // -------------------------------------------------

    // ik doe dit: beweeg met bezig=true maar zonder kamer; ik verwacht veilige afhandeling van edge case
    @Test
    void testBranchBezigZonderKamer() {

        Schoonmaker s = new Schoonmaker();

        s.bezig = true;

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // ik doe dit: kamer instellen maar niet bezig zetten; ik verwacht dat beweging geen crash veroorzaakt
    @Test
    void testBranchKamerMaarNietBezig() {

        Schoonmaker s = new Schoonmaker();

        s.kamer = new Kamer();

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // ik doe dit: logger op null zetten en bewegen; ik verwacht dat logging optioneel is en geen crash geeft
    @Test
    void testBranchNullLogger() {

        Schoonmaker s = new Schoonmaker();

        s.setLogger(null);

        s.bezig = true;

        s.kamer = new Kamer();

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // ik doe dit: bewegen met actieve logger; ik verwacht dat logging branch correct uitgevoerd wordt
    @Test
    void testBranchMetLogger() {

        TestLogger logger =
                new TestLogger();

        Schoonmaker s =
                new Schoonmaker(logger);

        s.bezig = true;

        s.kamer = new Kamer();

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // ik doe dit: route resetten naar null; ik verwacht dat doelVakje correct null wordt
    @Test
    void testDoelVakjeWordtNull() {

        Schoonmaker s = new Schoonmaker();

        s.zetDoel(new Vakje());

        s.zetRouteNaarKamer(null);

        assertNull(s.doelVakje);
    }

    // -------------------------------------------------
    // gaNaarOptimalePositie tests
    // -------------------------------------------------

    // ik doe dit: optimale positie functie aanroepen; ik verwacht dat deze veilig niets breekt
    @Test
    void testGaNaarOptimalePositieCrashetNiet() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.gaNaarOptimalePositie();
        });
    }

    // ik doe dit: meerdere keren optimale positie aanroepen; ik verwacht stabiel gedrag zonder errors
    @Test
    void testGaNaarOptimalePositieMeerdereKeren() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.gaNaarOptimalePositie();

            s.gaNaarOptimalePositie();

            s.gaNaarOptimalePositie();
        });
    }

    // -------------------------------------------------
    // EXTRA BRANCH COVERAGE TESTS (SCHOONMAKER)
    // -------------------------------------------------

    // ik doe dit: schoonmaak afronden simuleren; ik verwacht dat busy false wordt en kamer wordt vrijgegeven
    @Test
    void testSchoonmaakAfrondenBranch_Fixed() {

        TestLogger logger = new TestLogger();
        Schoonmaker s = new Schoonmaker(logger);

        Kamer k = new Kamer();

        s.bezig = true;
        s.kamer = k;

        Vakje v = new Vakje();
        v.ruimte = k;

        s.huidigVakje = v;

        s.beweeg();

        try {
            java.lang.reflect.Field f =
                    Schoonmaker.class.getDeclaredField("resterendeSchoonmaakTicks");
            f.setAccessible(true);
            f.set(s, 1);
        } catch (Exception e) {
            fail(e);
        }

        s.beweeg();

        assertFalse(s.bezig);
        assertNull(s.kamer);

        assertTrue(logger.logs.stream()
                .anyMatch(l -> l.contains("schoon")));
    }

    // ik doe dit: schoonmaak countdown simuleren; ik verwacht dat timer correct aftelt zonder crash
    @Test
    void testSchoonmaakCountdownBranch() {

        Schoonmaker s = new Schoonmaker();

        Kamer k = new Kamer();

        s.bezig = true;
        s.kamer = k;

        Vakje v = new Vakje();
        v.ruimte = k;

        s.huidigVakje = v;

        s.beweeg();

        int before = 10;

        try {
            java.lang.reflect.Field f =
                    Schoonmaker.class.getDeclaredField("resterendeSchoonmaakTicks");
            f.setAccessible(true);
            f.set(s, before);
        } catch (Exception e) {
            fail(e);
        }

        s.beweeg();

        assertDoesNotThrow(() -> s.beweeg());
    }

    // ik doe dit: schoonmaak afronden branch triggeren; ik verwacht dat cleanup correct uitgevoerd wordt
    @Test
    void testSchoonmaakAfrondenBranch() {

        TestLogger logger = new TestLogger();

        Schoonmaker s = new Schoonmaker(logger);

        Kamer k = new Kamer();

        s.bezig = true;
        s.kamer = k;

        Vakje v = new Vakje();
        v.ruimte = k;

        s.huidigVakje = v;

        try {
            java.lang.reflect.Field f =
                    Schoonmaker.class.getDeclaredField("resterendeSchoonmaakTicks");
            f.setAccessible(true);
            f.set(s, 1);
        } catch (Exception e) {
            fail(e);
        }

        assertDoesNotThrow(() -> s.beweeg());

        assertFalse(s.bezig);
        assertNull(s.kamer);

        assertTrue(logger.logs.stream()
                .anyMatch(l -> l.contains("schoon")));
    }

    // ik doe dit: wachtvakje branch testen; ik verwacht dat doelVakje correct gezet wordt bij afronden
    @Test
    void testWachtVakjeBranch() {

        TestLogger logger = new TestLogger();

        Schoonmaker s = new Schoonmaker(logger);

        Kamer k = new Kamer();

        Vakje current = new Vakje();
        current.ruimte = k;

        Vakje wacht = new Vakje();

        s.huidigVakje = current;
        s.wachtVakje = wacht;

        s.bezig = true;
        s.kamer = k;

        try {
            java.lang.reflect.Field f =
                    Schoonmaker.class.getDeclaredField("resterendeSchoonmaakTicks");
            f.setAccessible(true);
            f.set(s, 1);
        } catch (Exception e) {
            fail(e);
        }

        s.beweeg();

        assertEquals(wacht, s.doelVakje);
    }

    // ik doe dit: super.beweeg fallback branch; ik verwacht normale beweging zonder kamerlogica
    @Test
    void testSuperBeweegBranch() {

        Schoonmaker s = new Schoonmaker();

        Vakje start = new Vakje();
        Vakje doel = new Vakje();

        s.huidigVakje = start;
        s.zetDoel(doel);

        assertDoesNotThrow(() -> s.beweeg());
    }

    // ik doe dit: null kamer edge case; ik verwacht dat systeem geen crash geeft bij ontbrekende kamer
    @Test
    void testNullKamerBranch() {

        Schoonmaker s = new Schoonmaker();

        s.bezig = true;
        s.kamer = null;

        assertDoesNotThrow(() -> s.beweeg());
    }
}