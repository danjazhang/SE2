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
    public static Hotel laadVanBestand(String bestandspad) {
        Hotel hotel = new Hotel();
        hotel.ruimtes = new ArrayList<>();
        hotel.personen = new ArrayList<>();

        try {
            String inhoud = new String(Files.readAllBytes(Paths.get(bestandspad)));
            JSONArray array = new JSONArray(inhoud);

            int maxX = 0, maxY = 0;

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int[] pos = hotel.parsePositie(obj.getString("Position"));
                int[] dim = hotel.parseDimensie(obj.getString("Dimension"));

                maxX = Math.max(maxX, pos[0] + dim[0] - 1);
                maxY = Math.max(maxY, pos[1] + dim[1] - 1);
            }

            hotel.breedte = maxX;
            hotel.hoogte = maxY;
            hotel.layout = new Layout(maxX, maxY);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                String areaType = obj.getString("AreaType");
                int[] pos = hotel.parsePositie(obj.getString("Position"));
                int[] dim = hotel.parseDimensie(obj.getString("Dimension"));

                Ruimte ruimte = hotel.maakRuimte(areaType, obj);
                ruimte.posX = pos[0];
                ruimte.posY = pos[1];
                ruimte.breedte = dim[0];
                ruimte.hoogte = dim[1];

                hotel.ruimtes.add(ruimte);
                hotel.layout.plaatsRuimte(ruimte);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return hotel;
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
