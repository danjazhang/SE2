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

// Bij GOTO_FITNESS slaat hij de eindtijd op
// Bij NONE checkt hij elke tick of gasten klaar zijn
public class Fitnessruimte extends Ruimte implements IEventListener {

    // de gasten die momenteel in de fitnessruimte zijn
    public List<Gast> gasten;

    // bijhoudt wanneer elke gast klaar is met sporten: gastId -> eindtijd
    private Map<Integer, Integer> sportEindTijden;

    // een fitness sessie duurt dit aantal ticks
    private static final int SPORTDUUR = 20;

    // logger voor het loggen naar de GUI
    private ILogger logger;

    // service voor het terugsturen van gasten naar hun kamer
    private GastRoutingService gastTerugService;

    // constructor met logger
    public Fitnessruimte(ILogger logger) {
        this.gasten = new ArrayList<>();
        this.logger = logger;
        this.sportEindTijden = new HashMap<>();
    }

    // lege constructor voor als er geen logger nodig is (bijv. in testen)
    public Fitnessruimte() {
        this.gasten = new ArrayList<>();
        this.sportEindTijden = new HashMap<>();
    }

    // stel de terugservice in
    public void setGastTerugService(GastRoutingService gastTerugService) {
        this.gastTerugService = gastTerugService;
    }

    // wordt aangeroepen door EventController als er een library event binnenkomt
    @Override
    public void onEvent(HotelEvent event) {
        // GOTO_FITNESS: gast gaat sporten, sla eindtijd op en log
        if (event.getEventType() == HotelEventType.GOTO_FITNESS) {
            int gastId = event.getGuestId();
            sportEindTijden.put(gastId, event.getTime() + SPORTDUUR);
            if (logger != null) logger.log("[" + event.getTime() + "] Fitness: gast " + gastId + " gaat sporten");
        }
        // NONE: elke tick checkt de fitnessruimte of gasten klaar zijn
        else if (event.getEventType() == HotelEventType.NONE) {
            //sla huidige tijdstip van event op als tijd
            int tijd = event.getTime();

            // maak lege lijst voor gasten die klaar zijn met sporten
            List<Integer> klaar = new ArrayList<>();

            //loop door de sleutel-waarde paren in de map
            for (Map.Entry<Integer, Integer> entry : sportEindTijden.entrySet()) {
                
                //gastid is sleutel
                int gastId = entry.getKey();
                //eindtijd is waarde
                int eindTijd = entry.getValue();
                if (tijd >= eindTijd) {
                    klaar.add(gastId);
                }
            }

            // verwerk elke klare gast: verwijder uit lijst, log en stuur terug
            for (int gastId : klaar) {
                sportEindTijden.remove(gastId);
                FitnessEindEvent eindEvent = new FitnessEindEvent(tijd, gastId);
                if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Fitness: gast " + eindEvent.getGastId() + " klaar");
                if (gastTerugService != null) gastTerugService.stuurTerugNaarKamer(gastId);
            }
        }
    }

    // laat een gast sporten
    public void breedteFitness() {}

    // laat een gast de fitnessruimte verlaten
    public void verlaatFitness() {}
}
