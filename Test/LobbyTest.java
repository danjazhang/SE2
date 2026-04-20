import Model.ruimte.Lobby;
import Model.Hotel;
import Model.layout.Layout;
import Model.ruimte.Kamer;
import Model.persoon.Gast;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {

    @Test void testConstructor() {
        Lobby l = new Lobby(1, 1, 2, 2, 1, 1, null, null);
        assertEquals(1, l.getBalieX());
        assertEquals(1, l.getBalieY());
    }

    @Test void testErftVanRuimte() {
        Lobby l = new Lobby(2, 3, 4, 5, 1, 1, null, null);
        assertEquals(2, l.posX);
        assertEquals(3, l.posY);
        assertEquals(4, l.breedte);
        assertEquals(5, l.hoogte);
    }

    @Test void testCheckInMaaktGastAan() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        Kamer kamer = new Kamer();
        kamer.posX = 2; kamer.posY = 2; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Lobby lobby = new Lobby(1, 5, 3, 1, 2, 5, hotel, null);
        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        assertEquals(1, hotel.personen.size());
    }

    @Test void testCheckInKoppeltKamer() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        Kamer kamer = new Kamer();
        kamer.posX = 2; kamer.posY = 2; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Lobby lobby = new Lobby(1, 5, 3, 1, 2, 5, hotel, null);
        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        assertTrue(kamer.isBezet());
    }

    @Test void testCheckOutOntkoppeltGast() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        Kamer kamer = new Kamer();
        kamer.posX = 2; kamer.posY = 2; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        Lobby lobby = new Lobby(1, 5, 3, 1, 2, 5, hotel, null);
        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        lobby.onEvent(new HotelEvent(2, HotelEventType.CHECK_OUT, 1, -1));
        assertFalse(kamer.isBezet());
        assertFalse(kamer.isSchoon());
    }

    @Test void testCheckOutZonderGastCrashetNiet() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        Lobby lobby = new Lobby(1, 5, 3, 1, 2, 5, hotel, null);
        assertDoesNotThrow(() -> lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_OUT, 99, -1)));
    }

    @Test void testLoggerWordtAangeroepen() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        boolean[] logged = {false};
        Lobby lobby = new Lobby(1, 5, 3, 1, 2, 5, hotel, bericht -> logged[0] = true);
        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        assertTrue(logged[0]);
    }
}
