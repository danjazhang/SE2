package Model;

import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;
import java.util.ArrayList;
import java.util.List;

// Stelt de fitnessruimte voor in het hotel
// Erft van Ruimte en reageert op fitness events
public class Fitnesruimte extends Ruimte implements HotelEventListener {

    // de gasten die momenteel in de fitnessruimte zijn
    public List<Gast> gasten;

    // constructor: fitnessruimte begint zonder gasten
    public Fitnesruimte() {
        this.gasten = new ArrayList<>();
    }

    // laat een gast sporten
    public void breedteFitness() {}

    // laat een gast de fitnessruimte verlaten
    public void verlaatFitness() {}

    // reageer op fitness events
    @Override
    public void notify(HotelEvent evt) {
        if (evt.getEventType() == HotelEventType.GOTO_FITNESS) {
            System.out.println("[" + evt.getTime() + "] Fitness: gast " + evt.getGuestId() + " gaat sporten");
        }
    }
}
