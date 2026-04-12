package Model;

// Intern event dat aangemaakt wordt als de library NEED_FOOD stuurt
// Het Restaurant reageert op dit event
public class RestaurantStartEvent extends InternEvent {
    public RestaurantStartEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}

