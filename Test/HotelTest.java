import Model.BrandalarmService;
import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Hotel: constructor, personen, ruimtes, schoonmaak, alarmen
public class HotelTest {

    // hulpmethode: bouw een volledig hotel
    static Hotel maakVolledigHotel() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(8, 6);
        hotel.breedte = 8;
        hotel.hoogte = 6;
        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 3; lift.breedte = 1; lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        lift.initWachtrijen(6);
        lift.setLobbyVerdieping(2);
        Trap trap = new Trap(2);
        trap.posX = 7; trap.posY = 3; trap.breedte = 2; trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        Lobby lobby = new Lobby(1, 2, 5, 1, 3, 2, hotel, null);
        hotel.lobby = lobby;
        hotel.ruimtes.add(lobby);
        hotel.layout.plaatsRuimte(lobby);
        hotel.pathfinder = new Pathfinder(hotel);
        hotel.brandalarmService = new BrandalarmService(hotel, null);
        return hotel;
    }

    // constructor: lijsten zijn aangemaakt en leeg
    @Test void testConstructorLijstenLeeg() {
        Hotel h = new Hotel();
        assertNotNull(h.ruimtes);
        assertNotNull(h.personen);
        assertNotNull(h.wachtendeSchoonmaakKamers);
        assertTrue(h.ruimtes.isEmpty());
        assertTrue(h.personen.isEmpty());
        assertTrue(h.wachtendeSchoonmaakKamers.isEmpty());
    }

    // constructor: alarmen staan uit
    @Test void testConstructorAlarmsUit() {
        Hotel h = new Hotel();
        assertFalse(h.brandalarmActief);
        assertFalse(h.godzillaActief);
    }

    // constructor: brandendeKolommen en slachtoffers zijn leeg
    @Test void testConstructorBrandendeKolommenEnSlachtoffersLeeg() {
        Hotel h = new Hotel();
        assertTrue(h.brandendeKolommen.isEmpty());
        assertTrue(h.slachtoffers.isEmpty());
    }

    // voegPersoonToe: persoon komt in de lijst
    @Test void testVoegPersoonToe() {
        Hotel h = new Hotel();
        Gast g = new Gast(1, 1);
        h.voegPersoonToe(g);
        assertTrue(h.personen.contains(g));
    }

    // voegPersoonToe: meerdere personen worden allemaal toegevoegd
    @Test void testVoegMeerderePersonenToe() {
        Hotel h = new Hotel();
        h.voegPersoonToe(new Gast(1, 1));
        h.voegPersoonToe(new Gast(2, 2));
        h.voegPersoonToe(new Schoonmaker());
        assertEquals(3, h.personen.size());
    }

    // voegPersoonToe: brandalarmService null crasht niet
    @Test void testVoegPersoonToeBrandalarmServiceNull() {
        Hotel hotel = new Hotel();
        hotel.brandalarmService = null;
        assertDoesNotThrow(() -> hotel.voegPersoonToe(new Gast(1, 1)));
    }

    // voegPersoonToe tijdens brandalarm: nieuwe persoon wordt direct geëvacueerd
    @Test void testVoegPersoonToeTijdensAlarm() {
        Hotel hotel = maakVolledigHotel();
        hotel.brandalarmActief = true;
        hotel.brandalarmService.activeer(1);
        Gast g = new Gast(1, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        hotel.voegPersoonToe(g);
        assertNotNull(g.doelVakje);
    }

    // krijgRuimteOp: geeft null als layout null is
    @Test void testKrijgRuimteOpZonderLayout() {
        assertNull(new Hotel().krijgRuimteOp(1, 1));
    }

    // krijgRuimteOp: geeft de ruimte terug als die aanwezig is
    @Test void testKrijgRuimteOpMetRuimte() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        Kamer kamer = new Kamer();
        kamer.posX = 2; kamer.posY = 2; kamer.breedte = 1; kamer.hoogte = 1;
        h.layout.plaatsRuimte(kamer);
        assertEquals(kamer, h.krijgRuimteOp(2, 2));
    }

    // krijgRuimteOp: geeft null als vakje geen ruimte heeft
    @Test void testKrijgRuimteOpLeegVakje() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        assertNull(h.krijgRuimteOp(3, 3));
    }

    // krijgRuimteOp: geeft null buiten grid
    @Test void testKrijgRuimteOpBuitenGrid() {
        Hotel h = new Hotel();
        h.layout = new Layout(5, 5);
        assertNull(h.krijgRuimteOp(0, 0));
        assertNull(h.krijgRuimteOp(6, 6));
    }

    // voegWachtendeSchoonmaakToe: kamer wordt toegevoegd
    @Test void testVoegWachtendeSchoonmaakToe() {
        Hotel h = new Hotel();
        Kamer kamer = new Kamer();
        h.voegWachtendeSchoonmaakToe(kamer);
        assertTrue(h.wachtendeSchoonmaakKamers.contains(kamer));
    }

    // voegWachtendeSchoonmaakToe: dubbel toevoegen wordt genegeerd
    @Test void testVoegWachtendeSchoonmaakToeDubbel() {
        Hotel h = new Hotel();
        Kamer kamer = new Kamer();
        h.voegWachtendeSchoonmaakToe(kamer);
        h.voegWachtendeSchoonmaakToe(kamer);
        assertEquals(1, h.wachtendeSchoonmaakKamers.size());
    }

    // voegWachtendeSchoonmaakToe: null kamer wordt genegeerd
    @Test void testVoegWachtendeSchoonmaakToeNull() {
        Hotel h = new Hotel();
        h.voegWachtendeSchoonmaakToe(null);
        assertTrue(h.wachtendeSchoonmaakKamers.isEmpty());
    }

    // voegWachtendeSchoonmaakToe: meerdere unieke kamers worden bijgehouden
    @Test void testMeerdereWachtendeKamers() {
        Hotel h = new Hotel();
        h.voegWachtendeSchoonmaakToe(new Kamer());
        h.voegWachtendeSchoonmaakToe(new Kamer());
        h.voegWachtendeSchoonmaakToe(new Kamer());
        assertEquals(3, h.wachtendeSchoonmaakKamers.size());
    }

    // verwijderWachtendeSchoonmaak: kamer wordt verwijderd
    @Test void testVerwijderWachtendeSchoonmaak() {
        Hotel h = new Hotel();
        Kamer kamer = new Kamer();
        h.voegWachtendeSchoonmaakToe(kamer);
        h.verwijderWachtendeSchoonmaak(kamer);
        assertFalse(h.wachtendeSchoonmaakKamers.contains(kamer));
    }

    // verwijderWachtendeSchoonmaak: niet-aanwezige kamer geeft geen crash
    @Test void testVerwijderWachtendeNietAanwezig() {
        assertDoesNotThrow(() -> new Hotel().verwijderWachtendeSchoonmaak(new Kamer()));
    }

    // godzillaActief vlag: standaard false, kan op true gezet worden
    @Test void testGodzillaActiefVlag() {
        Hotel h = new Hotel();
        assertFalse(h.godzillaActief);
        h.godzillaActief = true;
        assertTrue(h.godzillaActief);
    }

    // brandalarmActief vlag: standaard false, kan op true gezet worden
    @Test void testBrandalarmActiefVlag() {
        Hotel h = new Hotel();
        assertFalse(h.brandalarmActief);
        h.brandalarmActief = true;
        assertTrue(h.brandalarmActief);
    }

    // brandendeKolommen: kolommen kunnen worden toegevoegd
    @Test void testBrandendeKolommen() {
        Hotel h = new Hotel();
        h.brandendeKolommen.add(2);
        h.brandendeKolommen.add(3);
        assertTrue(h.brandendeKolommen.contains(2));
        assertTrue(h.brandendeKolommen.contains(3));
        assertEquals(2, h.brandendeKolommen.size());
    }
}
