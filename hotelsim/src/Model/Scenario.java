package Model;

import java.util.List;
import java.util.ArrayList;

public class Scenario {

    public List<Gebeurtenis> gebeurtenissen;

    public Scenario() {
        gebeurtenissen = new ArrayList<>();
    }

    public void voegGebeurtenisToe(Gebeurtenis g) {
        gebeurtenissen.add(g);
    }

    public List<Gebeurtenis> krijgGebeurtenissen(int tijd) {
        List<Gebeurtenis> resultaat = new ArrayList<>();
        for (Gebeurtenis g : gebeurtenissen) {
            if (g.tijd == tijd) resultaat.add(g);
        }
        return resultaat;
    }
}
