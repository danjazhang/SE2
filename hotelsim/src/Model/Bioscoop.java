package Model;

import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;
import java.util.List;
import java.util.ArrayList;

public class Bioscoop extends Ruimte implements HotelEventListener {
    public boolean filmBezig;
    public int filmDuur;
    public List<Gast> gasten;

    public Bioscoop() {
        this.gasten = new ArrayList<>();
        this.filmBezig = false;
        this.filmDuur = 0;
    }

    public void startFilm() {}
    public void stopFilm() {}
    public void betreedBioscoop() {}

    @Override
    public void notify(HotelEvent evt) {
        switch (evt.getEventType()) {
            case GOTO_CINEMA:
                System.out.println("[" + evt.getTime() + "] Bioscoop: gast " + evt.getGuestId() + " komt binnen");
                break;
            case START_CINEMA:
                System.out.println("[" + evt.getTime() + "] Bioscoop: film start");
                break;
            default: break;
        }
    }
}
