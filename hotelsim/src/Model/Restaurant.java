package Model;

import java.util.HashMap;
import java.util.Map;

// Stelt het restaurant voor in het hotel
// Erft van Ruimte en implementeert IEventListener
// Het restaurant is verantwoordelijk voor eten logica (single responsibility)
// Gebruikt TickEvent om bij te houden wanneer gasten klaar zijn met eten
public class Restaurant extends Ruimte implements IEventListener {

    // het maximaal aantal gasten dat het restaurant kan bevatten
    public int capaciteit;

    // de gast die momenteel in het restaurant is
    public Gast gasten;

    // bijhoudt wanneer elke gast klaar is met eten: gastId -> eindtijd
    private Map<Integer, Integer> eetEindTijden;

    // een maaltijd duurt 60 ticks
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

    // wordt aangeroepen door EventController als er een intern event binnenkomt
    @Override
    public void onEvent(InternEvent event) {
        // als een gast naar het restaurant gaat, log dat en sla eindtijd op
        if (event instanceof RestaurantStartEvent) {
            int gastId = event.getGastId();
            int eindTijd = event.getTijd() + EETDUUR;
            eetEindTijden.put(gastId, eindTijd);
            if (logger != null) logger.log("[" + event.getTijd() + "] Restaurant: gast " + gastId + " bestelt eten");
        }

        // elke tick checkt het restaurant of gasten klaar zijn met eten
        else if (event instanceof TickEvent) {
            int tijd = event.getTijd();
            // loop door alle gasten en check of ze klaar zijn
            eetEindTijden.entrySet().removeIf(entry -> {
                if (tijd >= entry.getValue()) {
                    if (logger != null) logger.log("[" + tijd + "] Restaurant: gast " + entry.getKey() + " klaar");
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
