import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Model.*;

public class ScenarioTest {

    // een nieuw scenario moet een lege lijst hebben
    @Test
    void testConstructorLeeg() {
        Scenario scenario = new Scenario();
        assertTrue(scenario.gebeurtenissen.isEmpty());
    }

    // na toevoegen moet de lijst één gebeurtenis bevatten
    @Test
    void testVoegGebeurtenisToe() {
        Scenario scenario = new Scenario();
        Gebeurtenis g = new Gebeurtenis(5, "checkin");
        scenario.voegGebeurtenisToe(g);
        assertEquals(1, scenario.gebeurtenissen.size());
    }

    // krijgGebeurtenissen moet alleen de gebeurtenis op het juiste tijdstip teruggeven
    @Test
    void testKrijgGebeurtenissenOpTijd() {
        Scenario scenario = new Scenario();
        scenario.voegGebeurtenisToe(new Gebeurtenis(10, "checkin"));
        scenario.voegGebeurtenisToe(new Gebeurtenis(20, "schoonmaak"));

        assertEquals(1, scenario.krijgGebeurtenissen(10).size());
        assertEquals("checkin", scenario.krijgGebeurtenissen(10).get(0).type);
    }

    // bij een tijdstip zonder gebeurtenissen moet de lijst leeg zijn
    @Test
    void testKrijgGebeurtenissenLeegOpOnbekendTijd() {
        Scenario scenario = new Scenario();
        scenario.voegGebeurtenisToe(new Gebeurtenis(10, "checkin"));

        assertTrue(scenario.krijgGebeurtenissen(99).isEmpty());
    }

    // meerdere gebeurtenissen op hetzelfde tijdstip moeten allemaal teruggegeven worden
    @Test
    void testMeerdereGebeurtenissenOpZelfdeTijd() {
        Scenario scenario = new Scenario();
        scenario.voegGebeurtenisToe(new Gebeurtenis(5, "checkin"));
        scenario.voegGebeurtenisToe(new Gebeurtenis(5, "schoonmaak"));

        assertEquals(2, scenario.krijgGebeurtenissen(5).size());
    }
}
