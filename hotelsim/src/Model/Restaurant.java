package Model;

import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;

public class Restaurant extends Ruimte implements HotelEventListener {
    public int capaciteit;
    public Gast gasten;

    public Restaurant() {}

    public void betreedRestaurant() {}
    public void verlaatRestaurant() {}
    public void isVol() {}

    @Override
    public void notify(HotelEvent evt) {
        if (evt.getEventType() == HotelEventType.NEED_FOOD) {
            System.out.println("[" + evt.getTime() + "] Restaurant: gast " + evt.getGuestId() + " bestelt eten");
        }
    }
}
