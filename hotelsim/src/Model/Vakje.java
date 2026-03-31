package Model;

import java.util.List;
import java.util.ArrayList;

// Stelt één vakje in het hotel grid voor
// Elk vakje heeft een positie, een ruimte en een lijst van personen
public class Vakje {

    // positie in het grid
    public int x;
    public int y;

    // de ruimte die dit vakje bezet
    public Ruimte ruimte;

    // de personen die momenteel op dit vakje staan
    public List<Persoon> personen;

    // constructor: maak een leeg vakje aan
    public Vakje() {
        personen = new ArrayList<>();
    }

    public Ruimte getRuimte() { return ruimte; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setRuimte(Ruimte ruimte) { this.ruimte = ruimte; }

    // voeg een persoon toe aan dit vakje
    public void voegPersoonToe(Persoon p) { personen.add(p); }

    // verwijder een persoon van dit vakje
    public void verwijderPersoon(Persoon p) { personen.remove(p); }

    // geef een kopie van de personenlijst terug
    public List<Persoon> krijgPersonen() { return new ArrayList<>(personen); }
}
