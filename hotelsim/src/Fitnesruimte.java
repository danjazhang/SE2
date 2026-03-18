import java.util.ArrayList;
import java.util.List;

public class Fitnesruimte extends Ruimte {
    //arraylist gasten
    List<Gast> gasten;

    //constructor
    public Fitnesruimte(){
        this.gasten = new ArrayList<>();
    }

    public void breedteFitness(){}
    public void verlaatFitness(){}
}
