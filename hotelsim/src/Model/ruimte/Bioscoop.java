package Model.ruimte;

import Model.events.IEventListener;
import Model.ILogger;
import Model.persoon.Gast;
import Model.events.FilmEindEvent;
import Model.GastRoutingService;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Bij GOTO_CINEMA registreert hij de gast
// Bij START_CINEMA slaat hij de eindtijd op
// Bij NONE checkt hij elke tick of de film klaar is
public class Bioscoop extends Ruimte implements IEventListener {

    // of er momenteel een film bezig is
    public boolean filmBezig;

    // de duur van de huidige film in ticks
    public int filmDuur;

    // de gasten die momenteel in de bioscoop zijn
    public List<Gast> gasten;

    // het tijdstip waarop de huidige film eindigt
    private int filmEindTijd;

    // gastIds van gasten die momenteel in de bioscoop zitten
    private Set<Integer> aanwezigeGastIds = new HashSet<>();

    // een film duurt dit aantal ticks na START_CINEMA
    private static final int FILMDUUR = 40;

    // logger voor het loggen naar de GUI
    private ILogger logger;

    // service voor het terugsturen van gasten naar hun kamer
    private GastRoutingService gastTerugService;

    // constructor met logger
    public Bioscoop(ILogger logger) {
        this.gasten = new ArrayList<>();
        this.filmBezig = false;
        this.filmDuur = 0;
        this.filmEindTijd = 0;
        this.logger = logger;
    }

    // lege constructor voor als er geen logger nodig is (bijv. in testen)
    public Bioscoop() {
        this.gasten = new ArrayList<>();
        this.filmBezig = false;
        this.filmDuur = 0;
        this.filmEindTijd = 0;
    }

    // stel de terugservice in
    public void setGastTerugService(GastRoutingService gastTerugService) {
        this.gastTerugService = gastTerugService;
    }

    // wordt aangeroepen door EventController als er een library event binnenkomt
    @Override
    public void onEvent(HotelEvent event) {
        // GOTO_CINEMA: gast komt binnen, registreer hem en log
        if (event.getEventType() == HotelEventType.GOTO_CINEMA) {
            aanwezigeGastIds.add(event.getGuestId());
            if (logger != null) logger.log("[" + event.getTime() + "] Bioscoop: gast " + event.getGuestId() + " komt binnen");
        }
        // START_CINEMA: film start, sla eindtijd op en log
        else if (event.getEventType() == HotelEventType.START_CINEMA) {
            filmBezig = true;
            filmEindTijd = event.getTime() + FILMDUUR;
            if (logger != null) logger.log("[" + event.getTime() + "] Bioscoop: film start");
        }
        // NONE: elke tick checkt de bioscoop of de film klaar is
        else if (event.getEventType() == HotelEventType.NONE) {
            int tijd = event.getTime();
            if (filmBezig && tijd >= filmEindTijd) {
                filmBezig = false;
                FilmEindEvent eindEvent = new FilmEindEvent(tijd, -1);
                if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Bioscoop: film eindigt");

                // stuur alle aanwezige gasten terug naar hun kamer
                if (gastTerugService != null) {
                    for (int gastId : aanwezigeGastIds) {
                        gastTerugService.stuurTerugNaarKamer(gastId);
                    }
                }
                aanwezigeGastIds.clear();
            }
        }
    }

    // start een film
    public void startFilm() {}

    // stop een film
    public void stopFilm() {}

    // laat een gast de bioscoop betreden
    public void betreedBioscoop() {}

    @Override
    public boolean isFaciliteit() { return true; }

    // geef de status van de bioscoop terug voor het observatiescherm
    @Override
    public String getStatusTekst() {
        String film;
        if (filmBezig) {
            film = "film bezig";
        } else {
            film = "geen film";
        }
        return "Bioscoop : " + getAanwezigen().size() + " aanwezig, " + film;
    }
}
