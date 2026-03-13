import java.util.List;
import java.util.ArrayList;

public class Vakje {

    int x;
    int y;

    // ruimte waar dit vakje bij hoort
    Ruimte ruimte;

    // personen die op dit vakje staan
    List<Persoon> personen;

    // constructor
    public Vakje(){
        personen = new ArrayList<>();
    }

    // voegt een persoon toe aan dit vakje
    public void voegPersoonToe(Persoon p){
        personen.add(p);
    }

    // verwijdert een persoon van dit vakje
    public void verwijderPersoon(Persoon p){
        personen.remove(p);
    }

    // geeft de lijst van personen op dit vakje terug
    public List<Persoon> krijgPersonen(){
        return personen;
    }

}
