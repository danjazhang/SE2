package Model;

// Intern event dat aangemaakt wordt als de library CHECK_OUT stuurt
// De Lobby reageert op dit event
public class CheckOutEvent extends InternEvent {
    public CheckOutEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}


