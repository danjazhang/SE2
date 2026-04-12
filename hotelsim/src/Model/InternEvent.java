package Model;

// Abstracte basisklasse voor alle interne events in het hotel
// Dit zijn ONZE eigen events, niet die van de library
// De EventController maakt deze aan op basis van library events en stuurt ze door
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

