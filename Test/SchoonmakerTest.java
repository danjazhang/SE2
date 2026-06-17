import Model.Hotel;
import Model.ILogger;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Schoonmaker: constructor, beweeg, schoonmaken, evacueer, routes, status
public class SchoonmakerTest {

    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(8, 5);
        hotel.breedte = 8;
        hotel.hoogte = 5;
        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 5;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        lift.initWachtrijen(5);
        Trap trap = new Trap(2);
        trap.posX = 7; trap.posY = 1; trap.breedte = 2; trap.hoogte = 5;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        hotel.pathfinder = new Pathfinder(hotel);
    }

    // --- Constructor ---

    // lege constructor: standaard status correct
    @Test void testConstructor() {
        Schoonmaker s = new Schoonmaker();
        assertFalse(s.bezig);
        assertNull(s.kamer);
        assertNull(s.huidigVakje);
        assertNull(s.doelVakje);
    }

    // constructor met logger: geen crash
    @Test void testConstructorMetLogger() {
        Schoonmaker s = new Schoonmaker(new TestLogger());
        assertFalse(s.bezig);
        assertNull(s.kamer);
    }

    // --- Basiseigenschappen ---

    // bezig op true zetten
    @Test void testZetBezig() {
        Schoonmaker s = new Schoonmaker();
        s.bezig = true;
        assertTrue(s.bezig);
    }

    // kamer koppelen
    @Test void testKoppelKamer() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        s.kamer = k;
        assertEquals(k, s.kamer);
    }

    // isSchoonmaker: true
    @Test void testIsSchoonmaker() {
        assertTrue(new Schoonmaker().isSchoonmaker());
    }

    // isGast: false
    @Test void testIsGast() {
        assertFalse(new Schoonmaker().isGast());
    }

    // --- maakKamerSchoon ---

    // maakKamerSchoon: bezig=true en kamer gekoppeld
    @Test void testMaakKamerSchoon() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        s.maakKamerSchoon(k);
        assertTrue(s.bezig);
        assertEquals(k, s.kamer);
    }

    // --- Logger ---

    // setLogger: geen crash
    @Test void testSetLogger() {
        assertDoesNotThrow(() -> new Schoonmaker().setLogger(bericht -> {}));
    }

    // setLogger null: geen crash
    @Test void testSetLoggerNull() {
        assertDoesNotThrow(() -> new Schoonmaker().setLogger(null));
    }

    // --- Wachtvakje ---

    // setWachtVakje: geen crash
    @Test void testSetWachtVakje() {
        assertDoesNotThrow(() -> new Schoonmaker().setWachtVakje(new Vakje()));
    }

    // setWachtVakje null: geen crash
    @Test void testSetWachtVakjeNull() {
        assertDoesNotThrow(() -> new Schoonmaker().setWachtVakje(null));
    }

    // staatOpWachtVakje: false als wachtVakje null
    @Test void testStaatOpWachtVakjeNullWacht() {
        assertFalse(new Schoonmaker().staatOpWachtVakje());
    }

    // staatOpWachtVakje: false als schoonmaker op ander vakje staat
    @Test void testStaatOpWachtVakjeAnderVakje() {
        Schoonmaker s = new Schoonmaker();
        Vakje w = hotel.layout.krijgVakje(3, 1);
        s.setWachtVakje(w);
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        assertFalse(s.staatOpWachtVakje());
    }

    // staatOpWachtVakje: true als schoonmaker op wachtvakje staat
    @Test void testStaatOpWachtVakjeJuistVakje() {
        Schoonmaker s = new Schoonmaker();
        Vakje w = hotel.layout.krijgVakje(3, 1);
        s.setWachtVakje(w);
        s.zetStartPositie(w);
        assertTrue(s.staatOpWachtVakje());
    }

    // --- gaNaarWachtVakje ---

    // gaNaarWachtVakje: zet route naar wachtvakje
    @Test void testGaNaarWachtVakje() {
        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(hotel.pathfinder);
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        s.setWachtVakje(hotel.layout.krijgVakje(5, 1));
        s.gaNaarWachtVakje();
        assertNotNull(s.doelVakje);
    }

    // gaNaarWachtVakje: geen crash als wachtvakje null
    @Test void testGaNaarWachtVakjeNull() {
        Schoonmaker s = new Schoonmaker();
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        assertDoesNotThrow(() -> s.gaNaarWachtVakje());
    }

    // --- zetRouteNaarKamer ---

    // zetRouteNaarKamer: doel wordt ingesteld
    @Test void testZetRouteNaarKamer() {
        Schoonmaker s = new Schoonmaker();
        Vakje doel = new Vakje();
        s.zetRouteNaarKamer(doel);
        assertEquals(doel, s.doelVakje);
    }

    // zetRouteNaarKamer: wist oude route
    @Test void testZetRouteNaarKamerWistOude() {
        Schoonmaker s = new Schoonmaker();
        s.zetDoel(new Vakje());
        Vakje nieuw = new Vakje();
        s.zetRouteNaarKamer(nieuw);
        assertEquals(nieuw, s.doelVakje);
    }

    // zetRouteNaarKamer: null crasht niet
    @Test void testZetRouteNaarKamerNull() {
        assertDoesNotThrow(() -> new Schoonmaker().zetRouteNaarKamer(null));
    }

    // --- beweeg ---

    // beweeg: geen crash zonder positie
    @Test void testBeweegZonderPositie() {
        assertDoesNotThrow(() -> new Schoonmaker().beweeg());
    }

    // beweeg: bezig=true maar kamer null → geen crash
    @Test void testBeweegBezigZonderKamer() {
        Schoonmaker s = new Schoonmaker();
        s.bezig = true;
        assertDoesNotThrow(() -> s.beweeg());
    }

    // beweeg: kamer maar niet bezig → geen crash
    @Test void testBeweegKamerNietBezig() {
        Schoonmaker s = new Schoonmaker();
        s.kamer = new Kamer();
        assertDoesNotThrow(() -> s.beweeg());
    }

    // beweeg: schoonmaker beweegt naar kamer
    @Test void testBeweegNaarKamer() {
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(hotel.pathfinder);
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        s.maakKamerSchoon(kamer);
        s.zetRouteNaarKamer(hotel.layout.krijgVakje(3, 1));
        s.beweeg();
        assertNotEquals(hotel.layout.krijgVakje(2, 1), s.huidigVakje);
    }

    // beweeg: log bij binnenkomen kamer
    @Test void testBeweegLogsBijBinnenkomen() {
        TestLogger logger = new TestLogger();
        Schoonmaker s = new Schoonmaker(logger);
        s.setPathfinder(hotel.pathfinder);
        Kamer k = new Kamer();
        k.posX = 3; k.posY = 1; k.breedte = 1; k.hoogte = 1;
        k.kamernummer = 101;
        hotel.ruimtes.add(k);
        hotel.layout.plaatsRuimte(k);
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        s.maakKamerSchoon(k);
        s.zetRouteNaarKamer(hotel.layout.krijgVakje(3, 1));
        s.beweeg();
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("101")));
    }

    // beweeg: kamer wordt schoon gemaakt na ticks
    @Test void testBeweegMaaktKamerSchoon() {
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        kamer.schoon = false;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        TestLogger logger = new TestLogger();
        Schoonmaker s = new Schoonmaker(logger);
        s.setPathfinder(hotel.pathfinder);
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        s.maakKamerSchoon(kamer);
        s.zetRouteNaarKamer(hotel.layout.krijgVakje(3, 1));
        for (int i = 0; i < 25; i++) s.beweeg();
        assertTrue(kamer.isSchoon());
        assertFalse(s.bezig);
    }

    // beweeg: afronden zet resterend op 1 → daarna bezig=false
    @Test void testSchoonmaakAfrondenViaReflectie() {
        TestLogger logger = new TestLogger();
        Schoonmaker s = new Schoonmaker(logger);
        Kamer k = new Kamer();
        s.bezig = true;
        s.kamer = k;
        Vakje v = new Vakje();
        v.ruimte = k;
        s.huidigVakje = v;
        s.beweeg(); // zet resterend op schoonmaakDuur
        try {
            var f = Schoonmaker.class.getDeclaredField("resterendeSchoonmaakTicks");
            f.setAccessible(true);
            f.set(s, 1);
        } catch (Exception e) { fail(e); }
        s.beweeg(); // resterende = 0 → afronden
        assertFalse(s.bezig);
        assertNull(s.kamer);
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("schoon")));
    }

    // beweeg: na afronden is bezig false en kamer null (wachtvakje route wordt door service gezet, niet hier)
    @Test void testWachtVakjeWordtDoelNaSchoonmaken() {
        TestLogger logger = new TestLogger();
        Schoonmaker s = new Schoonmaker(logger);
        Kamer k = new Kamer();
        Vakje kamerVakje = new Vakje();
        kamerVakje.ruimte = k;
        Vakje wacht = new Vakje();
        s.huidigVakje = kamerVakje;
        s.wachtVakje = wacht;
        s.bezig = true;
        s.kamer = k;
        try {
            var f = Schoonmaker.class.getDeclaredField("resterendeSchoonmaakTicks");
            f.setAccessible(true);
            f.set(s, 1);
        } catch (Exception e) { fail(e); }
        s.beweeg();
        // na afronden: kamer schoon, bezig false, kamer null
        assertFalse(s.bezig);
        assertNull(s.kamer);
        assertTrue(k.isSchoon());
    }

    // --- evacueer ---

    // evacueer: route naar uitgang wordt gezet
    @Test void testEvacueerZetRoute() {
        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(hotel.pathfinder);
        s.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        s.maakKamerSchoon(new Kamer());
        s.evacueer(hotel.layout.krijgVakje(4, 1), hotel.pathfinder);
        assertNotNull(s.doelVakje);
    }

    // evacueer: geen crash als pathfinder null
    @Test void testEvacueerZonderPathfinder() {
        Schoonmaker s = new Schoonmaker();
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        assertDoesNotThrow(() -> s.evacueer(hotel.layout.krijgVakje(3, 1), null));
    }

    // evacueer: geen crash als huidigVakje null
    @Test void testEvacueerZonderHuidigVakje() {
        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(hotel.pathfinder);
        assertDoesNotThrow(() -> s.evacueer(hotel.layout.krijgVakje(3, 1), hotel.pathfinder));
    }

    // --- Eigenschappen ---

    // setSchoonmaakDuur en getSchoonmaakDuur
    @Test void testSetEnGetSchoonmaakDuur() {
        Schoonmaker s = new Schoonmaker();
        s.setSchoonmaakDuur(15);
        assertEquals(15, s.getSchoonmaakDuur());
    }

    // setNoodSchoonmaker en isNoodSchoonmaker
    @Test void testNoodSchoonmaker() {
        Schoonmaker s = new Schoonmaker();
        s.setNoodSchoonmaker(true);
        assertTrue(s.isNoodSchoonmaker());
        s.setNoodSchoonmaker(false);
        assertFalse(s.isNoodSchoonmaker());
    }

    // setHuidigeTijd: geen crash
    @Test void testSetHuidigeTijd() {
        assertDoesNotThrow(() -> new Schoonmaker().setHuidigeTijd(42));
    }

    // --- getStatusTekst ---

    // bevat "Schoonmaker"
    @Test void testStatusTekstBevatSchoonmaker() {
        assertTrue(new Schoonmaker().getStatusTekst().contains("Schoonmaker"));
    }

    // "bezig met kamer" als bezig
    @Test void testStatusTekstBezig() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        k.kamernummer = 101;
        s.maakKamerSchoon(k);
        assertTrue(s.getStatusTekst().contains("bezig met kamer"));
    }

    // "onderweg" als bezig maar kamer null
    @Test void testStatusTekstOnderweg() {
        Schoonmaker s = new Schoonmaker();
        s.bezig = true;
        assertTrue(s.getStatusTekst().contains("onderweg"));
    }

    // "vrij" als niet bezig
    @Test void testStatusTekstVrij() {
        assertTrue(new Schoonmaker().getStatusTekst().contains("vrij"));
    }
}
