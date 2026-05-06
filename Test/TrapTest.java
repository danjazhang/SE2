import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor Trap: ik test trapinstellingen en veilig gebruik.
public class TrapTest {

    // Ik maak een Trap met tijd per verdieping; ik verwacht dat die waarde opgeslagen is.
    @Test void testConstructor() {
        Trap t = new Trap(5);
        assertEquals(5, t.tijdperverdieping);
    }

    // Ik maak een Trap; ik verwacht dat hij Ruimte-attributen zoals posX en posY heeft.
    @Test void testErftVanRuimte() {
        Trap t = new Trap(2);
        assertEquals(0, t.posX);
        assertEquals(0, t.posY);
    }

    // Ik roep gebruikTrap aan; ik verwacht geen exception.
    @Test void testGebruikTrapCrashetNiet() {
        assertDoesNotThrow(() -> new Trap(2).gebruikTrap(null));
    }
}
