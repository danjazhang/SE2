package Model.ruimte;

import Model.persoon.Gast;

import java.util.ArrayList;
import java.util.List;

// Stelt de fitnessruimte voor in het hotel
// Erft van Ruimte en reageert op fitness events
public class Fitnessruimte extends Ruimte {

    // de gasten die momenteel in de fitnessruimte zijn
    public List<Gast> gasten;

    // constructor: fitnessruimte begint zonder gasten
    public Fitnessruimte() {
        this.gasten = new ArrayList<>();
    }

    // laat een gast sporten
    public void breedteFitness() {}

    // laat een gast de fitnessruimte verlaten
    public void verlaatFitness() {}
}
