package Model.ruimte;

import Model.events.IEventListener;
import Model.ILogger;
import Model.events.RestaurantEindEvent;
import Model.persoon.Gast;

import Model.GastRoutingService;
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

    // service voor het terugsturen van gasten naar hun kamer
    private GastRoutingService gastTerugService;

    // constructor met logger
    public Restaurant(ILogger logger) {
        this.logger = logger;
        this.eetEindTijden = new HashMap<>();
    }

    // lege constructor voor als er geen logger nodig is (bijv. in testen)
    public Restaurant() {
        this.eetEindTijden = new HashMap<>();
    }

    // stel de terugservice in
    public void setGastTerugService(GastRoutingService gastTerugService) {
        this.gastTerugService = gastTerugService;
    }

    // wordt aangeroepen door GastRoutingService als een gast naar dit restaurant gestuurd wordt
    // registreert de gast en logt dat hij eten bestelt
    public void registreerGast(int gastId, int tijd) {
        if (eetEindTijden.containsKey(gastId)) return;
        int eindTijd = tijd + EETDUUR;
        eetEindTijden.put(gastId, eindTijd);
        if (logger != null) logger.log("[" + tijd + "] Restaurant: gast " + gastId + " gaat naar restaurant");
    }

    // wordt aangeroepen door EventController als er een library event binnenkomt
    @Override
    public void onEvent(HotelEvent event) {
        // NEED_FOOD: wordt afgehandeld via registreerGast() vanuit GastRoutingService
        // zodat alleen het restaurant waar de gast naartoe gestuurd wordt logt
        // NONE: elke tick checkt het restaurant of gasten klaar zijn
        if (event.getEventType() == HotelEventType.NONE) {
            int tijd = event.getTime();
            eetEindTijden.entrySet().removeIf(entry -> {
                if (tijd >= entry.getValue()) {
                    // maak een RestaurantEindEvent aan en log gast klaar
                    RestaurantEindEvent eindEvent = new RestaurantEindEvent(tijd, entry.getKey());
                    if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Restaurant: gast " + eindEvent.getGastId() + " klaar");
                    if (gastTerugService != null) gastTerugService.stuurTerugNaarKamer(eindEvent.getGastId());
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
