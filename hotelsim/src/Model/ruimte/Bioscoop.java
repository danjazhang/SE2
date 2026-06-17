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

    // een film duurt dit aantal ticks na START_CINEMA — instelbaar via setFilmDuur()
    private int filmDuurTicks = 40;

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
        //als het eventtype gelijk is aan gotocinema
        if (event.getEventType() == HotelEventType.GOTO_CINEMA) {
            //voeg het gastid toe aan de set aanwezigegastids
            aanwezigeGastIds.add(event.getGuestId());
            if (logger != null) logger.log("[" + event.getTime() + "] Bioscoop: gast " + event.getGuestId() + " komt binnen");
        }
        // anders als eventtype gelijk is aan startcinema
        else if (event.getEventType() == HotelEventType.START_CINEMA) {
            // zet filmbezig op true
            filmBezig = true;
            //bereken de eindtijd, huidige tick plus de filmduur
            filmEindTijd = event.getTime() + filmDuurTicks;
            if (logger != null) logger.log("[" + event.getTime() + "] Bioscoop: film start");
        }
        // anders als eventtype gelijk is aan non
        else if (event.getEventType() == HotelEventType.NONE) {
            int tijd = event.getTime();
            //als filmbezig is en tijd groter of gelijk is aan filmeindtijd
            if (filmBezig && tijd >= filmEindTijd) {
                filmBezig = false;
                // Maak een intern FilmEindEvent object aan alleen voor het logbericht. Log
                FilmEindEvent eindEvent = new FilmEindEvent(tijd, -1);
                if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Bioscoop: film eindigt");

                // Als de gastTerugService bestaat
                if (gastTerugService != null) {
                    // loop  door alle gastIds in de set
                    for (int gastId : aanwezigeGastIds) {
                        //stuur elke gast terug naar zijn kamer
                        gastTerugService.stuurTerugNaarKamer(gastId);
                    }
                }
                aanwezigeGastIds.clear();
            }
        }
    }

    // stel de filmduur in — standaard 40 ticks
    public void setFilmDuur(int duur) { this.filmDuurTicks = duur; }

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
