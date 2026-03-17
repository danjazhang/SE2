import java.util.ArrayList;
import java.util.List;

public class Lift {

    private int huidigeVerdieping;
    private List<Persoon> passagiers;
    private List<Integer> verzoeken;
    private boolean deurOpen;

    // constructor
    public Lift() {
        this.huidigeVerdieping = 0;
        this.passagiers = new ArrayList<>();
        this.verzoeken = new ArrayList<>();
        this.deurOpen = false;
    }

    public void gaOmhoog() {
        huidigeVerdieping++;
    }

    public void gaOmlaag() {
        huidigeVerdieping--;
    }

    public void openDeur() {
        deurOpen = true;
    }

    public void sluitDeur() {
        deurOpen = false;
    }

    public void voegVerzoekToe(int verdieping) {
        if (!verzoeken.contains(verdieping)) {
            verzoeken.add(verdieping);
        }
    }

    public int getHuidigeVerdieping() {
        return huidigeVerdieping;
    }

    public List<Integer> getVerzoeken() {
        return verzoeken;
    }
}