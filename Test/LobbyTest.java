import Model.ruimte.Lobby;
import Model.Hotel;
import Model.layout.Layout;
import Model.ruimte.Kamer;
import Model.persoon.Gast;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Lobby: ik test check-in, check-out en de koppeling met Hotel/Kamer.
public class LobbyTest {

    // Ik maak een Lobby met baliepositie; ik verwacht dat balieX en balieY goed opgeslagen zijn.
    @Test void testConstructor() {
        Lobby l = new Lobby(1, 1, 2, 2, 1, 1, null, null);
        assertEquals(1, l.getBalieX());
        assertEquals(1, l.getBalieY());
    }

    // Ik maak een Lobby als Ruimte; ik verwacht dat positie en afmetingen goed staan.
    @Test void testErftVanRuimte() {
        Lobby l = new Lobby(2, 3, 4, 5, 1, 1, null, null);
        assertEquals(2, l.posX);
        assertEquals(3, l.posY);
        assertEquals(4, l.breedte);
        assertEquals(5, l.hoogte);
    }

    // Ik stuur CHECK_IN naar Lobby; ik verwacht dat er een Gast in het Hotel komt.
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

    // Ik stuur CHECK_IN met een vrije Kamer; ik verwacht dat de Kamer bezet wordt.
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

    // Ik check een Gast in en uit; ik verwacht dat de Kamer leeg en vies wordt.
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

    // Ik stuur CHECK_OUT voor een onbekende Gast; ik verwacht geen exception.
    @Test void testCheckOutZonderGastCrashetNiet() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        Lobby lobby = new Lobby(1, 5, 3, 1, 2, 5, hotel, null);
        assertDoesNotThrow(() -> lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_OUT, 99, -1)));
    }

    // Ik stuur CHECK_IN met een logger; ik verwacht dat Lobby een logbericht schrijft.
    @Test void testLoggerWordtAangeroepen() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(5, 5);
        boolean[] logged = {false};
        Lobby lobby = new Lobby(1, 5, 3, 1, 2, 5, hotel, bericht -> logged[0] = true);
        lobby.onEvent(new HotelEvent(1, HotelEventType.CHECK_IN, 1, 1));
        assertTrue(logged[0]);
    }
}
