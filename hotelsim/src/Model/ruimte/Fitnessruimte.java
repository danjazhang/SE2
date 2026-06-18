package Model.ruimte;

import Model.events.FitnessEindEvent;
import Model.events.IEventListener;
import Model.ILogger;
import Model.persoon.Gast;
import Model.GastRoutingService;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Verantwoordelijkheid: bijhouden wanneer gasten klaar zijn met sporten en hen daarna terugsturen.
// Bij GOTO_FITNESS slaat de fitnessruimte de eindtijd op.
// Bij NONE checkt de fitnessruimte elke tick of er gasten klaar zijn.
// Fitnessruimte erft van Ruimte en implementeert IEventListener.
public class Fitnessruimte extends Ruimte implements IEventListener {

    // Lijst van gasten die momenteel in de fitnessruimte zijn.
    public List<Gast> gasten;

    // Een map die per gast bijhoudt wanneer hij klaar is met sporten.
    // 'Map<Integer, Integer>' betekent: sleutel is gastId (int), waarde is eindtijd (int).
    private Map<Integer, Integer> sportEindTijden;

    // 'private static final' betekent: dit getal is voor alle Fitnessruimtes hetzelfde en verandert nooit.
    // Een fitness sessie duurt 20 ticks.
    private static final int SPORTDUUR = 20;

    // Logger voor het sturen van berichten naar de GUI.
    private ILogger logger;

    // Service voor het terugsturen van gasten naar hun kamer nadat ze klaar zijn.
    private GastRoutingService gastTerugService;

    // Constructor met logger: maak lege lijsten aan en sla de logger op.
    public Fitnessruimte(ILogger logger) {
        this.gasten = new ArrayList<>();
        this.logger = logger;
        this.sportEindTijden = new HashMap<>();
    }

    // Lege constructor voor als er geen logger nodig is, bijvoorbeeld in tests.
    public Fitnessruimte() {
        this.gasten = new ArrayList<>();
        this.sportEindTijden = new HashMap<>();
    }

    // Sla de gastTerugService op zodat we gasten na het sporten kunnen terugsturen.
    public void setGastTerugService(GastRoutingService gastTerugService) {
        this.gastTerugService = gastTerugService;
    }

    // '@Override' betekent: deze methode vervangt onEvent() van de interface IEventListener.
    // Wordt aangeroepen door EventController bij elk binnenkomend event.
    @Override
    public void onEvent(HotelEvent event) {

        // GOTO_FITNESS: een gast gaat sporten. Sla zijn eindtijd op en log het.
        // 'sportEindTijden.put(gastId, event.getTime() + SPORTDUUR)' betekent:
        // sla op dat gastId klaar is op tijdstip huidigetijd plus 20.
        if (event.getEventType() == HotelEventType.GOTO_FITNESS) {
            int gastId = event.getGuestId();
            sportEindTijden.put(gastId, event.getTime() + SPORTDUUR);
            if (logger != null) logger.log("[" + event.getTime() + "] Fitness: gast " + gastId + " gaat sporten");
        }

        // NONE: elke tick controleren we de sportEindTijden.
        else if (event.getEventType() == HotelEventType.NONE) {
            // Haal het huidige tijdstip op uit het event.
            int tijd = event.getTime();

            // Maak een lege lijst voor gasten die klaar zijn met sporten.
            List<Integer> klaar = new ArrayList<>();

            // Loop door alle sleutel-waarde paren in de map.
            // 'entry.getKey()' is de gastId (de sleutel), 'entry.getValue()' is de eindtijd (de waarde).
            for (Map.Entry<Integer, Integer> entry : sportEindTijden.entrySet()) {
                int gastId = entry.getKey();
                int eindTijd = entry.getValue();
                // Als de huidige tijd groter is dan of gelijk is aan (>=) de eindtijd, is de gast klaar.
                if (tijd >= eindTijd) {
                    klaar.add(gastId);
                }
            }

            // Verwerk elke klare gast: verwijder hem uit de map, log het, en stuur hem terug naar zijn kamer.
            for (int gastId : klaar) {
                sportEindTijden.remove(gastId);
                FitnessEindEvent eindEvent = new FitnessEindEvent(tijd, gastId);
                if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Fitness: gast " + eindEvent.getGastId() + " klaar");
                if (gastTerugService != null) gastTerugService.stuurTerugNaarKamer(gastId);
            }
        }
    }

    // Lege methoden als placeholders.
    public void breedteFitness() {}
    public void verlaatFitness() {}

    @Override
    public boolean isFaciliteit() { return true; }

    // geef de status van de fitnessruimte terug voor het observatiescherm
    @Override
    public String getStatusTekst() {
        return "Fitness : " + getAanwezigen().size() + " aanwezig";
    }
}
