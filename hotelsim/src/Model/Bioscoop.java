package Model;

import java.util.List;
import java.util.ArrayList;

// Stelt de bioscoop voor in het hotel
// Erft van Ruimte en reageert op cinema events
public class Bioscoop extends Ruimte {

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
}
