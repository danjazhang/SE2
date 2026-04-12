package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Stelt de fitnessruimte voor in het hotel
// Erft van Ruimte en implementeert IEventListener
// De fitnessruimte is verantwoordelijk voor sport logica (single responsibility)
// Gebruikt TickEvent om bij te houden wanneer gasten klaar zijn met sporten
public class Fitnessruimte extends Ruimte implements IEventListener {

    // de gasten die momenteel in de fitnessruimte zijn
    public List<Gast> gasten;

    // bijhoudt wanneer elke gast klaar is met sporten: gastId -> eindtijd
    private Map<Integer, Integer> sportEindTijden;

    // een fitness sessie duurt 60 ticks
    private static final int SPORTDUUR = 20;

    // logger voor het loggen naar de GUI
    private ILogger logger;

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

    // wordt aangeroepen door EventController als er een intern event binnenkomt
    @Override
    public void onEvent(InternEvent event) {
        // als een gast naar de fitness gaat, log dat en sla eindtijd op
        if (event instanceof FitnessStartEvent) {
            int gastId = event.getGastId();
            int eindTijd = event.getTijd() + SPORTDUUR;
            sportEindTijden.put(gastId, eindTijd);
            if (logger != null) logger.log("[" + event.getTijd() + "] Fitness: gast " + gastId + " gaat sporten");
        }

        // elke tick checkt de fitnessruimte of gasten klaar zijn met sporten
        else if (event instanceof TickEvent) {
            int tijd = event.getTijd();
            // loop door alle gasten en check of ze klaar zijn
            sportEindTijden.entrySet().removeIf(entry -> {
                if (tijd >= entry.getValue()) {
                    if (logger != null) logger.log("[" + tijd + "] Fitness: gast " + entry.getKey() + " klaar");
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
