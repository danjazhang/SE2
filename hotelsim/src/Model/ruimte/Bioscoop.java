package Model.ruimte;

import Model.IEventListener;
import Model.ILogger;
import Model.persoon.Gast;
import Model.events.FilmEindEvent;

import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import java.util.ArrayList;
import java.util.List;

// Stelt de bioscoop voor in het hotel
// Erft van Ruimte en implementeert IEventListener
// De bioscoop is verantwoordelijk voor film logica (single responsibility)
// Bij GOTO_CINEMA logt hij dat een gast binnenkomt
// Bij START_CINEMA slaat hij de eindtijd op
// Bij NONE checkt hij elke tick of de film eindigt en maakt FilmEindEvent aan
public class Bioscoop extends Ruimte implements IEventListener {

    // of er momenteel een film bezig is
    public boolean filmBezig;

    // de duur van de huidige film in ticks
    public int filmDuur;

    // de gasten die momenteel in de bioscoop zijn
    public List<Gast> gasten;

    // het tijdstip waarop de huidige film eindigt
    private int filmEindTijd;

    // een film duurt dit aantal ticks na START_CINEMA
    private static final int FILMDUUR = 40;

    // logger voor het loggen naar de GUI
    private ILogger logger;

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

    // wordt aangeroepen door EventController als er een library event binnenkomt
    @Override
    public void onEvent(HotelEvent event) {
        // GOTO_CINEMA: een gast gaat naar de bioscoop, log dat
        if (event.getEventType() == HotelEventType.GOTO_CINEMA) {
            if (logger != null) logger.log("[" + event.getTime() + "] Bioscoop: gast " + event.getGuestId() + " komt binnen");
        }
        // START_CINEMA: film start officieel, sla eindtijd op en log film start
        else if (event.getEventType() == HotelEventType.START_CINEMA) {
            filmBezig = true;
            filmEindTijd = event.getTime() + FILMDUUR;
            if (logger != null) logger.log("[" + event.getTime() + "] Bioscoop: film start");
        }
        // NONE: elke tick checkt de bioscoop of de film al voorbij is
        else if (event.getEventType() == HotelEventType.NONE) {
            int tijd = event.getTime();
            // als de film bezig is en de eindtijd is bereikt
            if (filmBezig && tijd >= filmEindTijd) {
                filmBezig = false;
                // maak een FilmEindEvent aan en log film eindigt
                FilmEindEvent eindEvent = new FilmEindEvent(tijd, -1);
                if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Bioscoop: film eindigt");
            }
        }
    }

    // start een film
    public void startFilm() {}

    // stop een film
    public void stopFilm() {}

    // laat een gast de bioscoop betreden
    public void betreedBioscoop() {}
}
