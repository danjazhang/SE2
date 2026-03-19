import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {

    // balielocatie wordt correct opgeslagen via de constructor
    @Test
    void testConstructor() {
        Layout layout = new Layout(5, 5);
        Lobby lobby = new Lobby(layout);
        assertEquals(layout, lobby.balielocatie);
    }

    // lobby erft van Ruimte, posX en posY beginnen op 0
    @Test
    void testErftVanRuimte() {
        Lobby lobby = new Lobby(new Layout(3, 3));
        assertEquals(0, lobby.posX);
        assertEquals(0, lobby.posY);
    }
}
