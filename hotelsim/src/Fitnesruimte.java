import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import hotelevents.HotelEventListener;
import java.util.ArrayList;
import java.util.List;

public class Fitnesruimte extends Ruimte implements HotelEventListener {
    //arraylist gasten
    List<Gast> gasten;

    //constructor
    public Fitnesruimte(){
        this.gasten = new ArrayList<>();
    }

    public void breedteFitness(){}
    public void verlaatFitness(){}

    @Override
    public void notify(HotelEvent evt) {

        if (evt.getEventType() == HotelEventType.GOTO_FITNESS) {
            System.out.println("[" + evt.getTime() + "] Fitness: gast "
                    + evt.getGuestId() + " gaat sporten");
        }
    }
}
