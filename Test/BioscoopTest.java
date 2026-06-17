import Model.*;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.Bioscoop;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BioscoopTest {

    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    static HotelEvent maakEvent(HotelEventType type, int tijd, int gastId) {
        return new HotelEvent(tijd, type, gastId, -1);
    }

    // constructor: film niet bezig, duur 0, gasten leeg
    @Test void testConstructor() {
        Bioscoop b = new Bioscoop();
        assertFalse(b.filmBezig);
        assertEquals(0, b.filmDuur);
        assertNotNull(b.gasten);
        assertTrue(b.gasten.isEmpty());
    }

    // erft van Ruimte: posX en posY zijn 0
    @Test void testErftVanRuimte() {
        Bioscoop b = new Bioscoop();
        assertEquals(0, b.posX);
        assertEquals(0, b.posY);
    }

    // GOTO_CINEMA: log bevat gastId
    @Test void testGotoCinemaWordtGelogd() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.GOTO_CINEMA, 100, 12));
        assertTrue(logger.logs.get(0).contains("12"));
    }

    // START_CINEMA: filmBezig wordt true
    @Test void testStartCinemaZetFilmBezig() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        assertTrue(b.filmBezig);
    }

    // START_CINEMA: log bevat "film start"
    @Test void testStartCinemaLogt() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        assertTrue(logger.logs.get(0).contains("film start"));
    }

    // film eindigt na 40 ticks (standaard filmDuur)
    @Test void testFilmEindigt() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        b.onEvent(maakEvent(HotelEventType.NONE, 140, 0));
        assertFalse(b.filmBezig);
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("film eindigt")));
    }

    // film eindigt niet te vroeg
    @Test void testFilmEindigtNietVroeg() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        b.onEvent(maakEvent(HotelEventType.NONE, 130, 0));
        assertTrue(b.filmBezig);
    }

    // NONE zonder film: geen effect
    @Test void testNoneZonderFilm() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.NONE, 50, 0));
        assertTrue(logger.logs.isEmpty());
        assertFalse(b.filmBezig);
    }

    // ander event wordt genegeerd
    @Test void testAnderEventGenegeerd() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.CHECK_IN, 10, 1));
        assertTrue(logger.logs.isEmpty());
    }

    // null logger: geen crash bij GOTO_CINEMA
    @Test void testNullLoggerGotoCinema() {
        assertDoesNotThrow(() -> new Bioscoop().onEvent(maakEvent(HotelEventType.GOTO_CINEMA, 10, 1)));
    }

    // null logger: geen crash bij START_CINEMA
    @Test void testNullLoggerStartCinema() {
        assertDoesNotThrow(() -> new Bioscoop().onEvent(maakEvent(HotelEventType.START_CINEMA, 10, -1)));
    }

    // null logger: geen crash bij film einde
    @Test void testNullLoggerFilmEinde() {
        Bioscoop b = new Bioscoop();
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 100, -1));
        assertDoesNotThrow(() -> b.onEvent(maakEvent(HotelEventType.NONE, 140, 0)));
    }

    // setFilmDuur: film eindigt op nieuwe duur
    @Test void testSetFilmDuur() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.setFilmDuur(10);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 1, -1));
        b.onEvent(maakEvent(HotelEventType.NONE, 11, 0));
        assertFalse(b.filmBezig);
    }

    // gastTerugService: gasten worden terugestuurd na film einde
    @Test void testGastTerugServiceBijFilmEinde() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;
        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        lift.initWachtrijen(4);
        Trap trap = new Trap(2);
        trap.posX = 6; trap.posY = 1; trap.breedte = 1; trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        hotel.pathfinder = new Pathfinder(hotel);
        Gast gast = new Gast(1, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        kamer.koppelGast(gast);
        hotel.voegPersoonToe(gast);

        Bioscoop b = new Bioscoop();
        b.setGastTerugService(new GastRoutingService(hotel));
        b.onEvent(maakEvent(HotelEventType.GOTO_CINEMA, 1, 1));
        b.setFilmDuur(10);
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 1, -1));
        b.onEvent(maakEvent(HotelEventType.NONE, 11, -1));
        assertNotNull(gast.doelVakje);
    }

    // isFaciliteit: true
    @Test void testIsFaciliteit() {
        assertTrue(new Bioscoop().isFaciliteit());
    }

    // GOTO_CINEMA met tijdstip in log
    @Test void testGotoCinemaLogBevatTijdstip() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.onEvent(maakEvent(HotelEventType.GOTO_CINEMA, 55, 3));
        assertTrue(logger.logs.get(0).contains("55"));
    }

    // meerdere gasten: allemaal worden terugestuurd na film
    @Test void testMeerdereGastenBijFilmEinde() {
        TestLogger logger = new TestLogger();
        Bioscoop b = new Bioscoop(logger);
        b.setFilmDuur(10);
        b.onEvent(maakEvent(HotelEventType.GOTO_CINEMA, 1, 1));
        b.onEvent(maakEvent(HotelEventType.GOTO_CINEMA, 1, 2));
        b.onEvent(maakEvent(HotelEventType.START_CINEMA, 1, -1));
        b.onEvent(maakEvent(HotelEventType.NONE, 11, -1));
        assertFalse(b.filmBezig);
    }
}
