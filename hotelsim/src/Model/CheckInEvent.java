package Model;

// Intern event dat aangemaakt wordt als de library CHECK_IN stuurt
// De Lobby reageert op dit event
public class CheckInEvent extends InternEvent {
    public CheckInEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}