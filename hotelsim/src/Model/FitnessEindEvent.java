package Model;

// Intern event dat aangemaakt wordt als een fitness sessie eindigt
// Wordt door de Fitnessruimte zelf aangemaakt via een TickEvent na 60 ticks
public class FitnessEindEvent extends InternEvent {
    public FitnessEindEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}

