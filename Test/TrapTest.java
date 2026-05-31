import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrapTest {

    // Ik maak een nieuwe trap aan; ik verwacht dat de tijd per verdieping correct wordt opgeslagen.
    @Test void testConstructor() {
        Trap t = new Trap(5);
        assertEquals(5, t.tijdperverdieping);
    }

    // Ik gebruik de trap als subklasse van Ruimte; ik verwacht dat de standaard positie op 0,0 staat.
    @Test void testErftVanRuimte() {
        Trap t = new Trap(2);
        assertEquals(0, t.posX);
        assertEquals(0, t.posY);
    }

    // Ik laat iemand de trap gebruiken met null als invoer; ik verwacht dat dit geen crash geeft.
    @Test void testGebruikTrapCrashetNiet() {
        assertDoesNotThrow(() -> new Trap(2).gebruikTrap(null));
    }
}
