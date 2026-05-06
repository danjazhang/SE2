package Model.ruimte;

import Model.events.IEventListener;
import Model.ILogger;
import Model.events.RestaurantEindEvent;
import Model.persoon.Gast;

import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import java.util.HashMap;
import java.util.Map;



// Bij NEED_FOOD slaat hij de eindtijd op
// Bij NONE checkt hij elke tick of gasten klaar zijn en maakt RestaurantEindEvent aan
public class Restaurant extends Ruimte implements IEventListener {

    // het maximaal aantal gasten dat het restaurant kan bevatten
    public int capaciteit;

    // de gast die momenteel in het restaurant is
    public Gast gasten;

    // bijhoudt wanneer elke gast klaar is met eten: gastId -> eindtijd
    private Map<Integer, Integer> eetEindTijden;

    // een maaltijd duurt dit aantal ticks
    private static final int EETDUUR = 20;

    // logger voor het loggen naar de GUI
    private ILogger logger;

    // constructor met logger
    public Restaurant(ILogger logger) {
        this.logger = logger;
        this.eetEindTijden = new HashMap<>();
    }

    // lege constructor voor als er geen logger nodig is (bijv. in testen)
    public Restaurant() {
        this.eetEindTijden = new HashMap<>();
    }

    // wordt aangeroepen door EventController als er een library event binnenkomt
    @Override
    public void onEvent(HotelEvent event) {
        // NEED_FOOD: een gast gaat eten, log dat en sla eindtijd op
        if (event.getEventType() == HotelEventType.NEED_FOOD) {
            int gastId = event.getGuestId();
            int eindTijd = event.getTime() + EETDUUR;
            eetEindTijden.put(gastId, eindTijd);
            if (logger != null) logger.log("[" + event.getTime() + "] Restaurant: gast " + gastId + " bestelt eten");
        }
        // NONE: elke tick checkt het restaurant of gasten klaar zijn
        else if (event.getEventType() == HotelEventType.NONE) {
            int tijd = event.getTime();
            eetEindTijden.entrySet().removeIf(entry -> {
                if (tijd >= entry.getValue()) {
                    // maak een RestaurantEindEvent aan en log gast klaar
                    RestaurantEindEvent eindEvent = new RestaurantEindEvent(tijd, entry.getKey());
                    if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Restaurant: gast " + eindEvent.getGastId() + " klaar");
                    return true;
                }
                return false;
            });
        }
    }

    // laat een gast het restaurant betreden
    public void betreedRestaurant() {}

    // laat een gast het restaurant verlaten
    public void verlaatRestaurant() {}

    // controleer of het restaurant vol is
    public void isVol() {}
}