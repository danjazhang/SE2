import Model.ILogger;
import Model.layout.Vakje;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SchoonmakerTest {

    // -------------------------------------------------
    // Test logger
    // -------------------------------------------------

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

    // Nieuwe schoonmaker heeft standaardwaarden
    @Test
    void testConstructor() {

        Schoonmaker s = new Schoonmaker();

        assertFalse(s.bezig);

        assertNull(s.kamer);
    }

    // Constructor met logger
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

    // bezig handmatig aanpassen
    @Test
    void testZetBezig() {

        Schoonmaker s = new Schoonmaker();

        s.bezig = true;

        assertTrue(s.bezig);
    }

    // kamer koppelen
    @Test
    void testKoppelKamer() {

        Schoonmaker s = new Schoonmaker();

        Kamer k = new Kamer();

        s.kamer = k;

        assertEquals(k, s.kamer);
    }

    // erft van Persoon
    @Test
    void testErftVanPersoon() {

        Schoonmaker s = new Schoonmaker();

        assertNull(s.huidigVakje);

        assertNull(s.doelVakje);
    }

    // -------------------------------------------------
    // maakKamerSchoon tests
    // -------------------------------------------------

    // methode zet bezig op true
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

    // setLogger mag geen crash geven
    @Test
    void testSetLogger() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.setLogger(bericht -> {});
        });
    }

    // logger mag null zijn
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

    // set wachtvakje
    @Test
    void testSetWachtVakje() {

        Schoonmaker s = new Schoonmaker();

        Vakje v = new Vakje();

        assertDoesNotThrow(() -> {

            s.setWachtVakje(v);
        });
    }

    // wachtvakje null
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

    // beweeg zonder positie
    @Test
    void testBeweegZonderPositieCrashetNiet() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // beweeg zonder kamer
    @Test
    void testBeweegZonderKamer() {

        Schoonmaker s = new Schoonmaker();

        s.bezig = true;

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // beweeg zonder huidig vakje
    @Test
    void testBeweegMetNullVakje() {

        Schoonmaker s = new Schoonmaker();

        s.bezig = true;

        s.kamer = new Kamer();

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // beweeg zonder bezig status
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

    // route zetten met null
    @Test
    void testZetRouteNaarKamerNullCrashetNiet() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.zetRouteNaarKamer(null);
        });
    }

    // route zetten met doelvakje
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

    // oude route moet gewist worden
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

    // beweeg met bezig true maar zonder kamer
    @Test
    void testBranchBezigZonderKamer() {

        Schoonmaker s = new Schoonmaker();

        s.bezig = true;

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // beweeg met kamer maar niet bezig
    @Test
    void testBranchKamerMaarNietBezig() {

        Schoonmaker s = new Schoonmaker();

        s.kamer = new Kamer();

        assertDoesNotThrow(() -> {

            s.beweeg();
        });
    }

    // beweeg met null logger
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

    // beweeg met logger
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

    // doelvakje mag null worden
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

    // lege methode mag geen crash geven
    @Test
    void testGaNaarOptimalePositieCrashetNiet() {

        Schoonmaker s = new Schoonmaker();

        assertDoesNotThrow(() -> {

            s.gaNaarOptimalePositie();
        });
    }

    // meerdere keren aanroepen
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

    // Test: schoonmaker komt kamer binnen → start schoonmaak + logger branch
    @Test
    void testSchoonmaakAfrondenBranch_Fixed() {

        TestLogger logger = new TestLogger();

        Schoonmaker s = new Schoonmaker(logger);

        Kamer k = new Kamer();

        // schoonmaker is bezig
        s.bezig = true;
        s.kamer = k;

        Vakje v = new Vakje();
        v.ruimte = k;

        // belangrijk: simuleer dat hij AL in kamer staat
        s.huidigVakje = v;

        // forceer countdown naar 1
        try {
            java.lang.reflect.Field f =
                    Schoonmaker.class.getDeclaredField("resterendeSchoonmaakTicks");
            f.setAccessible(true);
            f.set(s, 1);
        } catch (Exception e) {
            fail(e);
        }

        // actie
        s.beweeg();

        // 🔥 correcte verwachtingen
        assertFalse(s.bezig);   // nu klopt het
        assertNull(s.kamer);

        // logger check (optioneel maar stabiel)
        assertTrue(logger.logs.stream()
                .anyMatch(l -> l.contains("Schoonmaker maakt kamer")));
    }

    // Test: schoonmaker zit al in kamer → countdown branch
    @Test
    void testSchoonmaakCountdownBranch() {

        Schoonmaker s = new Schoonmaker();

        Kamer k = new Kamer();

        // schoonmaker is al bezig in kamer
        s.bezig = true;
        s.kamer = k;

        Vakje v = new Vakje();
        v.ruimte = k;

        s.huidigVakje = v;

        // eerste keer binnenkomst om counter te zetten
        s.beweeg();

        // nu zitten we in kamer → resterende ticks moeten aftellen
        int before = 10;

        try {
            java.lang.reflect.Field f =
                    Schoonmaker.class.getDeclaredField("resterendeSchoonmaakTicks");
            f.setAccessible(true);
            f.set(s, before);
        } catch (Exception e) {
            fail(e);
        }

        // tweede beweeg → countdown branch
        s.beweeg();

        assertDoesNotThrow(() -> s.beweeg());
    }

    // Test: schoonmaak eindigt → rondSchoonmaakAf branch
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

        // forceer resterende ticks = 1 zodat hij afrondt
        try {
            java.lang.reflect.Field f =
                    Schoonmaker.class.getDeclaredField("resterendeSchoonmaakTicks");
            f.setAccessible(true);
            f.set(s, 1);
        } catch (Exception e) {
            fail(e);
        }

        // deze call moet afronden → rondSchoonmaakAf()
        assertDoesNotThrow(() -> s.beweeg());

        // branches:
        // - bezig moet false worden
        // - kamer moet null worden
        assertFalse(s.bezig);
        assertNull(s.kamer);

        // logger branch moet geraakt zijn
        assertTrue(logger.logs.stream()
                .anyMatch(l -> l.contains("schoon")));
    }

    // Test: rondSchoonmaakAf met wachtvakje branch
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

        // forceer einde schoonmaak
        try {
            java.lang.reflect.Field f =
                    Schoonmaker.class.getDeclaredField("resterendeSchoonmaakTicks");
            f.setAccessible(true);
            f.set(s, 1);
        } catch (Exception e) {
            fail(e);
        }

        s.beweeg();

        // wachtvakje branch moet doel zetten
        assertEquals(wacht, s.doelVakje);
    }

    // Test: super.beweeg branch (niet in kamer → normale beweging)
    @Test
    void testSuperBeweegBranch() {

        Schoonmaker s = new Schoonmaker();

        Vakje start = new Vakje();
        Vakje doel = new Vakje();

        s.huidigVakje = start;
        s.zetDoel(doel);

        // geen kamer context → moet super.beweeg() gebruiken
        assertDoesNotThrow(() -> s.beweeg());
    }

    // Test: null kamer branch (moet veilig blijven)
    @Test
    void testNullKamerBranch() {

        Schoonmaker s = new Schoonmaker();

        s.bezig = true;
        s.kamer = null;

        assertDoesNotThrow(() -> s.beweeg());
    }
}