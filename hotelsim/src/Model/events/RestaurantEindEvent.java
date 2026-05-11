package Model.events;

import Model.events.InternEvent;

// Intern eind event dat aangemaakt wordt door het Restaurant
// als een gast klaar is met eten na een bepaald aantal ticks na NEED_FOOD
public class RestaurantEindEvent extends InternEvent {

    // constructor: sla tijdstip en gastnummer op
    public RestaurantEindEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}
