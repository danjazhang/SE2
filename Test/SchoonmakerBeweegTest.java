import Model.*;
import Model.layout.Layout;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class SchoonmakerBeweegTest {

    // hulpklasse om logs op te vangen
    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    // hulpmethode: maak een minimaal hotel
    static Hotel maakHotel() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;
        Lift lift = new Lift();
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        Trap trap = new Trap(2);
        trap.posX = 6; trap.posY = 1; trap.breedte = 1; trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        hotel.pathfinder = new Pathfinder(hotel);
        return hotel;
    }

    // maakKamerSchoon: bezig wordt true en kamer wordt gekoppeld
    @Test void testMaakKamerSchoonZetBezig() {
        Schoonmaker s = new Schoonmaker();
        Kamer k = new Kamer();
        s.maakKamerSchoon(k);
        assertTrue(s.bezig);
        assertEquals(k, s.kamer);
    }

    // zetRouteNaarKamer: wist oude route en zet nieuw doel
    @Test void testZetRouteNaarKamer() {
        Hotel hotel = maakHotel();
        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(hotel.pathfinder);
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        s.zetRouteNaarKamer(hotel.layout.krijgVakje(4, 1));
        assertEquals(hotel.layout.krijgVakje(4, 1), s.doelVakje);
    }

    // zetRouteNaarKamer: geen crash met null vakje
    @Test void testZetRouteNaarKamerNullCrashetNiet() {
        Schoonmaker s = new Schoonmaker();
        assertDoesNotThrow(() -> s.zetRouteNaarKamer(null));
    }

    // setWachtVakje: wachtvakje wordt correct ingesteld
    @Test void testSetWachtVakje() {
        Hotel hotel = maakHotel();
        Schoonmaker s = new Schoonmaker();
        s.setWachtVakje(hotel.layout.krijgVakje(2, 1));
        assertEquals(hotel.layout.krijgVakje(2, 1), s.wachtVakje);
    }

    // setLogger: geen crash
    @Test void testSetLogger() {
        Schoonmaker s = new Schoonmaker();
        assertDoesNotThrow(() -> s.setLogger(bericht -> {}));
    }

    // beweeg: schoonmaker beweegt naar kamer toe
    @Test void testBeweegNaarKamer() {
        Hotel hotel = maakHotel();
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(hotel.pathfinder);
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        s.maakKamerSchoon(kamer);
        s.zetRouteNaarKamer(hotel.layout.krijgVakje(4, 1));
        s.beweeg();
        assertNotEquals(hotel.layout.krijgVakje(2, 1), s.huidigVakje);
    }

    // beweeg: schoonmaker maakt kamer schoon na 15 ticks in de kamer
    @Test void testBeweegMaaktKamerSchoonNa15Ticks() {
        Hotel hotel = maakHotel();
        TestLogger logger = new TestLogger();
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        kamer.schoon = false;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        Schoonmaker s = new Schoonmaker(logger);
        s.setPathfinder(hotel.pathfinder);
        // zet schoonmaker naast de kamer zodat binnenkomst getriggerd wordt
        s.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        s.maakKamerSchoon(kamer);
        s.zetRouteNaarKamer(hotel.layout.krijgVakje(3, 1));
        // 1 stap om kamer binnen te lopen + 15 ticks schoonmaken + 1 extra
        for (int i = 0; i < 17; i++) s.beweeg();
        assertTrue(kamer.isSchoon());
        assertFalse(s.bezig);
    }

    // gaNaarOptimalePositie: geen crash
    @Test void testGaNaarOptimalePositieCrashetNiet() {
        assertDoesNotThrow(() -> new Schoonmaker().gaNaarOptimalePositie());
    }
}
