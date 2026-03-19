import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import hotelevents.HotelEventListener;

public class Restaurant extends Ruimte implements HotelEventListener{
    int capaciteit;
    Gast gasten;

    //constructor
    public Restaurant(){}

    public void betreedRestaurant(){}
    public void verlaatRestaurant(){}
    public void isVol(){}

    @Override
    public void notify(HotelEvent evt) {

        if (evt.getEventType() == HotelEventType.NEED_FOOD) {
            System.out.println("[" + evt.getTime() + "] Restaurant: gast "
                    + evt.getGuestId() + " bestelt eten");
        }
    }
}
