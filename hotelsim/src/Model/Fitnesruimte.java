package Model;

import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;
import java.util.ArrayList;
import java.util.List;

public class Fitnesruimte extends Ruimte implements HotelEventListener {
    public List<Gast> gasten;

    public Fitnesruimte() {
        this.gasten = new ArrayList<>();
    }

    public void breedteFitness() {}
    public void verlaatFitness() {}

    @Override
    public void notify(HotelEvent evt) {
        if (evt.getEventType() == HotelEventType.GOTO_FITNESS) {
            System.out.println("[" + evt.getTime() + "] Fitness: gast " + evt.getGuestId() + " gaat sporten");
        }
    }
}
