import View.dialog.InstellingenDialog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Testklasse voor InstellingenDialog: ik test veilig het resultaatobject,
// zonder dat ik een echte Swing-popup hoef te openen tijdens de test.
public class InstellingenDialogTest {

    // Ik maak een InstellingenResult met vaste waarden; ik verwacht dat snelheid,
    // eventlog-zichtbaarheid en tileSize onveranderd via de getters terugkomen.
    @Test void testInstellingenResultBewaartWaarden() {
        InstellingenDialog.InstellingenResult result =
                new InstellingenDialog.InstellingenResult("Snel", true, 88);

        assertEquals("Snel", result.getSnelheid());
        assertTrue(result.isEventlogZichtbaar());
        assertEquals(88, result.getTileSize());
    }
}
