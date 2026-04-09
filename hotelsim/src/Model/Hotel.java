package Model;

import java.util.ArrayList;
import java.util.List;

// Model klasse: bevat alle data van het hotel
// Implementeert HotelEventListener om te reageren op events (bijv. evacuatie)
// Implementeert het Observer pattern via ModelListener zodat View en Controller
// automatisch een melding krijgen als de data verandert
public class Hotel {

    // breedte en hoogte van het hotel grid
    public int breedte;
    public int hoogte;


    // de huidige layout van het hotel
    public Layout layout;

    // lijst van alle ruimtes in het hotel
    public List<Ruimte> ruimtes;

    // lijst van alle personen in het hotel
    public List<Persoon> personen;

    // lijst van observers (View en Controller) die genotificeerd worden bij wijzigingen
    private List<ModelListener> listeners = new ArrayList<>();

    // lift en trap referenties
    Lift lift;
    Trap trap;

    // constructor: maak lege lijsten aan
    public Hotel() {
        ruimtes = new ArrayList<>();
        personen = new ArrayList<>();
    }

    // voeg een observer toe aan de lijst
    public void voegListenerToe(ModelListener l) {
        listeners.add(l);
    }

    // stuur een melding naar alle observers dat het model veranderd is
    public void notifyListeners() {
        for (ModelListener l : listeners) l.modelGewijzigd();
    }

    // voeg een persoon toe aan het hotel
    public void voegPersoonToe(Persoon p) {
        personen.add(p);
    }

    // geef de ruimte op positie (x, y) terug
    public Ruimte krijgRuimteOp(int x, int y) {
        Vakje vakje = layout.krijgVakje(x, y);
        if (vakje != null) return vakje.ruimte;
        return null;
    }
}