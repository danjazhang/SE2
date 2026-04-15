package Model;

// Abstracte basisklasse voor alle interne eind events in het hotel
// De eind events zoals FilmEindEvent erven van deze klasse
// Ze worden aangemaakt door de ruimtes zelf als de eindtijd bereikt is
public abstract class InternEvent {

    // tijdstip waarop het event plaatsvindt
    private int tijd;

    // gastnummer van de gast die het event veroorzaakt
    private int gastId;

    // constructor: sla tijdstip en gastnummer op
    public InternEvent(int tijd, int gastId) {
        this.tijd = tijd;
        this.gastId = gastId;
    }

    // geef het tijdstip terug
    public int getTijd() { return tijd; }

    // geef het gastnummer terug
    public int getGastId() { return gastId; }
}
