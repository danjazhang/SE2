package Model.events;

// Abstracte basisklasse voor alle interne eind events in het hotel
// Ze worden aangemaakt door de ruimtes zelf als de eindtijd bereikt is
public abstract class InternEvent {

    // variabele die het tijdstip opslaat
    private int tijd;

    // variable die het gastid opslaat
    private int gastId;

    // constructor: slaat meegegeven t en g op in de variabele van dit object
    public InternEvent(int tijd, int gastId) {
        this.tijd = tijd;
        this.gastId = gastId;
    }

    // geef het tijdstip terug
    public int getTijd() { return tijd; }

    // geef het gastnummer terug
    public int getGastId() { return gastId; }
}