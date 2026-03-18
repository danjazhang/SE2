import java.util.ArrayList;
import java.util.List;

public class Lift {
    int huidigeverdieping;
    //Richitng Richting;
    //arraylist persoon passagiers
    List<Persoon> passagiers;
    //verdiepingverzoeken list?

    //constructor
    public Lift(){
        this.huidigeverdieping = 0;
        this.passagiers = new ArrayList<>();
    }

    public void gaOmhoog(){}
    public void gaOmlaag(){}
    public void openDeur(){}
    public void sluitDeur(){}
    public void voegVerzoekToe(){}

}
