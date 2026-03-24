import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class KlokTest {

    // een nieuwe klok moet op tijd 0 beginnen
    @Test
    void testBegintOpNul() {
        Klok klok = new Klok();
        assertEquals(0, klok.huidigeTijd);
    }

    // één tick moet de tijd met 1 verhogen
    @Test
    void testTickVerhoogtTijd() {
        Klok klok = new Klok();
        klok.tick();
        assertEquals(1, klok.huidigeTijd);
    }

    // drie ticks achter elkaar moet tijd 3 geven
    @Test
    void testMeerdereTicks() {
        Klok klok = new Klok();
        klok.tick();
        klok.tick();
        klok.tick();
        assertEquals(3, klok.huidigeTijd);
    }

    // na reset moet de tijd weer 0 zijn, ook na meerdere ticks
    @Test
    void testReset() {
        Klok klok = new Klok();
        klok.tick();
        klok.tick();
        klok.reset();
        assertEquals(0, klok.huidigeTijd);
    }

    // krijgTijd moet dezelfde waarde teruggeven als huidigeTijd
    @Test
    void testKrijgTijd() {
        Klok klok = new Klok();
        klok.tick();
        assertEquals(1, klok.krijgTijd());
    }

    // standaard tickDuur moet 1 zijn
    @Test
    void testTickDuurStandaard() {
        Klok klok = new Klok();
        assertEquals(1, klok.tickDuur);
    }
}
