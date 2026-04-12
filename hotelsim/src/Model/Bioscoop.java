package Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Stelt de bioscoop voor in het hotel
// Erft van Ruimte en implementeert IEventListener
// De bioscoop reageert op START_CINEMA om filmEindTijd te berekenen
// Via TickEvent checkt hij wanneer de film eindigt
// GOTO_CINEMA en START_CINEMA worden al gelogd door de library
public class Bioscoop extends Ruimte implements IEventListener {

    // of er momenteel een film bezig is
    public boolean filmBezig;

    // de duur van de huidige film in ticks
    public int filmDuur;

    // de gasten die momenteel in de bioscoop zijn
    public List<Gast> gasten;

    // het tijdstip waarop de huidige film eindigt
    private int filmEindTijd;

    // wachtrij van gastnummers die wachten op de volgende film
    private Queue<Integer> wachtrij;

    // als de wachttijd meer dan dit aantal ticks is, gaat de gast weg
    private static final int MAX_WACHTTIJD = 10;

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
        this.wachtrij = new LinkedList<>();
        this.logger = logger;
    }

    // lege constructor voor als er geen logger nodig is (bijv. in testen)
    public Bioscoop() {
        this.gasten = new ArrayList<>();
        this.filmBezig = false;
        this.filmDuur = 0;
        this.filmEindTijd = 0;
        this.wachtrij = new LinkedList<>();
    }

    // wordt aangeroepen door EventController als er een intern event binnenkomt
    @Override
    public void onEvent(InternEvent event) {

        // START_CINEMA: film start officieel, bereken wanneer de film eindigt
        // GOTO_CINEMA en START_CINEMA worden al gelogd door de library
        if (event instanceof FilmStartEvent) {
            int tijd = event.getTijd();
            filmBezig = true;
            filmEindTijd = tijd + FILMDUUR;
        }

        // elke tick checkt de bioscoop of de film al voorbij is
        else if (event instanceof TickEvent) {
            int tijd = event.getTijd();

            // als de film bezig is en de eindtijd is bereikt
            if (filmBezig && tijd >= filmEindTijd) {
                filmBezig = false;
                if (logger != null) logger.log("[" + tijd + "] Bioscoop: film eindigt");
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
