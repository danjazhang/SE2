package Model.events;

import Model.events.InternEvent;

// Intern eind event dat aangemaakt wordt door de Schoonmaker
// als een schoonmaak noodgeval is afgehandeld
public class SchoonmaakEindEvent extends InternEvent {

    // constructor: sla tijdstip en gastnummer op
    public SchoonmaakEindEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}