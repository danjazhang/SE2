import Model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {

    private Lobby maakLobby() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 8);
        hotel.breedte = 6;
        hotel.hoogte = 8;
        ILogger logger = bericht -> {};
        return new Lobby(1, 1, 2, 2, 3, 4, hotel, logger);
    }

    @Test
    void testConstructor() {
        Lobby lobby = maakLobby();
        assertEquals(3, lobby.getBalieX());
        assertEquals(4, lobby.getBalieY());
    }

    @Test
    void testErftVanRuimte() {
        Lobby lobby = maakLobby();
        assertEquals(1, lobby.posX);
        assertEquals(1, lobby.posY);
    }

    @Test
    void testAfmetingen() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(6, 8);
        hotel.breedte = 6;
        hotel.hoogte = 8;
        ILogger logger = bericht -> {};
        Lobby lobby = new Lobby(1, 1, 4, 5, 1, 1, hotel, logger);
        assertEquals(4, lobby.breedte);
        assertEquals(5, lobby.hoogte);
    }

    @Test
    void testToonStatusSchermCrashetNiet() {
        Lobby lobby = maakLobby();
        assertDoesNotThrow(() -> lobby.toonStatusScherm());
    }

    @Test
    void testBetreedEnVerlaat() {
        Lobby lobby = maakLobby();
        Persoon p = new Persoon();
        lobby.betreed(p);
        assertEquals(1, lobby.getAanwezigen().size());
        lobby.verlaat(p);
        assertEquals(0, lobby.getAanwezigen().size());
    }

    @Test
    void testSetLogger() {
        Lobby lobby = maakLobby();
        assertDoesNotThrow(() -> lobby.setLogger(bericht -> {}));
    }
}
