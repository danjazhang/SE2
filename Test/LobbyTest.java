import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Trap;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {

    private Hotel hotel;
    private Kamer kamer;
    private Lobby lobby;

    // =========================================================
    // SETUP: minimale hotelconfiguratie voor alle tests
    // =========================================================
    @BeforeEach
    void setUp() {

        // ik doe dit: ik maak een nieuw hotel + basis layout + ruimtes (lift, trap, kamer, lobby)
        // ik verwacht: dat elke test start met een volledig werkende hotelomgeving zonder afhankelijkheden

        hotel = new Hotel();
        hotel.layout = new Layout(6, 4);
        hotel.breedte = 6;
        hotel.hoogte = 4;

        Lift lift = new Lift(hotel);
        lift.posX = 1;
        lift.posY = 1;
        lift.breedte = 1;
        lift.hoogte = 4;

        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);
        trap.posX = 6;
        trap.posY = 1;
        trap.breedte = 1;
        trap.hoogte = 4;

        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        kamer = new Kamer();
        kamer.posX = 3;
        kamer.posY = 1;
        kamer.breedte = 1;
        kamer.hoogte = 1;
        kamer.sterren = 1;

        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        hotel.pathfinder = new Pathfinder(hotel);

        lobby = new Lobby(2, 4, 3, 1, 3, 4, hotel, null);
        hotel.lobby = lobby;
        hotel.ruimtes.add(lobby);
        hotel.layout.plaatsRuimte(lobby);
    }

    // =========================================================
    // CONSTRUCTOR TEST
    // =========================================================

    // ik doe dit: ik maak een nieuwe Lobby met vaste waarden
    // ik verwacht: dat alle properties correct worden opgeslagen (positie, grootte en baliepositie)
    @Test
    void testConstructor() {

        Lobby l = new Lobby(1, 2, 3, 4, 5, 6, hotel, null);

        assertEquals(1, l.posX);
        assertEquals(2, l.posY);
        assertEquals(3, l.breedte);
        assertEquals(4, l.hoogte);
    }

    // =========================================================
    // CHECK-IN BRANCH: kamer beschikbaar
    // =========================================================

    // ik doe dit: ik stuur een CHECK_IN event naar de lobby
    // ik verwacht: dat er een gast wordt aangemaakt en toegevoegd aan het hotel
    @Test
    void testCheckInMaaktGastAan() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));

        assertEquals(1, hotel.personen.size());
        assertTrue(hotel.personen.get(0) instanceof Gast);
    }

    // ik doe dit: ik check een gast in
    // ik verwacht: dat de kamer direct bezet wordt gezet
    @Test
    void testCheckInZetKamerBezet() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));

        assertTrue(kamer.isBezet());
    }

    // ik doe dit: ik check een gast in
    // ik verwacht: dat de gast gekoppeld wordt aan een kamer
    @Test
    void testCheckInGastKrijgtKamer() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));

        Gast g = (Gast) hotel.personen.get(0);
        assertEquals(kamer, g.kamer);
    }

    // =========================================================
    // CHECK-IN BRANCH: geen kamer beschikbaar
    // =========================================================

    // ik doe dit: ik blokkeer alle kamers zodat geen enkele geldig is
    // ik verwacht: dat er geen gast wordt aangemaakt omdat geen kamer beschikbaar is
    @Test
    void testCheckInGeenKamer() {

        kamer.koppelGast(new Gast(999, 1));
        kamer.schoon = false;

        Kamer tweede = new Kamer();
        tweede.sterren = 1;
        tweede.koppelGast(new Gast(1000, 1));
        hotel.ruimtes.add(tweede);

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 2, 1));

        assertEquals(0, hotel.personen.size());
    }

    // =========================================================
    // CHECK-OUT BRANCH
    // =========================================================

    // ik doe dit: ik check een gast in en daarna uit
    // ik verwacht: dat de kamer weer vrij wordt
    @Test
    void testCheckOutMaaktKamerVrij() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        lobby.onEvent(new HotelEvent(2, HotelEventType.CHECK_OUT, 1, -1));

        assertFalse(kamer.isBezet());
    }

    // ik doe dit: ik check een gast uit
    // ik verwacht: dat de kamer vuil wordt na checkout
    @Test
    void testCheckOutMaaktKamerVies() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        lobby.onEvent(new HotelEvent(2, HotelEventType.CHECK_OUT, 1, -1));

        assertFalse(kamer.isSchoon());
    }

    // ik doe dit: ik check een gast uit
    // ik verwacht: dat de gast als uitcheckend gemarkeerd wordt
    @Test
    void testCheckOutZetUitcheckend() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        Gast g = (Gast) hotel.personen.get(0);

        lobby.onEvent(new HotelEvent(2, HotelEventType.CHECK_OUT, 1, -1));

        assertTrue(g.uitcheckend);
    }

    // =========================================================
    // SAFE BRANCHES (edge cases zonder crash)
    // =========================================================

    // ik doe dit: ik stuur een checkout voor een niet-bestaande gast
    // ik verwacht: dat er geen crash optreedt
    @Test
    void testCheckOutZonderGastGeenCrash() {

        assertDoesNotThrow(() ->
                lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_OUT, 999, -1))
        );
    }

    // ik doe dit: ik stuur een onbekend event type
    // ik verwacht: dat het genegeerd wordt
    @Test
    void testAnderEventWordtGenegeerd() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.EVACUATE, -1, -1));

        assertEquals(0, hotel.personen.size());
    }

    // =========================================================
    // LOGGER BRANCH
    // =========================================================

    // ik doe dit: ik geef een logger mee en trigger check-in
    // ik verwacht: dat de logger wordt aangeroepen
    @Test
    void testLoggerWordtAangeroepen() {

        boolean[] log = {false};

        Lobby l = new Lobby(2, 4, 3, 1, 3, 4, hotel, msg -> log[0] = true);

        l.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));

        assertTrue(log[0]);
    }

    // =========================================================
    // BETREED BRANCH
    // =========================================================

    // ik doe dit: ik laat een uitcheckende gast de lobby betreden
    // ik verwacht: dat de gast verwijderd wordt uit het hotel
    @Test
    void testUitcheckGastWordtVerwijderd() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        Gast g = (Gast) hotel.personen.get(0);

        lobby.onEvent(new HotelEvent(2, HotelEventType.CHECK_OUT, 1, -1));

        int before = hotel.personen.size();

        lobby.betreed(g);

        assertEquals(before - 1, hotel.personen.size());
    }

    // ik doe dit: ik laat een normale gast de lobby betreden
    // ik verwacht: dat de gast NIET verwijderd wordt
    @Test
    void testNormaleGastBlijftBestaan() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        Gast g = (Gast) hotel.personen.get(0);

        lobby.betreed(g);

        assertTrue(hotel.personen.contains(g));
    }
    // ik doe dit: ik vraag een 1-ster kamer terwijl alleen een 2-ster kamer vrij is
    // ik verwacht: dat de lobby de hogere kamer als fallback gebruikt
    @Test
    void testCheckInGebruiktHogereSterrenAlsFallback() {

        kamer.sterren = 2;

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));

        Gast g = (Gast) hotel.personen.get(0);
        assertSame(kamer, g.kamer);
    }

    // ik doe dit: ik zet het balievakje buiten de layout en check een gast uit
    // ik verwacht: dat de fallback-route naar de lobby zelf gebruikt wordt
    @Test
    void testCheckOutZonderBalieVakjeGebruiktLobbyFallback() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        Gast g = (Gast) hotel.personen.get(0);

        Lobby l = new Lobby(2, 4, 3, 1, 99, 99, hotel, null);
        hotel.lobby = l;

        assertDoesNotThrow(() -> l.onEvent(new HotelEvent(2, HotelEventType.CHECK_OUT, 1, -1)));
        assertTrue(g.uitcheckend);
        assertNotNull(g.doelVakje);
    }

    // ik doe dit: ik check een gast zonder kamer uit
    // ik verwacht: dat de logger de algemene checkout-branch gebruikt
    @Test
    void testCheckOutGastZonderKamerLogtAlgemeen() {

        StringBuilder log = new StringBuilder();
        Lobby l = new Lobby(2, 4, 3, 1, 3, 4, hotel, log::append);
        Gast g = new Gast(77, 1);
        g.setPathfinder(hotel.pathfinder);
        g.zetStartPositie(hotel.layout.krijgVakje(3, 4));
        hotel.voegPersoonToe(g);

        l.onEvent(new HotelEvent(2, HotelEventType.CHECK_OUT, 77, -1));

        assertTrue(log.toString().contains("checkt uit"));
        assertFalse(log.toString().contains("kamer"));
    }

    // ik doe dit: ik vervang de logger via setLogger
    // ik verwacht: dat de nieuwe logger wordt gebruikt
    @Test
    void testSetLoggerWordtGebruikt() {

        StringBuilder log = new StringBuilder();
        lobby.setLogger(log::append);

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));

        assertTrue(log.length() > 0);
    }

    // ik doe dit: uitcheckende gast betreedt lobby maar staat niet op balievakje
    // ik verwacht: dat hij nog niet verwijderd wordt
    @Test
    void testUitcheckGastNietOpBalieBlijftBestaan() {

        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        Gast g = (Gast) hotel.personen.get(0);
        lobby.onEvent(new HotelEvent(2, HotelEventType.CHECK_OUT, 1, -1));
        g.huidigVakje = hotel.layout.krijgVakje(2, 4);

        lobby.betreed(g);

        assertTrue(hotel.personen.contains(g));
    }
}
