/*package Model;

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
*/
package Model;

import View.EventLog;
import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;

public class Restaurant extends Ruimte implements HotelEventListener {

    public int capaciteit;
    public Gast gasten;

    public Restaurant() {}

    // 🍽️ guest restaurant’a girer
    public void betreedRestaurant(int guestId) {
       // EventLog.log("Gast " + guestId + " betreedt restaurant");
    }

    // 🚪 guest restaurant’tan çıkar (şimdilik manuel / opsiyonel)
    public void verlaatRestaurant(int guestId) {
        EventLog.log("Gast " + guestId + " verlaat restaurant");
    }

    public boolean isVol() {
        return false;
    }

    @Override
    public void notify(HotelEvent evt) {

        // 🍔 guest yemek ister → restaurant’a gelir
        if (evt.getEventType() == HotelEventType.NEED_FOOD) {

            EventLog.log("[" + evt.getTime() + "] Restaurant: gast "
                    + evt.getGuestId() + " bestelt eten");

            // giriş aksiyonu
            betreedRestaurant(evt.getGuestId());
        }
    }
}