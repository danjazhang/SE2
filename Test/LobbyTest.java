import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
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

    // maak een hotel met lift, trap, kamer en lobby voor elke test
    @BeforeEach
    void setUp() {
        hotel = new Hotel();
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

        kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        hotel.pathfinder = new Pathfinder(hotel);

        lobby = new Lobby(2, 4, 3, 1, 3, 4, hotel, null);
        hotel.lobby = lobby;
        hotel.ruimtes.add(lobby);
        hotel.layout.plaatsRuimte(lobby);
    }

    // constructor: balieX en balieY worden correct ingesteld
    @Test void testConstructor() {
        Lobby l = new Lobby(1, 1, 2, 2, 3, 4, null, null);
        assertEquals(3, l.getBalieX());
        assertEquals(4, l.getBalieY());
    }

    // erft van Ruimte: posX en posY zijn correct
    @Test void testErftVanRuimte() {
        Lobby l = new Lobby(2, 3, 4, 5, 1, 1, null, null);
        assertEquals(2, l.posX);
        assertEquals(3, l.posY);
        assertEquals(4, l.breedte);
        assertEquals(5, l.hoogte);
    }

    // checkIn: gast wordt aangemaakt en toegevoegd aan hotel
    @Test void testCheckInMaaktGastAan() {
        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        assertEquals(1, hotel.personen.size());
    }

    // checkIn: kamer wordt bezet na check-in
    @Test void testCheckInZetKamerBezet() {
        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        assertTrue(kamer.isBezet());
    }

    // checkIn: gast krijgt route naar kamer
    @Test void testCheckInZetRouteOpGast() {
        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        assertNotNull(hotel.personen.get(0).doelVakje);
    }

    // checkOut: kamer wordt vrijgemaakt na check-out
    @Test void testCheckOutOntkoppeltGast() {
        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        lobby.onEvent(new HotelEvent(2, HotelEventType.CHECK_OUT, 1, -1));
        assertFalse(kamer.isBezet());
    }

    // checkOut: kamer is vies na check-out
    @Test void testCheckOutMaaktKamerVies() {
        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        lobby.onEvent(new HotelEvent(2, HotelEventType.CHECK_OUT, 1, -1));
        assertFalse(kamer.isSchoon());
    }


    // checkOut: geen crash als gast niet bestaat
    @Test void testCheckOutZonderGastCrashetNiet() {
        assertDoesNotThrow(() -> lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_OUT, 99, -1)));
    }

    // logger: wordt aangeroepen bij check-in
    @Test void testLoggerWordtAangeroepen() {
        boolean[] logged = {false};
        Lobby l = new Lobby(2, 4, 3, 1, 3, 4, hotel, bericht -> logged[0] = true);
        l.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        assertTrue(logged[0]);
    }

    // setLogger: logger wordt correct ingesteld
    @Test void testSetLogger() {
        assertDoesNotThrow(() -> lobby.setLogger(bericht -> {}));
    }

    // onEvent: andere events worden genegeerd
    @Test void testAndereEventWordtGenegeerd() {
        assertDoesNotThrow(() -> lobby.onEvent(new HotelEvent(1, HotelEventType.EVACUATE, -1, -1)));
        assertEquals(0, hotel.personen.size());
    }
}
