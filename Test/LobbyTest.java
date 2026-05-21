import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
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
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        // maak een nieuw hotel aan
        hotel = new Hotel();

        // maak layout van hotel aan
        hotel.layout = new Layout(6, 4);

        // stel grootte van hotel in
        hotel.breedte = 6;
        hotel.hoogte = 4;

        // =====================================================
        // LIFT
        // =====================================================

        // maak lift aan
        Lift lift = new Lift(hotel);

        // stel positie en grootte van lift in
        lift.posX = 1;
        lift.posY = 1;
        lift.breedte = 1;
        lift.hoogte = 4;

        // voeg lift toe aan hotel
        hotel.lift = lift;

        // voeg lift toe aan lijst met ruimtes
        hotel.ruimtes.add(lift);

        // plaats lift in layout
        hotel.layout.plaatsRuimte(lift);

        // =====================================================
        // TRAP
        // =====================================================

        // maak trap aan
        Trap trap = new Trap(2);

        // stel positie en grootte van trap in
        trap.posX = 6;
        trap.posY = 1;
        trap.breedte = 1;
        trap.hoogte = 4;

        // voeg trap toe aan hotel
        hotel.trap = trap;

        // voeg trap toe aan lijst met ruimtes
        hotel.ruimtes.add(trap);

        // plaats trap in layout
        hotel.layout.plaatsRuimte(trap);

        // =====================================================
        // KAMER
        // =====================================================

        // maak een kamer aan
        kamer = new Kamer();

        // stel positie en grootte van kamer in
        kamer.posX = 3;
        kamer.posY = 1;
        kamer.breedte = 1;
        kamer.hoogte = 1;

        // voeg kamer toe aan hotel
        hotel.ruimtes.add(kamer);

        // plaats kamer in layout
        hotel.layout.plaatsRuimte(kamer);

        // =====================================================
        // PATHFINDER
        // =====================================================

        // maak pathfinder aan zodat routes berekend kunnen worden
        hotel.pathfinder = new Pathfinder(hotel);

        // =====================================================
        // LOBBY
        // =====================================================

        // maak lobby aan
        lobby = new Lobby(
                2,
                4,
                3,
                1,
                3,
                4,
                hotel,
                null
        );

        // voeg lobby toe aan hotel
        hotel.lobby = lobby;

        // voeg lobby toe aan ruimtes
        hotel.ruimtes.add(lobby);

        // plaats lobby in layout
        hotel.layout.plaatsRuimte(lobby);
    }

    // =========================================================
    // CONSTRUCTOR TEST
    // =========================================================

    @Test
    void testConstructor() {

        // maak nieuwe lobby aan
        Lobby l = new Lobby(1, 2, 3, 4, 5, 6, hotel, null);

        // controleer positie van lobby
        assertEquals(1, l.posX);
        assertEquals(2, l.posY);

        // controleer grootte van lobby
        assertEquals(3, l.breedte);
        assertEquals(4, l.hoogte);

        // controleer positie van balie
        assertEquals(5, l.getBalieX());
        assertEquals(6, l.getBalieY());
    }

    // =========================================================
    // CHECK IN TESTS
    // =========================================================

    @Test
    void testCheckInMaaktGastAan() {

        // voer check-in event uit
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // controleer of er nu 1 persoon in hotel zit
        assertEquals(1, hotel.personen.size());

        // controleer of de persoon een gast is
        assertTrue(hotel.personen.get(0) instanceof Gast);
    }

    @Test
    void testCheckInZetKamerBezet() {

        // voer check-in uit
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // controleer of kamer bezet is
        assertTrue(kamer.isBezet());
    }

    @Test
    void testCheckInGastHeeftKamer() {

        // voer check-in uit
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // haal gast uit hotel
        Gast gast = (Gast) hotel.personen.get(0);

        // controleer of gast gekoppeld is aan kamer
        assertEquals(kamer, gast.kamer);
    }

    @Test
    void testCheckInGastHeeftRoute() {

        // voer check-in uit
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // haal gast op
        Gast gast = (Gast) hotel.personen.get(0);

        // controleer of gast een route heeft gekregen
        assertNotNull(gast.doelVakje);
    }

    @Test
    void testCheckInWanneerKamerAlBezetIs() {

        // maak eerst een gast aan
        Gast bestaandeGast = new Gast(99,1);

        // koppel gast aan kamer zodat kamer bezet is
        kamer.koppelGast(bestaandeGast);

        // voer nieuwe check-in uit
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // haal nieuwe gast op
        Gast nieuweGast = (Gast) hotel.personen.get(0);

        // controleer dat nieuwe gast geen kamer kreeg
        assertNull(nieuweGast.kamer);

        // controleer dat kamer nog steeds bezet is
        assertTrue(kamer.isBezet());
    }

    // =========================================================
    // CHECK OUT TESTS
    // =========================================================

    @Test
    void testCheckOutMaaktKamerVrij() {

        // check gast eerst in
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // check gast daarna uit
        lobby.onEvent(new HotelEvent(
                2,
                HotelEventType.CHECK_OUT,
                1,
                -1
        ));

        // controleer dat kamer niet meer bezet is
        assertFalse(kamer.isBezet());
    }

    @Test
    void testCheckOutMaaktKamerVies() {

        // check gast in
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // check gast uit
        lobby.onEvent(new HotelEvent(
                2,
                HotelEventType.CHECK_OUT,
                1,
                -1
        ));

        // controleer dat kamer vies is
        assertFalse(kamer.isSchoon());
    }

    @Test
    void testCheckOutGastWordtUitcheckend() {

        // check gast in
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // haal gast op
        Gast gast = (Gast) hotel.personen.get(0);

        // check gast uit
        lobby.onEvent(new HotelEvent(
                2,
                HotelEventType.CHECK_OUT,
                1,
                -1
        ));

        // controleer dat gast gemarkeerd is als uitcheckend
        assertTrue(gast.uitcheckend);
    }

    @Test
    void testCheckOutGastKrijgtRouteNaarLobby() {

        // check gast in
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // haal gast op
        Gast gast = (Gast) hotel.personen.get(0);

        // check gast uit
        lobby.onEvent(new HotelEvent(
                2,
                HotelEventType.CHECK_OUT,
                1,
                -1
        ));

        // controleer dat gast route kreeg naar lobby
        assertNotNull(gast.doelVakje);
    }

    @Test
    void testCheckOutRoeptSchoonmakerAan() {

        // maak schoonmaker aan
        Schoonmaker s = new Schoonmaker();

        // geef schoonmaker een startpositie
        s.zetStartPositie(hotel.layout.krijgVakje(3, 4));

        // voeg schoonmaker toe aan hotel
        hotel.voegPersoonToe(s);

        // check gast in
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // check gast uit
        lobby.onEvent(new HotelEvent(
                2,
                HotelEventType.CHECK_OUT,
                1,
                -1
        ));

        // controleer dat schoonmaker bezig is
        assertTrue(s.bezig);

        // controleer dat juiste kamer toegewezen werd
        assertEquals(kamer, s.kamer);
    }

    @Test
    void testCheckOutZonderGastCrashtNiet() {

        // controleer dat checkout zonder bestaande gast geen fout geeft
        assertDoesNotThrow(() ->
                lobby.onEvent(new HotelEvent(
                        1,
                        HotelEventType.CHECK_OUT,
                        999,
                        -1
                ))
        );
    }

    // =========================================================
    // LOGGER TESTS
    // =========================================================

    @Test
    void testLoggerWordtAangeroepenBijCheckIn() {

        // boolean array zodat lambda waarde kan aanpassen
        boolean[] logged = {false};

        // maak lobby met logger
        Lobby l = new Lobby(
                2,
                4,
                3,
                1,
                3,
                4,
                hotel,
                bericht -> logged[0] = true
        );

        // voer check-in uit
        l.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // controleer dat logger werd aangeroepen
        assertTrue(logged[0]);
    }

    @Test
    void testSetLogger() {

        // controleer dat logger zetten geen fout geeft
        assertDoesNotThrow(() ->
                lobby.setLogger(bericht -> {})
        );
    }

    // =========================================================
    // EVENT TESTS
    // =========================================================

    @Test
    void testAnderEventWordtGenegeerd() {

        // stuur evacuatie event
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.EVACUATE,
                -1,
                -1
        ));

        // controleer dat geen personen toegevoegd zijn
        assertEquals(0, hotel.personen.size());
    }

    // =========================================================
    // BETREED TESTS
    // =========================================================

    @Test
    void testUitcheckendeGastWordtVerwijderdUitHotel() {

        // check gast in
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // haal gast op
        Gast gast = (Gast) hotel.personen.get(0);

        // check gast uit
        lobby.onEvent(new HotelEvent(
                2,
                HotelEventType.CHECK_OUT,
                1,
                -1
        ));

        // sla aantal personen op
        int aantalVoor = hotel.personen.size();

        // laat gast lobby betreden
        lobby.betreed(gast);

        // controleer dat gast verwijderd werd
        assertEquals(aantalVoor - 1, hotel.personen.size());
    }

    @Test
    void testNormaleGastWordtNietVerwijderdBijBetreed() {

        // check gast in
        lobby.onEvent(new HotelEvent(
                1,
                HotelEventType.CHECK_IN,
                1,
                1
        ));

        // haal gast op
        Gast gast = (Gast) hotel.personen.get(0);

        // laat gast lobby betreden zonder uitchecken
        lobby.betreed(gast);

        // controleer dat gast nog steeds bestaat
        assertTrue(hotel.personen.contains(gast));
    }
}