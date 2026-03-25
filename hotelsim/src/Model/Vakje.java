package Model;

import java.util.List;
import java.util.ArrayList;

public class Vakje {

    public int x;
    public int y;
    public Ruimte ruimte;
    public List<Persoon> personen;

    public Vakje() {
        personen = new ArrayList<>();
    }

    public Ruimte getRuimte() { return ruimte; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setRuimte(Ruimte ruimte) { this.ruimte = ruimte; }
    public void voegPersoonToe(Persoon p) { personen.add(p); }
    public void verwijderPersoon(Persoon p) { personen.remove(p); }
    public List<Persoon> krijgPersonen() { return new ArrayList<>(personen); }
}
