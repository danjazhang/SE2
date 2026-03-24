import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import hotelevents.HotelEventListener;

public class Hotel implements HotelEventListener{
    int breedte;
    int hoogte;

    HotelManager manager = new HotelManager();

    Layout layout;
    List<Ruimte> ruimtes;
    List<Persoon> personen;

    Lift lift;
    Trap trap;

    public Hotel() {
        ruimtes = new ArrayList<>();
        personen = new ArrayList<>();
    }

    // laadt het layout van het hotel uit een JSON bestand
    public void laadLayoutBestand(String bestandspad) {
        try {
            String inhoud = new String(Files.readAllBytes(Paths.get(bestandspad)));
            JSONArray array = new JSONArray(inhoud);

            // bepaal de maximale breedte en hoogte
            int maxX = 0, maxY = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                //position is locatie
                int[] pos = parsePositie(obj.getString("Position"));
                //dimension is grootte
                int[] dim = parseDimensie(obj.getString("Dimension"));
                maxX = Math.max(maxX, pos[0] + dim[0] - 1);
                maxY = Math.max(maxY, pos[1] + dim[1] - 1);
            }

            this.breedte = maxX;
            this.hoogte = maxY;
            this.layout = new Layout(breedte, hoogte);
            manager.addLayout(bestandspad, this.layout);

            // maak elke ruimte aan
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

        } catch (IOException e) {
            System.err.println("Fout bij laden layout: " + e.getMessage());
        }
    }

    // maakt de juiste subklasse aan op basis van het AreaType
    Ruimte maakRuimte(String areaType, JSONObject obj) {
        switch (areaType) {
            case "Room":
                Kamer kamer = new Kamer();
                String classificatie = obj.getString("Classification");
                //haalt het getal uit bv "5 sterren"
                kamer.sterren = Integer.parseInt(classificatie.split(" ")[0]);
                return kamer;

            case "Restaurant":
                Restaurant restaurant = new Restaurant();
                if (obj.has("Capacity")) {
                    restaurant.capaciteit = obj.getInt("Capacity");
                }
                return restaurant;

            case "Cinema":
                return new Bioscoop();

            case "Fitness":
                return new Fitnesruimte();

            //lift en trap erven niet van ruimte dus je maakt een nieuwe ruimte
            //die de type lift en trap krijgen
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
        return new int[]{
            Integer.parseInt(delen[0].trim()),
            Integer.parseInt(delen[1].trim())
        };
    }

    // parse "breedte, hoogte" string naar int array [breedte, hoogte]
    int[] parseDimensie(String dimensie) {
        String[] delen = dimensie.split(",");
        return new int[]{
            Integer.parseInt(delen[0].trim()),
            Integer.parseInt(delen[1].trim())
        };
    }

    public void voegPersoonToe(Persoon p) {
        personen.add(p);
    }

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

            default:
                break;
        }
    }

}
