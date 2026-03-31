package Model;

import View.EventLog;
import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;
import jdk.jfr.Event;

import java.util.List;
import java.util.ArrayList;

// Stelt de bioscoop voor in het hotel
// Erft van Ruimte en reageert op cinema events
public class Bioscoop extends Ruimte implements HotelEventListener {

    // of er momenteel een film bezig is
    public boolean filmBezig;

    // de duur van de huidige film
    public int filmDuur;

    // de gasten die momenteel in de bioscoop zijn
    public List<Gast> gasten;

    // constructor: bioscoop begint zonder film en zonder gasten
    public Bioscoop() {
        this.gasten = new ArrayList<>();
        this.filmBezig = false;
        this.filmDuur = 0;
    }

    // start een film
    public void startFilm() {}

    // stop een film
    public void stopFilm() {}

    // laat een gast de bioscoop betreden
    public void betreedBioscoop() {}

    // reageer op cinema events
    @Override
    public void notify(HotelEvent evt) {
        switch (evt.getEventType()) {
            case GOTO_CINEMA:
                EventLog.log("[" + evt.getTime() + "] Bioscoop: gast " + evt.getGuestId() + " komt binnen");
                break;
            case START_CINEMA:
                EventLog.log("[" + evt.getTime() + "] Bioscoop: film start");
                break;
            default: break;
        }
    }
}
