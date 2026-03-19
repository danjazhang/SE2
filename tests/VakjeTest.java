import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VakjeTest {
    @Test
    void testVoegPersoonToe() {
        Vakje vakje = new Vakje();
        Gast gast = new Gast(3);
        vakje.voegPersoonToe(gast);
        //verwacht 1 persoon op vakje
        assertEquals(1, vakje.krijgPersonen().size());
    }

    @Test
    void testVerwijderPersoon() {
        Vakje vakje = new Vakje();
        Gast gast = new Gast(3);
        vakje.voegPersoonToe(gast);
        vakje.verwijderPersoon(gast);
        //verwacht geen persoon
        assertEquals(0, vakje.krijgPersonen().size());
    }

}
