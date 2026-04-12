package Model;

// Intern event dat aangemaakt wordt als een film eindigt
// Wordt door de Bioscoop zelf aangemaakt via een TickEvent na 120 ticks
public class FilmEindEvent extends InternEvent {
    public FilmEindEvent(int tijd, int gastId) {
        super(tijd, gastId);
    }
}
