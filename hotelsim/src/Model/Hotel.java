package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    // laad de hotel layout uit een JSON bestand
    // bepaalt de breedte en hoogte, maakt ruimtes aan en plaatst ze in de layout
    public void laadLayoutBestand(String bestandspad) {
        try {
            ruimtes.clear();
            personen.clear();
            layout = null;

            String inhoud = new String(Files.readAllBytes(Paths.get(bestandspad)));
            JSONArray array = new JSONArray(inhoud);

            // bepaal de maximale breedte en hoogte van het grid
            int maxX = 0, maxY = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int[] pos = parsePositie(obj.getString("Position"));
                int[] dim = parseDimensie(obj.getString("Dimension"));
                maxX = Math.max(maxX, pos[0] + dim[0] - 1);
                maxY = Math.max(maxY, pos[1] + dim[1] - 1);
            }

            this.breedte = maxX;
            this.hoogte = maxY;
            this.layout = new Layout(breedte, hoogte);
            manager.addLayout(bestandspad, this.layout);

            // maak elke ruimte aan op basis van het type in de JSON
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String areaType = obj.getString("AreaType");
                int[] pos = parsePositie(obj.getString("Position"));
                int[] dim = parseDimensie(obj.getString("Dimension"));

                Ruimte ruimte = maakRuimte(areaType, obj);
                ruimte.posX = pos[0];
                ruimte.posY = pos[1];
                ruimte.breedte = dim[0];
                ruimte.hoogte = dim[1];

                ruimtes.add(ruimte);
                layout.plaatsRuimte(ruimte);
            }

            System.out.println("Layout geladen: " + breedte + "x" + hoogte + ", " + ruimtes.size() + " ruimtes");

            // notificeer alle observers dat de layout veranderd is
            notifyListeners();

        } catch (IOException e) {
            System.err.println("Fout bij laden layout: " + e.getMessage());
        }
    }

    // maakt de juiste subklasse aan op basis van het AreaType uit de JSON
    Ruimte maakRuimte(String areaType, JSONObject obj) {
        switch (areaType) {
            case "Room":
                Kamer kamer = new Kamer();
                // haalt het getal uit bv "5 sterren"
                kamer.sterren = Integer.parseInt(obj.getString("Classification").split(" ")[0]);
                return kamer;
            case "Restaurant":
                Restaurant restaurant = new Restaurant();
                if (obj.has("Capacity")) restaurant.capaciteit = obj.getInt("Capacity");
                return restaurant;
            case "Cinema":
                return new Bioscoop();
            case "Fitness":
                return new Fitnesruimte();
            // lift en trap erven niet van Ruimte, dus maak een gewone Ruimte met een type
            case "Lift":
                Ruimte lift = new Ruimte();
                lift.type = "Lift";
                return lift;
            case "Trap":
                Ruimte trap = new Ruimte();
                trap.type = "Trap";
                return trap;
            default:
                return new Ruimte();
        }
    }

    // parse "x, y" string naar int array [x, y]
    int[] parsePositie(String positie) {
        String[] delen = positie.split(",");
        return new int[]{Integer.parseInt(delen[0].trim()), Integer.parseInt(delen[1].trim())};
    }

    // parse "breedte, hoogte" string naar int array [breedte, hoogte]
    int[] parseDimensie(String dimensie) {
        String[] delen = dimensie.split(",");
        return new int[]{Integer.parseInt(delen[0].trim()), Integer.parseInt(delen[1].trim())};
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
                System.out.println("[" + evt.getTime() + "] HOTEL: evacuatie gestart!");
                break;
            case GODZILLA:
                System.out.println("[" + evt.getTime() + "] HOTEL: GODZILLA AANVAL!");
                break;
            default: break;
        }
    }
}
