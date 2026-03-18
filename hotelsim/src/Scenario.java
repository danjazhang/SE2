import java.util.ArrayList;
import java.util.List;

public class Scenario {
    // arraylist gebeurtenis gebeurtenissen
    List<Gebeurtenis> gebeurtenissen;

    //constructor
    public Scenario(){
        this.gebeurtenissen = new ArrayList<>();
    }

    public List<Gebeurtenis> krijgGebeurtenissen(int tijd) {
        List<Gebeurtenis> resultaat = new ArrayList<>();
        for (Gebeurtenis g : gebeurtenissen) {
            if (g.tijd == tijd) {
                resultaat.add(g);
            }
        }
        return resultaat;
    }

    public void voegGebeurtenisToe(Gebeurtenis g) {
        gebeurtenissen.add(g);
    }

}
