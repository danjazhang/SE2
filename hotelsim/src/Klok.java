public class Klok {

    // huidige tijd van de simulatie
    int huidigeTijd;

    // duur van één tick
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

    // geeft de huidige tijd terug
    public int krijgTijd(){
        return huidigeTijd;
    }
}