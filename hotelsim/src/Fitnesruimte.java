import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import hotelevents.HotelEventListener;

public class Fitnesruimte extends Ruimte implements HotelEventListener {
    //arraylist gasten

    //constructor
    public Fitnesruimte(){}

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
