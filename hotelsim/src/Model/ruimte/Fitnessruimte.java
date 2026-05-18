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
// Bij NONE checkt hij elke tick of gasten klaar zijn en maakt FitnessEindEvent aan
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
        // GOTO_FITNESS: een gast gaat sporten, log dat en sla eindtijd op
        if (event.getEventType() == HotelEventType.GOTO_FITNESS) {
            int gastId = event.getGuestId();
            int eindTijd = event.getTime() + SPORTDUUR;
            sportEindTijden.put(gastId, eindTijd);
            if (logger != null) logger.log("[" + event.getTime() + "] Fitness: gast " + gastId + " gaat sporten");
        }
        // NONE: elke tick checkt de fitnessruimte of gasten klaar zijn
        else if (event.getEventType() == HotelEventType.NONE) {
            int tijd = event.getTime();
            sportEindTijden.entrySet().removeIf(entry -> {
                if (tijd >= entry.getValue()) {
                    // maak een FitnessEindEvent aan en log gast klaar
                    FitnessEindEvent eindEvent = new FitnessEindEvent(tijd, entry.getKey());
                    if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Fitness: gast " + eindEvent.getGastId() + " klaar");
                    if (gastTerugService != null) gastTerugService.stuurTerugNaarKamer(eindEvent.getGastId());
                    return true;
                }
                return false;
            });
        }
    }

    // laat een gast sporten
    public void breedteFitness() {}

    // laat een gast de fitnessruimte verlaten
    public void verlaatFitness() {}
}
