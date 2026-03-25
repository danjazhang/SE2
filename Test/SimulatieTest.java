import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;
import Controller.*;

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
        // checkin gebeurtenis verwerken mag niet crashen
        assertDoesNotThrow(() -> sim.verwerkGebeurtenis(new Gebeurtenis(1, "checkin")));
    }

    @Test
    void testVerwerkGebeurtenisSchoonmaak() {
        Simulatie sim = new Simulatie(new Hotel(), new Scenario(), new Klok());
        // schoonmaak gebeurtenis verwerken mag niet crashen
        assertDoesNotThrow(() -> sim.verwerkGebeurtenis(new Gebeurtenis(2, "schoonmaak")));
    }

    @Test
    void testVerwerkGebeurtenisbrandalarm() {
        Simulatie sim = new Simulatie(new Hotel(), new Scenario(), new Klok());
        // brandalarm gebeurtenis verwerken mag niet crashen
        assertDoesNotThrow(() -> sim.verwerkGebeurtenis(new Gebeurtenis(3, "brandalarm")));
    }

    // start() loopt 100 ticks door zonder te crashen
    @Test
    void testStartCrashetNiet() {
        Hotel hotel = new Hotel();
        Scenario scenario = new Scenario();
        Klok klok = new Klok();
        // voeg een paar gebeurtenissen toe aan het scenario
        scenario.voegGebeurtenisToe(new Gebeurtenis(1, "checkin"));
        scenario.voegGebeurtenisToe(new Gebeurtenis(5, "schoonmaak"));
        Simulatie sim = new Simulatie(hotel, scenario, klok);
        // start() loopt 100 tijdstappen door en verwerkt alle gebeurtenissen
        assertDoesNotThrow(() -> sim.start());
    }

    // na start() moet de klok op 100 staan want er zijn 100 ticks
    @Test
    void testStartVerhoogtKlok() {
        Hotel hotel = new Hotel();
        Scenario scenario = new Scenario();
        Klok klok = new Klok();
        Simulatie sim = new Simulatie(hotel, scenario, klok);
        sim.start();
        // na 100 ticks staat de klok op 100
        assertEquals(100, klok.huidigeTijd);
    }
}
