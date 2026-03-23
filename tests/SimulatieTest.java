import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimulatieTest {

    // hotel, scenario en klok worden correct opgeslagen via de constructor
    @Test
    void testConstructor() {
        Hotel hotel = new Hotel();
        Scenario scenario = new Scenario();
        Klok klok = new Klok();

        Simulatie sim = new Simulatie(hotel, scenario, klok);

        assertEquals(hotel, sim.hotel);
        assertEquals(scenario, sim.scenario);
        assertEquals(klok, sim.klok);
    }

    // verwerkGebeurtenis mag niet crashen bij een geldig type
    @Test
    void testVerwerkGebeurtenisCheckin() {
        Simulatie sim = new Simulatie(new Hotel(), new Scenario(), new Klok());
        assertDoesNotThrow(() -> sim.verwerkGebeurtenis(new Gebeurtenis(1, "checkin")));
    }

    @Test
    void testVerwerkGebeurtenisSchoonmaak() {
        Simulatie sim = new Simulatie(new Hotel(), new Scenario(), new Klok());
        assertDoesNotThrow(() -> sim.verwerkGebeurtenis(new Gebeurtenis(2, "schoonmaak")));
    }

    @Test
    void testVerwerkGebeurtenisbrandalarm() {
        Simulatie sim = new Simulatie(new Hotel(), new Scenario(), new Klok());
        assertDoesNotThrow(() -> sim.verwerkGebeurtenis(new Gebeurtenis(3, "brandalarm")));
    }
}
