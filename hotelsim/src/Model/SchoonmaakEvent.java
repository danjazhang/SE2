package Model;

// Intern event dat aangemaakt wordt als de library CLEANING_EMERGENCY stuurt
// De Schoonmaker reageert op dit event
public class SchoonmaakEvent extends InternEvent {
    public SchoonmaakEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}
