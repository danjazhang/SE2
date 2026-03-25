package Model;

public class Klok {

    public int huidigeTijd;
    public int tickDuur;

    public Klok() {
        huidigeTijd = 0;
        tickDuur = 1;
    }

    public void tick() { huidigeTijd += tickDuur; }
    public void reset() { huidigeTijd = 0; }
    public int krijgTijd() { return huidigeTijd; }
}
