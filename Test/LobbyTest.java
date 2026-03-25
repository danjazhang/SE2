import Model.Lobby;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {

    // balieX en balieY worden correct opgeslagen via de constructor
    @Test
    void testConstructor() {
        Lobby lobby = new Lobby(1, 1, 2, 2, 3, 4);
        assertEquals(3, lobby.getBalieX());
        assertEquals(4, lobby.getBalieY());
    }

    // lobby erft van Ruimte, posX en posY worden correct opgeslagen
    @Test
    void testErftVanRuimte() {
        Lobby lobby = new Lobby(2, 3, 2, 2, 1, 1);
        assertEquals(2, lobby.posX);
        assertEquals(3, lobby.posY);
    }

    // breedte en hoogte worden correct opgeslagen
    @Test
    void testAfmetingen() {
        Lobby lobby = new Lobby(1, 1, 4, 5, 1, 1);
        assertEquals(4, lobby.breedte);
        assertEquals(5, lobby.hoogte);
    }
}
