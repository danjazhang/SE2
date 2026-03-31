package Model;

import View.EventLog;
import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;

// Stelt het restaurant voor in het hotel
// Erft van Ruimte en reageert op eten events
public class Restaurant extends Ruimte implements HotelEventListener {

    // het maximaal aantal gasten dat het restaurant kan bevatten
    public int capaciteit;

    // de gast die momenteel in het restaurant is
    public Gast gasten;

    // lege constructor
    public Restaurant() {}

    // laat een gast het restaurant betreden
    public void betreedRestaurant() {}

    // laat een gast het restaurant verlaten
    public void verlaatRestaurant() {}

    // controleer of het restaurant vol is
    public void isVol() {}

    // reageer op eten events
    @Override
    public void notify(HotelEvent evt) {
        if (evt.getEventType() == HotelEventType.NEED_FOOD) {
            EventLog.log("[" + evt.getTime() + "] Restaurant: gast " + evt.getGuestId() + " bestelt eten");
        }
    }
}
