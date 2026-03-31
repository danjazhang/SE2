package Model;

import View.EventLog;
import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;

import java.util.ArrayList;
import java.util.List;

// Model klasse: bevat alle data van het hotel
// Implementeert HotelEventListener om te reageren op events (bijv. evacuatie)
// Implementeert het Observer pattern via ModelListener zodat View en Controller
// automatisch een melding krijgen als de data verandert
public class Hotel implements HotelEventListener {

    // breedte en hoogte van het hotel grid
    public int breedte;
    public int hoogte;

    // beheert alle layouts en hotels
    public HotelManager manager = new HotelManager();

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

    // voeg een observer toe aan de lijst
    public void voegListenerToe(ModelListener l) { listeners.add(l); }

    // stuur een melding naar alle observers dat het model veranderd is
    private void notifyListeners() {
        for (ModelListener l : listeners) l.modelGewijzigd();
    }

    // constructor: maak lege lijsten aan
    public Hotel() {
        ruimtes = new ArrayList<>();
        personen = new ArrayList<>();
    }

    // de parser die het JSON bestand inleest en de hotel data vult
    private LayoutParser parser = new LayoutParser();

    // laad de hotel layout uit een JSON bestand via de LayoutParser
    // Hotel zelf weet niet hoe het bestand gelezen wordt, dat doet de parser
    public void laadLayoutBestand(String bestandspad) {
        parser.laad(bestandspad, this);
        // notificeer alle observers dat de layout veranderd is
        notifyListeners();
    }

    // voeg een persoon toe aan het hotel
    public void voegPersoonToe(Persoon p) { personen.add(p); }

    // geef de ruimte op positie (x, y) terug
    public Ruimte krijgRuimteOp(int x, int y) {
        Vakje vakje = layout.krijgVakje(x, y);
        if (vakje != null) return vakje.ruimte;
        return null;
    }

    // reageer op hotel events zoals evacuatie en godzilla aanval
    @Override
    public void notify(HotelEvent evt) {
        switch (evt.getEventType()) {
            case EVACUATE:
                EventLog.log("[" + evt.getTime() + "] HOTEL: evacuatie gestart!");
                break;
            case GODZILLA:
                EventLog.log("[" + evt.getTime() + "] HOTEL: GODZILLA AANVAL!");
                break;
            default: break;
        }
    }
}
