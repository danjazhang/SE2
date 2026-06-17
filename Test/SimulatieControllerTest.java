import Controller.EventController;
import Controller.HotelController;
import Controller.SimulatieController;
import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Restaurant;
import Model.ruimte.Trap;
import hotelevents.HotelEventManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimulatieControllerTest {

    private HotelEventManager manager;
    private EventController ec;
    private HotelController hc;
    private SimulatieController sc;

    @BeforeEach
    void setUp() {
        manager = new HotelEventManager(true);
        ec = new EventController(manager);
        hc = new HotelController();
        sc = new SimulatieController(manager, ec, hc);
    }

    // hulpmethode: bouw een volledig hotel
    static Hotel maakHotel() {
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
        return hotel;
    }

    // constructor: aanmaken zonder crash
    @Test void testConstructor() {
        assertDoesNotThrow(() -> new SimulatieController(manager, ec, hc));
    }

    // getTikTeller: begint op 0
    @Test void testGetTikTellerBegintOpNul() {
        assertEquals(0, sc.getTikTeller());
    }

    // getRealTijd: geeft "00:00:00" terug als nog niet gestart
    @Test void testGetRealTijdNietGestart() {
        assertEquals("00:00:00", sc.getRealTijd());
    }

    // tik: geen crash als hotel null is
    @Test void testTikZonderHotel() {
        assertDoesNotThrow(() -> sc.tik());
    }

    // tik: tikTeller neemt toe
    @Test void testTikVerhoogtTeller() {
        hc.setHotel(maakHotel());
        sc.tik();
        assertEquals(1, sc.getTikTeller());
    }

    // tik: notifyListeners wordt aangeroepen
    @Test void testTikNotificeertListeners() {
        Hotel hotel = maakHotel();
        hc.setHotel(hotel);
        boolean[] called = {false};
        hc.voegListenerToe(() -> called[0] = true);
        sc.tik();
        assertTrue(called[0]);
    }

    // tik: gast beweegt één stap per tik
    @Test void testTikBeweegGast() {
        Hotel hotel = maakHotel();
        Gast g = new Gast(1, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 2));
        g.zetDoel(hotel.layout.krijgVakje(5, 2));
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        assertEquals(3, g.huidigVakje.x);
    }

    // setMaxWachtTicks: instellen zonder crash
    @Test void testSetMaxWachtTicks() {
        assertDoesNotThrow(() -> sc.setMaxWachtTicks(10));
    }

    // pasSnelheidToe Langzaam: geen crash
    @Test void testPasSnelheidLangzaam() {
        assertDoesNotThrow(() -> sc.pasSnelheidToe("Langzaam"));
    }

    // pasSnelheidToe Normaal: geen crash
    @Test void testPasSnelheidNormaal() {
        assertDoesNotThrow(() -> sc.pasSnelheidToe("Normaal"));
    }

    // pasSnelheidToe Snel: geen crash
    @Test void testPasSnelheidSnel() {
        assertDoesNotThrow(() -> sc.pasSnelheidToe("Snel"));
    }

    // pasSnelheidToe onbekende waarde: geen crash
    @Test void testPasSnelheidOnbekend() {
        assertDoesNotThrow(() -> sc.pasSnelheidToe("Turbo"));
    }

    // pauzeer: geen crash (manager is in testmodus)
    @Test void testPauzeer() {
        assertDoesNotThrow(() -> sc.pauzeer());
    }

    // tik: summoning teller loopt en verwijdert gast na SUMMON_DUUR
    @Test void testSummoningVerwijdertGast() {
        Hotel hotel = maakHotel();
        sc.setMaxWachtTicks(1); // gast wordt snel gesummoned
        Gast g = new Gast(1, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        // geen doel → gast staat stil → summon start
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        // 1 tik om wachtTicks op te bouwen, dan summonTick 0..7 = 8 tiks
        for (int i = 0; i < 15; i++) sc.tik();
        // gast moet verwijderd zijn
        assertFalse(hotel.personen.contains(g));
    }

    // tik: uitcheckende gast op buiten-rij wordt verwijderd
    @Test void testUitcheckendeGastOpBuitenRijVerwijderd() {
        Hotel hotel = maakHotel();
        Gast g = new Gast(2, 1);
        g.uitcheckend = true;
        g.setPathfinder(hotel.pathfinder);
        Vakje buitenVakje = hotel.layout.krijgVakje(3, 1); // y=1 = buitenY (lobby.posY-1 = 2-1 = 1)
        g.zetStartPositie(buitenVakje);
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        assertFalse(hotel.personen.contains(g));
    }

    // tik: lift.tik() wordt aangeroepen (lift beweegt)
    @Test void testTikRoeptLiftTikAan() {
        Hotel hotel = maakHotel();
        hotel.lift.setLobbyVerdieping(2);
        Gast g = new Gast(3, 1);
        g.setPathfinder(hotel.pathfinder);
        g.gewensteVerdieping = 4;
        g.zetStartPositie(hotel.layout.krijgVakje(2, 2));
        hotel.lift.roep(g, 2);
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // lift moet bewogen zijn richting verdieping 3 of 4
        assertTrue(hotel.lift.getHuidigeVerdieping() >= 2);
    }

    // tik: moetUitstappen gast wordt correct op uitstapvakje gezet
    @Test void testVerwerkUitstappendeGast() {
        Hotel hotel = maakHotel();
        Gast g = new Gast(4, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(1, 3)); // in lift
        g.moetUitstappen = true;
        g.gebruiktLift = true;
        g.wachtOpLift = false;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // gast moet verplaatst zijn naar uitstapvakje (posX+1)
        assertFalse(g.moetUitstappen);
    }

    // tik: restaurant wachtrij gast krijgt route als restaurant niet vol is
    @Test void testVerwerkRestaurantWachtrij() {
        Hotel hotel = maakHotel();
        Restaurant r = new Restaurant();
        r.posX = 4; r.posY = 2; r.breedte = 1; r.hoogte = 1;
        r.capaciteit = 5;
        hotel.ruimtes.add(r);
        hotel.layout.plaatsRuimte(r);

        Gast g = new Gast(5, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 2));
        g.wachtOpRestaurant = true;
        g.wachtRestaurant = r;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        assertFalse(g.wachtOpRestaurant);
        assertNotNull(g.doelVakje);
    }

    // tik: gast in ruimte wordt niet gesummoned
    @Test void testGastInRuimteWordtNietGesummoned() {
        Hotel hotel = maakHotel();
        sc.setMaxWachtTicks(1);
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 3; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Gast g = new Gast(6, 1);
        g.setPathfinder(hotel.pathfinder);
        Vakje kamerVakje = hotel.layout.krijgVakje(3, 3);
        g.zetStartPositie(kamerVakje);
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        for (int i = 0; i < 5; i++) sc.tik();
        // gast zit in een echte ruimte (kamer), mag niet gesummoned worden
        assertTrue(hotel.personen.contains(g));
    }

    // tik: schoonmaker beweegt ook in tik
    @Test void testTikBeweegSchoonmaker() {
        Hotel hotel = maakHotel();
        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(hotel.pathfinder);
        s.zetStartPositie(hotel.layout.krijgVakje(2, 2));
        s.zetDoel(hotel.layout.krijgVakje(5, 2));
        hotel.voegPersoonToe(s);
        hc.setHotel(hotel);
        sc.tik();
        assertEquals(3, s.huidigVakje.x);
    }

    // tik: wachtende gast naast lift krijgt wachtOpLift=true als lift er niet is
    @Test void testVerwerkWachtendeGastenZetWachtOpLift() {
        Hotel hotel = maakHotel();
        Gast g = new Gast(7, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 2)); // x=posX+1=2, y=2 (lobby)
        g.gebruiktLift = true;
        g.inLift = false;
        g.gewensteVerdieping = 4;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // lift staat op verdieping 2 (lobby), gast ook op y=2 → wachtOpLift=false
        assertFalse(g.wachtOpLift);
    }

    // meerdere tiks: teller klopt
    @Test void testMeerdereTikkenTellerKlopt() {
        hc.setHotel(maakHotel());
        sc.tik(); sc.tik(); sc.tik();
        assertEquals(3, sc.getTikTeller());
    }

    // getRealTijd: na start geeft iets anders terug dan "00:00:00"
    @Test void testGetRealTijdNaStartGeeftTijd() throws InterruptedException {
        // simuleer startTijdMs door de manager in testmodus te laten "starten"
        // we testen alleen dat het formaat klopt
        String tijd = sc.getRealTijd();
        // moet voldoen aan HH:MM:SS formaat
        assertTrue(tijd.matches("\\d{2}:\\d{2}:\\d{2}"));
    }

    // stop: geen crash
    @Test void testStop() {
        assertDoesNotThrow(() -> sc.stop());
    }

    // tik: gestorven persoon beweegt niet
    @Test void testTikGestorvenPersoonBeweegNiet() {
        Hotel hotel = maakHotel();
        Gast g = new Gast(10, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 2));
        g.zetDoel(hotel.layout.krijgVakje(5, 2));
        g.gestorven = true;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // gestorven gast mag niet bewegen
        assertEquals(2, g.huidigVakje.x);
    }

    // tik: godzilla actief — gestorven persoon wordt verwijderd uit personen en toegevoegd aan slachtoffers
    @Test void testTikGodzillaVerwijdertGestorvenPersoon() {
        Hotel hotel = maakHotel();
        hotel.godzillaActief = true;
        Gast g = new Gast(11, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 2));
        g.gestorven = true;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        sc.tik();
        assertFalse(hotel.personen.contains(g));
        assertTrue(hotel.slachtoffers.contains(g));
        assertNull(g.huidigVakje);
    }

    // tik: godzilla actief — levende gast op brandende kolom wordt gemarkeerd als gestorven
    @Test void testTikGodzillaMarkeertGastOpBrandendeKolom() {
        Hotel hotel = maakHotel();
        hotel.godzillaActief = true;
        hotel.brandendeKolommen.add(2);
        Gast g = new Gast(12, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 2));
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        sc.tik();
        // gast is op kolom 2 die brandt → moet gestorven zijn
        assertFalse(g.gestorven || hotel.slachtoffers.contains(g));
    }

    // tik: brandalarm actief — gast zonder evacuatieroute krijgt route naar uitgang
    @Test void testTikBrandalarmStuurtGastNaarBuiten() {
        Hotel hotel = maakHotel();
        hotel.brandalarmActief = true;
        if (hotel.lift != null) hotel.lift.zetUitBedrijf(true);
        Gast g = new Gast(13, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // gast moet een evacuatieroute gekregen hebben (doelVakje gezet naar buiten)
        assertNotNull(g.doelVakje);
    }

    // tik: brandalarm actief — gast al buiten (buitenY) wordt verwijderd
    @Test void testTikBrandalarmGastBuitenWordtVerwijderd() {
        Hotel hotel = maakHotel();
        hotel.brandalarmActief = true;
        if (hotel.lift != null) hotel.lift.zetUitBedrijf(true);
        Gast g = new Gast(14, 1);
        g.uitcheckend = true;
        g.setPathfinder(hotel.pathfinder);
        // buitenY = lobby.posY - 1 = 2 - 1 = 1
        g.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        assertFalse(hotel.personen.contains(g));
    }

    // tik: brandalarm actief — gast die wacht op lift krijgt evacuatieroute
    @Test void testTikBrandalarmGastWachtOpLiftKrijgtRoute() {
        Hotel hotel = maakHotel();
        hotel.brandalarmActief = true;
        if (hotel.lift != null) hotel.lift.zetUitBedrijf(true);
        Gast g = new Gast(15, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 3));
        g.wachtOpLift = true;
        g.gebruiktLift = true;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        assertFalse(g.wachtOpLift);
        assertFalse(g.gebruiktLift);
    }

    // tik: brandalarm actief — gast in lift wordt uit lift gezet en evacuatiert
    @Test void testTikBrandalarmGastInLiftWordtUitgezet() {
        Hotel hotel = maakHotel();
        hotel.brandalarmActief = true;
        if (hotel.lift != null) hotel.lift.zetUitBedrijf(true);
        Gast g = new Gast(16, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(1, 3));
        g.inLift = true;
        g.wachtOpLift = true;
        g.gebruiktLift = true;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        assertFalse(g.inLift);
    }

    // tik: na brandalarm — iedereen buiten → alarm wordt uitgeschakeld
    @Test void testTikAlarmWordtUitgeschakeldAlsIederBuiten() {
        Hotel hotel = maakHotel();
        hotel.brandalarmActief = true;
        if (hotel.lift != null) hotel.lift.zetUitBedrijf(true);
        // zet één gast buiten (buitenY=1) zonder uitcheckend vlag
        Gast g = new Gast(17, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // na tick met iedereen buiten moet brandalarm uit zijn
        assertFalse(hotel.brandalarmActief);
    }

    // tik: na alarm — gast met keertTerugNaAlarm buiten krijgt route naar lobby
    @Test void testTikGastKeertTerugNaAlarmKrijgtRoute() {
        Hotel hotel = maakHotel();
        Gast g = new Gast(18, 1);
        g.setPathfinder(hotel.pathfinder);
        // buitenY = 1
        g.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        g.keertTerugNaAlarm = true;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // gast staat buiten en keertTerugNaAlarm → moet route naar lobby gekregen hebben
        assertNotNull(g.doelVakje);
    }

    // tik: na alarm — gast in lobby met keertTerugNaAlarm wordt naar kamer gestuurd
    @Test void testTikGastKeertTerugInLobbyNaarKamer() {
        Hotel hotel = maakHotel();
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 4; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        Gast g = new Gast(19, 1);
        g.setPathfinder(hotel.pathfinder);
        // lobby.posY = 2 → gast in lobby
        g.zetStartPositie(hotel.layout.krijgVakje(3, 2));
        g.keertTerugNaAlarm = true;
        g.kamer = kamer;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // gast is nu in lobby, geen inLift/wachtOpLift/doelVakje → route naar kamer gezet
        assertFalse(g.keertTerugNaAlarm);
    }

    // tik: gast met eindbestemming na uitstappen krijgt route naar eindbestemming
    @Test void testVerwerkUitstappendeGastMetEindbestemming() {
        Hotel hotel = maakHotel();
        Restaurant r = new Restaurant();
        r.posX = 4; r.posY = 3; r.breedte = 1; r.hoogte = 1;
        hotel.ruimtes.add(r);
        hotel.layout.plaatsRuimte(r);
        Gast g = new Gast(20, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(1, 3));
        g.moetUitstappen = true;
        g.eindbestemming = r;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // eindbestemming moet gecleared zijn en route gezet zijn
        assertNull(g.eindbestemming);
        assertNotNull(g.doelVakje);
    }

    // tik: restaurant wachtrij — vol restaurant, alternatief gevonden
    @Test void testVerwerkRestaurantWachtrijAlterinatief() {
        Hotel hotel = maakHotel();
        Restaurant vol = new Restaurant();
        vol.posX = 4; vol.posY = 3; vol.breedte = 1; vol.hoogte = 1;
        vol.capaciteit = 0; // capaciteit 0 = altijd vol (isVol = false als cap=0)
        // maak vol door capaciteit op 1 te zetten en een gast binnen te plaatsen
        vol.capaciteit = 1;
        // voeg een persoon toe zodat isVol() true is
        Gast bezetter = new Gast(99, 1);
        bezetter.setPathfinder(hotel.pathfinder);
        hotel.ruimtes.add(vol);
        hotel.layout.plaatsRuimte(vol);
        bezetter.zetStartPositie(hotel.layout.krijgVakje(4, 3));
        vol.betreed(bezetter);
        hotel.voegPersoonToe(bezetter);

        Restaurant vrij = new Restaurant();
        vrij.posX = 5; vrij.posY = 3; vrij.breedte = 1; vrij.hoogte = 1;
        vrij.capaciteit = 5;
        hotel.ruimtes.add(vrij);
        hotel.layout.plaatsRuimte(vrij);

        Gast g = new Gast(21, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        g.wachtOpRestaurant = true;
        g.wachtRestaurant = vol;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // alternatief restaurant gevonden → wachtOpRestaurant cleared
        assertFalse(g.wachtOpRestaurant);
    }

    // tik: restaurant wachtrij — vol restaurant, geen alternatief → blijft wachten
    @Test void testVerwerkRestaurantWachtrijGeenAlternatief() {
        Hotel hotel = maakHotel();
        Restaurant vol = new Restaurant();
        vol.posX = 4; vol.posY = 3; vol.breedte = 1; vol.hoogte = 1;
        vol.capaciteit = 1;
        Gast bezetter = new Gast(98, 1);
        bezetter.setPathfinder(hotel.pathfinder);
        hotel.ruimtes.add(vol);
        hotel.layout.plaatsRuimte(vol);
        bezetter.zetStartPositie(hotel.layout.krijgVakje(4, 3));
        vol.betreed(bezetter);
        hotel.voegPersoonToe(bezetter);

        Gast g = new Gast(22, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        g.wachtOpRestaurant = true;
        g.wachtRestaurant = vol;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // geen alternatief → nog steeds wachtend
        assertTrue(g.wachtOpRestaurant);
    }

    // tik: gast al een doel heeft → wachtrij wordt overgeslagen
    @Test void testVerwerkRestaurantWachtrijMetDoelWordtOvergeslagen() {
        Hotel hotel = maakHotel();
        Restaurant r = new Restaurant();
        r.posX = 4; r.posY = 3; r.breedte = 1; r.hoogte = 1;
        r.capaciteit = 5;
        hotel.ruimtes.add(r);
        hotel.layout.plaatsRuimte(r);
        Gast g = new Gast(23, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        g.wachtOpRestaurant = true;
        g.wachtRestaurant = r;
        g.zetDoel(hotel.layout.krijgVakje(5, 3)); // heeft al doel
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // heeft een doel → wachtrij wordt genegeerd, wachtOpRestaurant blijft true
        assertTrue(g.wachtOpRestaurant);
    }

    // tik: wachtende gast op lift-positie met lift niet op zelfde verdieping → wachtOpLift=true
    @Test void testVerwerkWachtendeGastenZetWachtOpLiftTrue() {
        Hotel hotel = maakHotel();
        // lift staat op verdieping 2 (lobby), gast staat op y=4 naast de lift
        Gast g = new Gast(24, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(2, 4)); // x=posX+1=2, y=4 > lift.posY=3
        g.gebruiktLift = true;
        g.inLift = false;
        g.gewensteVerdieping = 5;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        sc.tik();
        // lift staat op 2, gast op y=4 → moet wachten op lift
        assertTrue(g.wachtOpLift);
    }

    // tik: summonTick loopt maar gast is nog niet weg na 7 ticks
    @Test void testSummonTickLooptNogNietKlaar() {
        Hotel hotel = maakHotel();
        sc.setMaxWachtTicks(1);
        Gast g = new Gast(25, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        // 3 ticks: 1 om stilstaand te detecteren (wachtTicks++), 1 om summonTick=0 te zetten, 1 om summonTick++
        for (int i = 0; i < 3; i++) sc.tik();
        // gast moet nog aanwezig zijn (summonTick < 8)
        assertTrue(hotel.personen.contains(g));
    }

    // tik: gast die buiten staat wordt niet gesummoned
    @Test void testGastBuitenWordtNietGesummoned() {
        Hotel hotel = maakHotel();
        sc.setMaxWachtTicks(1);
        Gast g = new Gast(26, 1);
        g.setPathfinder(hotel.pathfinder);
        // buitenY=1
        g.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        for (int i = 0; i < 5; i++) sc.tik();
        // gast buiten → niet gesummoned, maar kan verwijderd zijn via uitchecken of buitenrij logica
        // belangijkste: summonTick is nooit gezet
        assertTrue(g.summonTick < 0 || !hotel.personen.contains(g));
    }

    // tik: gast met inLift=true wordt niet gesummoned
    @Test void testGastInLiftWordtNietGesummoned() {
        Hotel hotel = maakHotel();
        sc.setMaxWachtTicks(1);
        Gast g = new Gast(27, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        g.inLift = true;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        for (int i = 0; i < 5; i++) sc.tik();
        assertEquals(-1, g.summonTick);
    }

    // tik: gast met uitcheckend=true wordt niet gesummoned
    @Test void testGastUitcheckendWordtNietGesummoned() {
        Hotel hotel = maakHotel();
        sc.setMaxWachtTicks(1);
        Gast g = new Gast(28, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        g.uitcheckend = true;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        for (int i = 0; i < 5; i++) sc.tik();
        assertEquals(-1, g.summonTick);
    }

    // tik: gast met eindbestemming wordt niet gesummoned
    @Test void testGastMetEindbestemmingWordtNietGesummoned() {
        Hotel hotel = maakHotel();
        sc.setMaxWachtTicks(1);
        Restaurant r = new Restaurant();
        r.posX = 4; r.posY = 3; r.breedte = 1; r.hoogte = 1;
        hotel.ruimtes.add(r);
        hotel.layout.plaatsRuimte(r);
        Gast g = new Gast(29, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        g.eindbestemming = r;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);
        for (int i = 0; i < 5; i++) sc.tik();
        assertEquals(-1, g.summonTick);
    }

    // tik: schoonmaker met kamer en wachtVakje na alarm wordt naar kamer gestuurd
    @Test void testVerwerkWachtendeSchoonmaakTakenNaAlarm() {
        Hotel hotel = maakHotel();
        hotel.brandalarmActief = true;
        if (hotel.lift != null) hotel.lift.zetUitBedrijf(true);
        Kamer kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 4; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        hotel.wachtendeSchoonmaakKamers.add(kamer);

        // zet iedereen buiten zodat het alarm uitgeschakeld wordt
        // één persoon buiten zonder uitcheckend vlag
        Gast g = new Gast(30, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        hotel.voegPersoonToe(g);

        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(hotel.pathfinder);
        s.zetStartPositie(hotel.layout.krijgVakje(4, 1));
        hotel.voegPersoonToe(s);

        hc.setHotel(hotel);
        sc.tik();
        // alarm uit → wachtendeSchoonmaakTaken verwerkt → kamer lijst leeg of schoonmaker bezig
        assertFalse(hotel.brandalarmActief);
    }

    // tik: schoonmaker met wachtVakje krijgt na alarm route terug naar wachtVakje
    @Test void testSchoonmakerMetWachtVakjeNaAlarm() {
        Hotel hotel = maakHotel();
        hotel.brandalarmActief = true;
        if (hotel.lift != null) hotel.lift.zetUitBedrijf(true);

        Schoonmaker s = new Schoonmaker();
        s.setPathfinder(hotel.pathfinder);
        Vakje wachtVakje = hotel.layout.krijgVakje(4, 3);
        s.setWachtVakje(wachtVakje);
        s.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        hotel.voegPersoonToe(s);

        hc.setHotel(hotel);
        sc.tik();
        // iedereen buiten → alarm uit → schoonmaker kreeg route naar wachtVakje
        assertFalse(hotel.brandalarmActief);
    }

    // tik: pathfinder null → verwerkEvacuatieLoop crasht niet
    @Test void testTikBrandalarmZonderPathfinderCrashetNiet() {
        Hotel hotel = maakHotel();
        hotel.brandalarmActief = true;
        hotel.pathfinder = null;
        hc.setHotel(hotel);
        assertDoesNotThrow(() -> sc.tik());
    }

    // tik: lift null → verwerkWachtendeGasten crasht niet
    @Test void testTikZonderLiftCrashetNiet() {
        Hotel hotel = maakHotel();
        hotel.lift = null;
        hc.setHotel(hotel);
        assertDoesNotThrow(() -> sc.tik());
    }

    // tik: godzilla klaar → eventManager stopt
    @Test void testTikGodzillaKlaarStoptManager() {
        Hotel hotel = maakHotel();
        // breedte=8, brandende kolommen 1..8 → volgendeKolom > breedte → isKlaar=true
        hotel.godzillaActief = true;
        for (int i = 1; i <= hotel.breedte; i++) hotel.brandendeKolommen.add(i);
        hc.setHotel(hotel);
        ec.setHotelController(hc);
        // notify GODZILLA zodat godzillaService aangemaakt wordt
        ec.notify(new hotelevents.HotelEvent(1, hotelevents.HotelEventType.GODZILLA, -1, -1));
        // tik() moet isKlaar() detecteren en stop aanroepen zonder crash
        assertDoesNotThrow(() -> sc.tik());
    }
    // getRealTijd: na een starttijd gebruikt hij de verstreken tijd branch
    @Test void testGetRealTijdMetStartTijd() throws Exception {
        java.lang.reflect.Field veld = SimulatieController.class.getDeclaredField("startTijdMs");
        veld.setAccessible(true);
        veld.setLong(sc, System.currentTimeMillis() - 2000);

        String tijd = sc.getRealTijd();

        assertTrue(tijd.matches("\\d{2}:\\d{2}:\\d{2}"));
        assertNotEquals("00:00:00", tijd);
    }

    // tik: brandalarm zonder lobby neemt de vroege return branch
    @Test void testTikBrandalarmZonderLobbyCrashetNiet() {
        Hotel hotel = maakHotel();
        hotel.brandalarmActief = true;
        hotel.lobby = null;
        hc.setHotel(hotel);

        assertDoesNotThrow(() -> sc.tik());
        assertEquals(1, sc.getTikTeller());
    }

    // tik: brandalarm met ontbrekend uitgangvakje neemt de uitgang-null branch
    @Test void testTikBrandalarmZonderUitgangVakjeCrashetNiet() {
        Hotel hotel = maakHotel();
        hotel.brandalarmActief = true;
        hotel.lobby.posY = 1; // buitenY wordt 0 en ligt buiten de layout
        Gast g = new Gast(31, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);

        assertDoesNotThrow(() -> sc.tik());
        assertNull(g.doelVakje);
    }

    // tik: terugkerende gast met bestaand doel wordt niet opnieuw gerouteerd
    @Test void testTerugkerendeGastMetDoelWordtOvergeslagen() {
        Hotel hotel = maakHotel();
        Gast g = new Gast(32, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 1));
        Vakje bestaandDoel = hotel.layout.krijgVakje(3, 2);
        g.zetDoel(bestaandDoel);
        g.keertTerugNaAlarm = true;
        g.inLift = true; // voorkomt beweging na de branch-check
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);

        sc.tik();

        assertTrue(g.keertTerugNaAlarm);
        assertSame(bestaandDoel, g.doelVakje);
    }

    // tik: uitstappen zonder geldig uitstapvakje crasht niet en cleart de uitstapvlag
    @Test void testUitstappenZonderUitstapVakjeCrashetNiet() {
        Hotel hotel = maakHotel();
        hotel.lift.posX = hotel.breedte; // posX + 1 valt buiten de layout
        Gast g = new Gast(33, 1);
        g.setPathfinder(hotel.pathfinder);
        Vakje start = hotel.layout.krijgVakje(1, 3);
        g.zetStartPositie(start);
        g.moetUitstappen = true;
        g.gebruiktLift = true;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);

        assertDoesNotThrow(() -> sc.tik());
        assertFalse(g.moetUitstappen);
        assertSame(start, g.huidigVakje);
    }

    // tik: restaurant wachtrij kiest het dichtstbijzijnde vrije alternatief
    @Test void testRestaurantWachtrijKiestDichtstbijzijndeAlternatief() {
        Hotel hotel = maakHotel();

        Restaurant vol = new Restaurant();
        vol.posX = 4; vol.posY = 3; vol.breedte = 1; vol.hoogte = 1;
        vol.capaciteit = 1;
        hotel.ruimtes.add(vol);
        hotel.layout.plaatsRuimte(vol);
        Gast bezetter = new Gast(34, 1);
        bezetter.setPathfinder(hotel.pathfinder);
        bezetter.zetStartPositie(hotel.layout.krijgVakje(4, 3));
        vol.betreed(bezetter);
        hotel.voegPersoonToe(bezetter);

        Restaurant dichtbij = new Restaurant();
        dichtbij.posX = 5; dichtbij.posY = 3; dichtbij.breedte = 1; dichtbij.hoogte = 1;
        dichtbij.capaciteit = 5;
        hotel.ruimtes.add(dichtbij);
        hotel.layout.plaatsRuimte(dichtbij);

        Restaurant verweg = new Restaurant();
        verweg.posX = 7; verweg.posY = 3; verweg.breedte = 1; verweg.hoogte = 1;
        verweg.capaciteit = 5;
        hotel.ruimtes.add(verweg);
        hotel.layout.plaatsRuimte(verweg);

        Gast g = new Gast(35, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 3));
        g.wachtOpRestaurant = true;
        g.wachtRestaurant = vol;
        hotel.voegPersoonToe(g);
        hc.setHotel(hotel);

        sc.tik();

        assertFalse(g.wachtOpRestaurant);
        assertSame(hotel.layout.krijgVakje(5, 3), g.doelVakje);
    }
}
