public class Klok {

    // huidige tijd van de simulatie
    int huidigeTijd;

    // duur van één tick (bijv. 1 seconde)
    int tickDuur;

    // constructor
    public Klok(){
        huidigeTijd = 0;
        tickDuur = 1;
    }

    // verhoogt de tijd met één tick
    public void tick(){
        huidigeTijd += tickDuur;
    }

    // reset de klok naar 0
    public void reset(){
        huidigeTijd = 0;
    }

}