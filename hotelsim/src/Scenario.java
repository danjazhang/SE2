import java.util.List;
import java.util.ArrayList;

public class Scenario {

    // lijst van alle gebeurtenissen in de simulatie
    List<Gebeurtenis> gebeurtenissen;

    // constructor
    public Scenario(){
        // initialiseert de lijst
        gebeurtenissen = new ArrayList<>();
    }

    // voegt een gebeurtenis toe aan het scenario
    public void voegGebeurtenisToe(Gebeurtenis g){
        gebeurtenissen.add(g);
    }

    // geeft de gebeurtenissen terug op een bepaald tijdstip
    // (logica wordt later geïmplementeerd)
    public List<Gebeurtenis> krijgGebeurtenissen(int tijd){
        return null;
    }

    /*
     Voorbeeld gebruik:

     Scenario scenario = new Scenario();

     scenario.voegGebeurtenisToe(new Gebeurtenis(10, "checkin"));
     scenario.voegGebeurtenisToe(new Gebeurtenis(20, "schoonmaak"));

     // later in de simulatie:
     // scenario.krijgGebeurtenissen(tijd);
    */

}
