import Model.*;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.Fitnessruimte;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FitnessruimteTest {

    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    static HotelEvent maakEvent(HotelEventType type, int tijd, int gastId) {
        return new HotelEvent(tijd, type, gastId, -1);
    }

    // constructor zonder logger: lege gasten lijst, geen crash
    @Test void testConstructorLeeg() {
        Fitnessruimte f = new Fitnessruimte();
        assertNotNull(f.gasten);
        assertTrue(f.gasten.isEmpty());
    }

    // constructor met logger: geen crash
    @Test void testConstructorMetLogger() {
        assertDoesNotThrow(() -> new Fitnessruimte(new TestLogger()));
    }

    // isFaciliteit: true
    @Test void testIsFaciliteit() {
        assertTrue(new Fitnessruimte().isFaciliteit());
    }

    // getStatusTekst: bevat "Fitness"
    @Test void testGetStatusTekst() {
        Fitnessruimte f = new Fitnessruimte();
        assertTrue(f.getStatusTekst().contains("Fitness"));
    }

    // getStatusTekst: bevat "0 aanwezig" als leeg
    @Test void testGetStatusTekstLeeg() {
        Fitnessruimte f = new Fitnessruimte();
        assertTrue(f.getStatusTekst().contains("0"));
    }

    // GOTO_FITNESS: wordt gelogd met gastId
    @Test void testGotoFitnessWordtGelogd() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 10, 5));
        assertTrue(logger.logs.get(0).contains("5"));
    }

    // GOTO_FITNESS: tijdstip in log
    @Test void testGotoFitnessLogBevatTijdstip() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 42, 3));
        assertTrue(logger.logs.get(0).contains("42"));
    }

    // NONE: gast klaar na 20 ticks → wordt gelogd
    @Test void testGastKlaarNa20Ticks() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 10, 7));
        f.onEvent(maakEvent(HotelEventType.NONE, 30, 0));
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // NONE: gast nog niet klaar voor eindtijd → geen "klaar" log
    @Test void testGastNogNietKlaar() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 10, 7));
        f.onEvent(maakEvent(HotelEventType.NONE, 20, 0)); // 10+20=30, tijd=20 → nog niet klaar
        assertFalse(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // NONE: gast klaar precies op eindtijd (>=)
    @Test void testGastKlaarPreciesOpEindtijd() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 5, 2));
        f.onEvent(maakEvent(HotelEventType.NONE, 25, 0)); // 5+20=25 → klaar
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // null logger: GOTO_FITNESS crasht niet
    @Test void testNullLoggerGotoFitness() {
        assertDoesNotThrow(() -> new Fitnessruimte().onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 1)));
    }

    // null logger: NONE crasht niet
    @Test void testNullLoggerNone() {
        Fitnessruimte f = new Fitnessruimte();
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 1));
        assertDoesNotThrow(() -> f.onEvent(maakEvent(HotelEventType.NONE, 21, 0)));
    }

    // ander event wordt genegeerd
    @Test void testAnderEventGenegeerd() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.CHECK_IN, 10, 1));
        assertTrue(logger.logs.isEmpty());
    }

    // NONE zonder voorgaand GOTO_FITNESS: geen log
    @Test void testNoneZonderGotoFitness() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.NONE, 10, 0));
        assertFalse(logger.logs.stream().anyMatch(l -> l.contains("klaar")));
    }

    // meerdere gasten: beiden worden terugestuurd na hun eindtijd
    @Test void testMeerdereGastenKlaar() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 10));
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 11));
        f.onEvent(maakEvent(HotelEventType.NONE, 21, 0));
        long aantalKlaar = logger.logs.stream().filter(l -> l.contains("klaar")).count();
        assertEquals(2, aantalKlaar);
    }

    // meerdere gasten: vroegste is klaar, latere nog niet
    @Test void testMeerdereGastenEenKlaar() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 10));  // klaar op 21
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 10, 11)); // klaar op 30
        f.onEvent(maakEvent(HotelEventType.NONE, 21, 0));
        long aantalKlaar = logger.logs.stream().filter(l -> l.contains("klaar")).count();
        assertEquals(1, aantalKlaar);
    }

    // gastTerugService: gast wordt teruggestuurd naar kamer na klaar zijn
    @Test void testGastTerugServiceBijKlaar() {
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

        Fitnessruimte f = new Fitnessruimte();
        f.setGastTerugService(new GastRoutingService(hotel));
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 1));
        f.onEvent(maakEvent(HotelEventType.NONE, 21, 0));
        assertNotNull(gast.doelVakje);
    }

    // setGastTerugService: geen crash
    @Test void testSetGastTerugServiceGeenCrash() {
        Fitnessruimte f = new Fitnessruimte();
        assertDoesNotThrow(() -> f.setGastTerugService(null));
    }

    // breedteFitness / verlaatFitness: geen crash (lege placeholders)
    @Test void testBreedteFitnessVerlaatFitnessGeenCrash() {
        Fitnessruimte f = new Fitnessruimte();
        assertDoesNotThrow(() -> { f.breedteFitness(); f.verlaatFitness(); });
    }

    // erft van Ruimte: posX en posY zijn 0
    @Test void testErftVanRuimte() {
        Fitnessruimte f = new Fitnessruimte();
        assertEquals(0, f.posX);
        assertEquals(0, f.posY);
    }

    // gast klaar → wordt verwijderd uit sportEindTijden (tweede NONE vindt hem niet meer)
    @Test void testGastWordtVerwijderdNaKlaar() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 5));
        f.onEvent(maakEvent(HotelEventType.NONE, 21, 0)); // klaar
        long voor = logger.logs.stream().filter(l -> l.contains("klaar")).count();
        f.onEvent(maakEvent(HotelEventType.NONE, 22, 0)); // tweede NONE, al verwijderd
        long na = logger.logs.stream().filter(l -> l.contains("klaar")).count();
        assertEquals(voor, na); // geen extra "klaar" log
    }

    // GOTO_FITNESS: log bevat gastId correct (boundary: id=0)
    @Test void testGotoFitnessGastIdNul() {
        TestLogger logger = new TestLogger();
        Fitnessruimte f = new Fitnessruimte(logger);
        f.onEvent(maakEvent(HotelEventType.GOTO_FITNESS, 1, 0));
        assertFalse(logger.logs.isEmpty());
    }
}
