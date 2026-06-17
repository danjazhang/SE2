import Model.BrandalarmService;
import Model.Hotel;
import Model.ILogger;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor BrandalarmService: activeer, vindUitgang, evacueerNieuwePersoon
public class BrandalarmServiceTest {

    static class TestLogger implements ILogger {
        List<String> logs = new ArrayList<>();
        @Override public void log(String bericht) { logs.add(bericht); }
    }

    private Hotel hotel;
    private TestLogger logger;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(8, 5);
        hotel.breedte = 8;
        hotel.hoogte = 5;

        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 3; lift.breedte = 1; lift.hoogte = 3;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        lift.initWachtrijen(5);
        lift.setLobbyVerdieping(2);

        Trap trap = new Trap(2);
        trap.posX = 7; trap.posY = 3; trap.breedte = 2; trap.hoogte = 3;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        // Lobby op y=2, buitenY = y-1 = 1
        Lobby lobby = new Lobby(1, 2, 6, 1, 4, 2, hotel, null);
        hotel.lobby = lobby;
        hotel.ruimtes.add(lobby);
        hotel.layout.plaatsRuimte(lobby);

        hotel.pathfinder = new Pathfinder(hotel);
        logger = new TestLogger();
    }

    // activeer: zet brandalarmActief op true
    @Test void testActiveerZetAlarmActief() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        bas.activeer(1);
        assertTrue(hotel.brandalarmActief);
    }

    // activeer: zet lift buiten gebruik
    @Test void testActiveerZetLiftUitBedrijf() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        bas.activeer(1);
        // lift mag geen nieuwe oproepen accepteren
        Gast g = new Gast(99, 1);
        hotel.lift.roep(g, 3);
        assertEquals(0, hotel.lift.aantalWachtend(3));
    }

    // activeer: logt evacuatiebericht
    @Test void testActiveerLogt() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        bas.activeer(5);
        assertTrue(logger.logs.stream().anyMatch(l -> l.contains("evacueren")));
    }

    // activeer: gast wordt gestuurd naar de uitgang (doelVakje != null)
    @Test void testActiveerStuurtGastNaarUitgang() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        Gast gast = new Gast(1, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        hotel.voegPersoonToe(gast);
        bas.activeer(1);
        assertNotNull(gast.doelVakje);
    }

    // activeer: schoonmaker wordt ook geëvacueerd
    @Test void testActiveerEvalueerSchoonmaker() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(hotel.pathfinder);
        s.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        hotel.voegPersoonToe(s);
        bas.activeer(1);
        assertNotNull(s.doelVakje);
    }

    // activeer: gast in lift wordt op uitstapvakje gezet en geëvacueerd
    @Test void testActiveerGastInLift() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        Gast gast = new Gast(2, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.inLift = true;
        gast.wachtOpLift = false;
        gast.zetStartPositie(hotel.layout.krijgVakje(1, 3)); // in liftkolom
        hotel.voegPersoonToe(gast);
        bas.activeer(1);
        assertFalse(gast.inLift);
        assertNotNull(gast.doelVakje);
    }

    // activeer: gast die wacht op lift wordt ook geëvacueerd
    @Test void testActiveerGastWachtOpLift() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        Gast gast = new Gast(3, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.wachtOpLift = true;
        gast.gebruiktLift = true;
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 3));
        hotel.voegPersoonToe(gast);
        bas.activeer(1);
        assertFalse(gast.wachtOpLift);
    }

    // vindUitgang: geeft het vakje op buitenY terug
    @Test void testVindUitgang() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        Vakje uitgang = bas.vindUitgang();
        assertNotNull(uitgang);
        assertEquals(hotel.lobby.posY - 1, uitgang.y);
    }

    // vindUitgang: geeft null als lobby null is
    @Test void testVindUitgangZonderLobby() {
        hotel.lobby = null;
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        assertNull(bas.vindUitgang());
    }

    // evacueerNieuwePersoon: nieuwe gast tijdens actief alarm krijgt evacuatieroute
    @Test void testEvacueerNieuwePersoon() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        bas.activeer(1); // stel uitgang in
        Gast nieuweGast = new Gast(10, 1);
        nieuweGast.setPathfinder(hotel.pathfinder);
        nieuweGast.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        bas.evacueerNieuwePersoon(nieuweGast);
        assertNotNull(nieuweGast.doelVakje);
    }

    // evacueerNieuwePersoon: doet niets als alarm niet actief is
    @Test void testEvacueerNieuwePersoonZonderAlarm() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        Gast gast = new Gast(11, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        bas.evacueerNieuwePersoon(gast);
        assertNull(gast.doelVakje);
    }

    // constructor zonder logger: geen crash bij activeer
    @Test void testZonderLogger() {
        BrandalarmService bas = new BrandalarmService(hotel, null);
        assertDoesNotThrow(() -> bas.activeer(1));
    }

    // hotel zonder lift: activeer crasht niet
    @Test void testActiveerZonderLift() {
        hotel.lift = null;
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        assertDoesNotThrow(() -> bas.activeer(1));
    }

    // activeer: lege personenlijst → geen crash
    @Test void testActiveerLegePersonenlijst() {
        BrandalarmService bas = new BrandalarmService(hotel, logger);
        assertDoesNotThrow(() -> bas.activeer(1));
    }
}
