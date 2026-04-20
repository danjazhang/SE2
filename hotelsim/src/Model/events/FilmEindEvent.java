package Model.events;

import Model.InternEvent;

// Intern eind event dat aangemaakt wordt door de Bioscoop
// als de film eindigt na een bepaald aantal ticks na START_CINEMA
public class FilmEindEvent extends InternEvent {

    // constructor: sla tijdstip en gastnummer op
    public FilmEindEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}
