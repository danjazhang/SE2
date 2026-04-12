package Model;

// Intern event dat aangemaakt wordt als een gast klaar is met eten
// Wordt door het Restaurant zelf aangemaakt via een TickEvent na 60 ticks
public class RestaurantEindEvent extends InternEvent {
    public RestaurantEindEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}
