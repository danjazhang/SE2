package Model;

// Event dat elke tick aangemaakt wordt op basis van het NONE event van de library
// Elke ruimte gebruikt dit om bij te houden wanneer activiteiten eindigen
// Bioscoop, Restaurant en Fitnessruimte reageren op dit event
public class TickEvent extends InternEvent {

    // constructor: sla het huidige tijdstip op
    public TickEvent(int tijd) {
        super(tijd, -1);
    }
}
