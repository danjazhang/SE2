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

// Verantwoordelijkheid: bijhouden wanneer gasten klaar zijn met eten en hen daarna terugsturen.
// Bij NEED_FOOD slaat het restaurant de eindtijd op via registreerGast().
// Bij NONE checkt het restaurant elke tick of er gasten klaar zijn met eten.
// Restaurant erft van Ruimte en implementeert IEventListener.
public class Restaurant extends Ruimte implements IEventListener {

    // Het maximaal aantal gasten dat het restaurant kan bevatten.
    public int capaciteit;

    // De gast die momenteel in het restaurant is (enkelvoudig, legacy veld).
    public Gast gasten;

    // Een map die per gast bijhoudt wanneer hij klaar is met eten.
    // 'Map<Integer, Integer>' betekent: sleutel is gastId (int), waarde is eindtijd (int).
    private Map<Integer, Integer> eetEindTijden;

    // 'private static final' betekent: dit getal is voor alle Restaurants hetzelfde en verandert nooit.
    // Een maaltijd duurt 20 ticks.
    private static final int EETDUUR = 20;

    // Logger voor het sturen van berichten naar de GUI.
    private ILogger logger;

    // Service voor het terugsturen van gasten naar hun kamer nadat ze klaar zijn.
    private GastRoutingService gastTerugService;

    // Constructor met logger: sla de logger op en maak een lege eetEindTijden-map aan.
    public Restaurant(ILogger logger) {
        this.logger = logger;
        this.eetEindTijden = new HashMap<>();
    }

    // Lege constructor voor als er geen logger nodig is, bijvoorbeeld in tests.
    public Restaurant() {
        this.eetEindTijden = new HashMap<>();
    }

    // Sla de gastTerugService op zodat we gasten na het eten kunnen terugsturen.
    public void setGastTerugService(GastRoutingService gastTerugService) {
        this.gastTerugService = gastTerugService;
    }

    // Registreer een gast: sla zijn eindtijd op als huidige tijd plus de eetduur.
    // 'eetEindTijden.containsKey(gastId)' betekent: als de gast al in de map staat, sla hem dan niet dubbel op.
    public void registreerGast(int gastId, int tijd) {
        if (eetEindTijden.containsKey(gastId)) return;
        // 'eetEindTijden.put(gastId, tijd + EETDUUR)' betekent: sla op dat gastId klaar is op tijdstip tijd + 20.
        eetEindTijden.put(gastId, tijd + EETDUUR);
        if (logger != null) logger.log("[" + tijd + "] Restaurant: gast " + gastId + " gaat naar restaurant");
    }

    // '@Override' betekent: deze methode vervangt onEvent() van de interface IEventListener.
    // Wordt elke tick aangeroepen door EventController via het NONE event.
    @Override
    public void onEvent(HotelEvent event) {
        // Alleen bij NONE: dit is het tick-signaal, elke tick controleren we de eetEindTijden.
        if (event.getEventType() == HotelEventType.NONE) {

            // Haal het huidige tijdstip op uit het event en sla op als lokale variabele tijd.
            int tijd = event.getTime();

            // Maak een lege lijst aan om de gastIds in op te slaan die klaar zijn met eten.
            List<Integer> klaar = new ArrayList<>();

            // Loop door alle sleutel-waarde paren in de map.
            // 'entry.getKey()' is de gastId (de sleutel), 'entry.getValue()' is de eindtijd (de waarde).
            for (Map.Entry<Integer, Integer> entry : eetEindTijden.entrySet()) {
                int gastId = entry.getKey();
                int eindTijd = entry.getValue();
                // Als de huidige tijd groter is dan of gelijk is aan (>=) de eindtijd, is de gast klaar.
                if (tijd >= eindTijd) {
                    klaar.add(gastId);
                }
            }

            // Verwerk elke klare gast: verwijder hem uit de map, log het, en stuur hem terug naar zijn kamer.
            for (int gastId : klaar) {
                eetEindTijden.remove(gastId);
                RestaurantEindEvent eindEvent = new RestaurantEindEvent(tijd, gastId);
                if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Restaurant: gast " + eindEvent.getGastId() + " klaar");
                if (gastTerugService != null) gastTerugService.stuurTerugNaarKamer(gastId);
            }
        }
    }

    // Lege methoden als placeholders.
    public void betreedRestaurant() {}
    public void verlaatRestaurant() {}
    public void isVol() {}
}
