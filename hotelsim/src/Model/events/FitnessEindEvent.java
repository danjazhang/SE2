package Model.events;

import Model.events.InternEvent;

// Intern eind event dat aangemaakt wordt door de Fitnessruimte
// als een gast klaar is met sporten na een bepaald aantal ticks na GOTO_FITNESS
public class FitnessEindEvent extends InternEvent {

    // constructor: sla tijdstip en gastnummer op
    public FitnessEindEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}
