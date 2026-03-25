package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Hotel implements HotelEventListener {
    public int breedte;
    public int hoogte;

    public HotelManager manager = new HotelManager();
    public Layout layout;
    public List<Ruimte> ruimtes;
    public List<Persoon> personen;
    private List<ModelListener> listeners = new ArrayList<>();

    public void voegListenerToe(ModelListener l) { listeners.add(l); }

    private void notifyListeners() {
        for (ModelListener l : listeners) l.modelGewijzigd();
    }

    Lift lift;
    Trap trap;

    public Hotel() {
        ruimtes = new ArrayList<>();
        personen = new ArrayList<>();
    }

    public void laadLayoutBestand(String bestandspad) {
        try {
            ruimtes.clear();
            personen.clear();
            layout = null;

            String inhoud = new String(Files.readAllBytes(Paths.get(bestandspad)));
            JSONArray array = new JSONArray(inhoud);

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
            notifyListeners();

        } catch (IOException e) {
            System.err.println("Fout bij laden layout: " + e.getMessage());
        }
    }

    Ruimte maakRuimte(String areaType, JSONObject obj) {
        switch (areaType) {
            case "Room":
                Kamer kamer = new Kamer();
                kamer.sterren = Integer.parseInt(obj.getString("Classification").split(" ")[0]);
                return kamer;
            case "Restaurant":
                Restaurant restaurant = new Restaurant();
                if (obj.has("Capacity")) restaurant.capaciteit = obj.getInt("Capacity");
                return restaurant;
            case "Cinema": return new Bioscoop();
            case "Fitness": return new Fitnesruimte();
            default: return new Ruimte();
        }
    }

    int[] parsePositie(String positie) {
        String[] delen = positie.split(",");
        return new int[]{Integer.parseInt(delen[0].trim()), Integer.parseInt(delen[1].trim())};
    }

    int[] parseDimensie(String dimensie) {
        String[] delen = dimensie.split(",");
        return new int[]{Integer.parseInt(delen[0].trim()), Integer.parseInt(delen[1].trim())};
    }

    public void voegPersoonToe(Persoon p) { personen.add(p); }

    public Ruimte krijgRuimteOp(int x, int y) {
        Vakje vakje = layout.krijgVakje(x, y);
        if (vakje != null) return vakje.ruimte;
        return null;
    }

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
