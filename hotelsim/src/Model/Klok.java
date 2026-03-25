package Model;

// Stelt de simulatieklok voor
// Houdt de huidige tijd bij en verhoogt die met elke tick
public class Klok {

    // de huidige tijd in de simulatie
    public int huidigeTijd;

    // hoeveel de tijd per tick verhoogd wordt
    public int tickDuur;

    // constructor: klok begint op tijd 0 met een tickDuur van 1
    public Klok() {
        huidigeTijd = 0;
        tickDuur = 1;
    }

    // verhoog de tijd met tickDuur
    public void tick() { huidigeTijd += tickDuur; }

    // zet de tijd terug naar 0
    public void reset() { huidigeTijd = 0; }

    // geef de huidige tijd terug
    public int krijgTijd() { return huidigeTijd; }
}
