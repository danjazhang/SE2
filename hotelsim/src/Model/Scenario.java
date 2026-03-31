package Model;

import java.util.List;
import java.util.ArrayList;

// Bevat een lijst van gebeurtenissen die op bepaalde tijdstippen plaatsvinden
// De Simulatie vraagt per tijdstip welke gebeurtenissen er zijn
public class Scenario {

    // alle gebeurtenissen in dit scenario
    public List<Gebeurtenis> gebeurtenissen;

    // constructor: begin met een lege lijst
    public Scenario() {
        gebeurtenissen = new ArrayList<>();
    }

    // voeg een gebeurtenis toe aan het scenario
    public void voegGebeurtenisToe(Gebeurtenis g) {
        gebeurtenissen.add(g);
    }

    // geef alle gebeurtenissen terug die plaatsvinden op het opgegeven tijdstip
    public List<Gebeurtenis> krijgGebeurtenissen(int tijd) {
        List<Gebeurtenis> resultaat = new ArrayList<>();
        for (Gebeurtenis g : gebeurtenissen) {
            if (g.tijd == tijd) resultaat.add(g);
        }
        return resultaat;
    }
}
