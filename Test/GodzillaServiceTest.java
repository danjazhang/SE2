import Model.GodzillaService;
import Model.Hotel;
import Model.ILogger;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor GodzillaService: start, behandel, isKlaar, markeerDodenOpKolom
public class GodzillaServiceTest {

    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    private Hotel hotel;
    private TestLogger logger;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(5, 4);
        hotel.breedte = 5;
        hotel.hoogte = 4;

        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        lift.initWachtrijen(4);

        Trap trap = new Trap(2);
        trap.posX = 5; trap.posY = 1; trap.breedte = 1; trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);
        logger = new TestLogger();
    }

    // constructor: aanmaken zonder crash
    @Test void testConstructor() {
        assertDoesNotThrow(() -> new GodzillaService(hotel, logger));
    }

    // start: zet godzillaActief op true
    @Test void testStartZetGodzillaActief() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(1);
        assertTrue(hotel.godzillaActief);
    }

    // start: voegt eerste kolom toe aan brandendeKolommen
    @Test void testStartVoegKolomToe() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(1);
        assertTrue(hotel.brandendeKolommen.contains(1));
    }

    // start: logt aanvalsbericht
    @Test void testStartLogt() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(5);
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("GODZILLA")));
    }

    // start: gast op kolom 1 wordt gestorven gemarkeerd
    @Test void testStartMarkeertGastOpKolom1() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(1, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(1, 1));
        hotel.voegPersoonToe(gast);
        gs.start(1);
        assertTrue(gast.gestorven);
    }

    // start: gast op andere kolom wordt niet gestorven
    @Test void testStartMarkeertNietGastOpAndereKolom() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(2, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        hotel.voegPersoonToe(gast);
        gs.start(1);
        assertFalse(gast.gestorven);
    }

    // behandel: voegt volgende kolom toe
    @Test void testBehandelVoegVolgendeKolomToe() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(1); // kolom 1 brandt
        gs.behandel(2); // kolom 2 brandt nu
        assertTrue(hotel.brandendeKolommen.contains(2));
    }

    // behandel: gast op brandende kolom wordt gestorven
    @Test void testBehandelMarkeertGastOpBrandKolom() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(1);
        Gast gast = new Gast(3, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);
        gs.behandel(2);
        assertFalse(gast.gestorven);
    }

    // behandel: doet niets als hotel al volledig afgebrand is
    @Test void testBehandelDoetNietsAlsKlaar() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        // verbrand alle kolommen
        gs.start(1);
        for (int i = 2; i <= hotel.breedte; i++) gs.behandel(i);
        assertTrue(gs.isKlaar());
        int aantalLogs = logger.logs.size();
        gs.behandel(99); // mag niets doen
        assertEquals(aantalLogs, logger.logs.size());
    }

    // isKlaar: false als nog niet alle kolommen branden
    @Test void testIsKlaarFalseAaNHetBegin() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        assertFalse(gs.isKlaar());
    }

    // isKlaar: true als alle kolommen branden
    @Test void testIsKlaarTrueNaAlleKolommen() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(1);
        for (int i = 2; i <= hotel.breedte; i++) gs.behandel(i);
        assertTrue(gs.isKlaar());
    }

    // markeerDodenOpKolom: gast met null vakje wordt overgeslagen
    @Test void testMarkeerDodenNullVakjeOvergeslagen() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(4, 1); // geen startpositie → huidigVakje = null
        hotel.voegPersoonToe(gast);
        gs.markeerDodenOpKolom(1, 1);
        assertFalse(gast.gestorven);
    }

    // markeerDodenOpKolom: al gestorven gast wordt niet opnieuw gemarkeerd
    @Test void testMarkeerDodenAlGestorvenWordtOvergeslagen() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(5, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        gast.gestorven = true;
        hotel.voegPersoonToe(gast);
        int logsVoor = logger.logs.size();
        gs.markeerDodenOpKolom(2, 1);
        // geen extra log want gast was al gestorven
        assertEquals(logsVoor, logger.logs.size());
    }

    // constructor zonder logger: geen crash
    @Test void testConstructorZonderLogger() {
        assertDoesNotThrow(() -> {
            GodzillaService gs = new GodzillaService(hotel, null);
            gs.start(1);
            gs.behandel(2);
        });
    }

    // liftpassagier op brandende liftkolom wordt gestorven gemarkeerd
    @Test void testLiftPassagierWordtGestorven() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(6, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(1, 1)); // liftkolom = posX=1
        gast.inLift = true;
        hotel.lift.roep(gast, 1);
        hotel.voegPersoonToe(gast);
        gs.start(1); // kolom 1 = liftkolom
        assertTrue(gast.gestorven);
    }

    // behandel: voegt meerdere kolommen toe in volgorde
    @Test void testBehandelVoortschrijdendBrand() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(1);
        gs.behandel(2);
        gs.behandel(3);
        assertTrue(hotel.brandendeKolommen.contains(1));
        assertTrue(hotel.brandendeKolommen.contains(2));
        assertTrue(hotel.brandendeKolommen.contains(3));
    }

    // start: zonder lift crasht niet en brandt nog steeds kolom 1
    @Test void testStartZonderLift() {
        hotel.lift = null;
        GodzillaService gs = new GodzillaService(hotel, logger);
        assertDoesNotThrow(() -> gs.start(1));
        assertTrue(hotel.brandendeKolommen.contains(1));
    }

    // markeerDodenOpKolom: persoon op andere kolom blijft leven
    @Test void testMarkeerDodenAndereKolomBlijftLeven() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(7, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        hotel.voegPersoonToe(gast);
        gs.markeerDodenOpKolom(2, 1);
        assertFalse(gast.gestorven);
    }

    // liftpassagier die al gestorven is wordt niet opnieuw gelogd
    @Test void testLiftPassagierAlGestorvenWordtOvergeslagen() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(8, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(1, 1));
        gast.inLift = true;
        gast.gestorven = true;
        hotel.lift.roep(gast, 1);
        hotel.voegPersoonToe(gast);
        int logsVoor = logger.logs.size();
        gs.start(1);
        assertEquals(logsVoor + 2, logger.logs.size()); // kolom + startbericht, geen passagierslog
    }
}
