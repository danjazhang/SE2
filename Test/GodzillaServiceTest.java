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

    @Test void testConstructor() {
        assertDoesNotThrow(() -> new GodzillaService(hotel, logger));
    }

    @Test void testStartZetGodzillaActief() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(1);
        assertTrue(hotel.godzillaActief);
    }

    @Test void testStartVoegKolomToe() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(1);
        assertTrue(hotel.brandendeKolommen.contains(1));
    }

    @Test void testStartLogt() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(5);
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("GODZILLA")));
    }

    @Test void testStartMarkeertGastOpKolom1() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(1, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(1, 1));
        hotel.voegPersoonToe(gast);
        gs.start(1);
        assertTrue(gast.gestorven);
    }

    @Test void testStartMarkeertNietGastOpAndereKolom() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(2, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        hotel.voegPersoonToe(gast);
        gs.start(1);
        assertFalse(gast.gestorven);
    }

    // behandel met tick deelbaar door 3
    @Test void testBehandelVoegVolgendeKolomToe() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(3);
        gs.behandel(6); // 6 % 3 == 0 → kolom 2 brandt
        assertTrue(hotel.brandendeKolommen.contains(2));
    }

    @Test void testBehandelMarkeertGastOpBrandKolom() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(3);
        Gast gast = new Gast(3, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);
        gs.behandel(6); // 6 % 3 == 0 → kolom 2 brandt
        assertTrue(gast.gestorven);
    }

    @Test void testBehandelDoetNietsAlsKlaar() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(3);
        gs.behandel(6);
        gs.behandel(9);
        gs.behandel(12);
        gs.behandel(15);
        assertTrue(gs.isKlaar());
        int aantalLogs = logger.logs.size();
        gs.behandel(18);
        assertEquals(aantalLogs, logger.logs.size());
    }

    @Test void testIsKlaarFalseAaNHetBegin() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        assertFalse(gs.isKlaar());
    }

    @Test void testIsKlaarTrueNaAlleKolommen() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(3);
        gs.behandel(6);
        gs.behandel(9);
        gs.behandel(12);
        gs.behandel(15);
        assertTrue(gs.isKlaar());
    }

    @Test void testMarkeerDodenNullVakjeOvergeslagen() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(4, 1);
        hotel.voegPersoonToe(gast);
        gs.markeerDodenOpKolom(1, 1);
        assertFalse(gast.gestorven);
    }

    @Test void testMarkeerDodenAlGestorvenWordtOvergeslagen() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(5, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        gast.gestorven = true;
        hotel.voegPersoonToe(gast);
        int logsVoor = logger.logs.size();
        gs.markeerDodenOpKolom(2, 1);
        assertEquals(logsVoor, logger.logs.size());
    }

    @Test void testConstructorZonderLogger() {
        assertDoesNotThrow(() -> {
            GodzillaService gs = new GodzillaService(hotel, null);
            gs.start(3);
            gs.behandel(6);
        });
    }

    @Test void testLiftPassagierWordtGestorven() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(6, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(1, 1));
        gast.inLift = true;
        hotel.lift.roep(gast, 1);
        hotel.voegPersoonToe(gast);
        gs.start(3);
        assertTrue(gast.gestorven);
    }

    @Test void testBehandelVoortschrijdendBrand() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        gs.start(3);
        gs.behandel(6);
        gs.behandel(9);
        assertTrue(hotel.brandendeKolommen.contains(1));
        assertTrue(hotel.brandendeKolommen.contains(2));
        assertTrue(hotel.brandendeKolommen.contains(3));
    }

    @Test void testStartZonderLift() {
        hotel.lift = null;
        GodzillaService gs = new GodzillaService(hotel, logger);
        assertDoesNotThrow(() -> gs.start(1));
        assertTrue(hotel.brandendeKolommen.contains(1));
    }

    @Test void testMarkeerDodenAndereKolomBlijftLeven() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(7, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        hotel.voegPersoonToe(gast);
        gs.markeerDodenOpKolom(2, 1);
        assertFalse(gast.gestorven);
    }

    @Test void testLiftPassagierAlGestorvenWordtOvergeslagen() {
        GodzillaService gs = new GodzillaService(hotel, logger);
        Gast gast = new Gast(8, 1);
        gast.zetStartPositie(hotel.layout.krijgVakje(1, 1));
        gast.inLift = true;
        gast.gestorven = true;
        hotel.lift.roep(gast, 1);
        hotel.voegPersoonToe(gast);
        int logsVoor = logger.logs.size();
        gs.start(3);
        assertEquals(logsVoor + 2, logger.logs.size());
    }
}
