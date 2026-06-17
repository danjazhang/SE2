import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Trap
public class TrapTest {

    // constructor: tijdPerVerdieping correct ingesteld
    @Test void testConstructor() {
        Trap t = new Trap(3);
        assertEquals(3, t.tijdperverdieping);
    }

    // setTijdPerVerdieping: waarde wordt bijgewerkt
    @Test void testSetTijdPerVerdieping() {
        Trap t = new Trap(2);
        t.setTijdPerVerdieping(5);
        assertEquals(5, t.tijdperverdieping);
    }

    // setTijdPerVerdieping: waarde 1 is geldig
    @Test void testSetTijdPerVerdieping1() {
        Trap t = new Trap(1);
        t.setTijdPerVerdieping(1);
        assertEquals(1, t.tijdperverdieping);
    }

    // gebruikTrap: geen crash
    @Test void testGebruikTrapGeenCrash() {
        assertDoesNotThrow(() -> new Trap(2).gebruikTrap(null));
    }

    // erft van Ruimte: posX en posY beginnen op 0
    @Test void testErftVanRuimte() {
        Trap t = new Trap(3);
        assertEquals(0, t.posX);
        assertEquals(0, t.posY);
    }

    // tijdPerVerdieping 0 is technisch geldig
    @Test void testTijdPerVerdieping0() {
        Trap t = new Trap(0);
        assertEquals(0, t.tijdperverdieping);
    }
}
