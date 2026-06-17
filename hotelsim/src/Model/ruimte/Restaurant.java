package Model.ruimte;

import Model.events.IEventListener;
import Model.ILogger;
import Model.events.RestaurantEindEvent;
import Model.persoon.Gast;
import Model.GastRoutingService;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Bij NEED_FOOD slaat hij de eindtijd op via registreerGast()
// Bij NONE checkt hij elke tick of gasten klaar zijn
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
    public void registreerGast(int gastId, int tijd) {
        if (eetEindTijden.containsKey(gastId)) return;
        eetEindTijden.put(gastId, tijd + EETDUUR);
        if (logger != null) logger.log("[" + tijd + "] Restaurant: gast " + gastId + " gaat naar restaurant");
    }

    // wordt aangeroepen door EventController als er een library event binnenkomt
    @Override
//
    public void onEvent(HotelEvent event) {
        // als eventype van het event obj gelijk is aan non voer de erin code uit
        if (event.getEventType() == HotelEventType.NONE) {
            //meth gettime oproepen en sla teruggegven getal terug 
            int tijd = event.getTime();

            // maak lege lijst voor de gasten die klaar zijn 
            List<Integer> klaar = new ArrayList<>();
            //loop door elke sleutel waarde paren in de map
            for (Map.Entry<Integer, Integer> entry : eetEindTijden.entrySet()) {

                //gastid is de key
                int gastId = entry.getKey();
                //eindtijd is de waarde 
                int eindTijd = entry.getValue();
                // als de huidige tijd groter/gelijk aan de eindtijd dan klaar en gast toevoegen aan klaar lijst
                if (tijd >= eindTijd) {
                    klaar.add(gastId);
                }
            }

            // loop door alle gastids in de klaarlijst
            for (int gastId : klaar) {
                // vw gast uit de map via gastid, zodat hij niet meer onthouden wordt
                eetEindTijden.remove(gastId);
                // nieuw obj, geeft tijd en gstid mee aan cons alleen gebruikt voor log bericht;
                RestaurantEindEvent eindEvent = new RestaurantEindEvent(tijd, gastId);
                //als de logger niet nu is log;
                if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Restaurant: gast " + eindEvent.getGastId() + " klaar");
                // als gts niet leeg is, gast loopt terug naar kamer 
                if (gastTerugService != null) gastTerugService.stuurTerugNaarKamer(gastId);
            }
        }
    }

    // laat een gast het restaurant betreden
    public void betreedRestaurant() {}

    // laat een gast het restaurant verlaten
    public void verlaatRestaurant() {}

    // controleer of het restaurant vol is op basis van aanwezigen vs capaciteit
    public boolean isVol() {
        return capaciteit > 0 && getAanwezigen().size() >= capaciteit;
    }

    @Override
    public boolean isFaciliteit() { return true; }

    // geef de status van het restaurant terug voor het observatiescherm
    @Override
    public String getStatusTekst() {
        int aanwezig = getAanwezigen().size();
        String vol = "";
        if (capaciteit > 0 && aanwezig >= capaciteit) {
            vol = " [VOL]";
        }
        return "Restaurant (cap " + capaciteit + ") : " + aanwezig + " aanwezig" + vol;
    }
}
