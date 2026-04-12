package Model;

// Intern event dat aangemaakt wordt als de library GOTO_FITNESS stuurt
// De Fitnessruimte reageert op dit event
public class FitnessStartEvent extends InternEvent {
    public FitnessStartEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}
