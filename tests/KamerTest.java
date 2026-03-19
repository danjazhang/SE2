import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KamerTest {

    @Test
    void testKamerConstructor() {
        Kamer kamer = new Kamer();
        //kamer is schoon en heeft geen gast
        assertTrue(kamer.schoon);
        assertNull(kamer.Gast);
    }
}
