import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class LobbyTest {

    // controleer dat balie positie correct wordt opgeslagen
    @Test
    void testConstructor() {
        Lobby lobby = new Lobby(0, 0, 10, 10, 1, 1);
        assertEquals(1, lobby.getBalieX());
        assertEquals(1, lobby.getBalieY());
    }

    // lobby erft van Ruimte, posX en posY beginnen op de meegegeven waarden
    @Test
    void testErftVanRuimte() {
        Lobby lobby = new Lobby(2, 3, 5, 5, 1, 1);
        assertEquals(2, lobby.posX);
        assertEquals(3, lobby.posY);
    }

    // toonStatusScherm mag niet crashen
    @Test
    void testToonStatusScherm() {
        Lobby lobby = new Lobby(0, 0, 10, 10, 1, 1);
        assertDoesNotThrow(() -> lobby.toonStatusScherm());
    }
}
