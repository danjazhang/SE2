package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// Verantwoordelijk voor het inlezen en verwerken van een JSON layout bestand
// Gescheiden van Hotel zodat Hotel alleen data beheert en niet weet hoe een bestand gelezen wordt
// Dit heet het Single Responsibility Principle: elke klasse heeft één verantwoordelijkheid
public class LayoutParser {

    // leest een JSON bestand en vult het hotel met ruimtes en een layout
    // geeft true terug als het laden gelukt is, false als er een fout was
    public boolean laad(String bestandspad, Hotel hotel) {
        try {
            hotel.ruimtes.clear();
            hotel.personen.clear();
            hotel.layout = null;

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

            hotel.breedte = maxX;
            hotel.hoogte = maxY;
            hotel.layout = new Layout(hotel.breedte, hotel.hoogte);
            hotel.manager.addLayout(bestandspad, hotel.layout);

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

                hotel.ruimtes.add(ruimte);
                hotel.layout.plaatsRuimte(ruimte);
            }

            System.out.println("Layout geladen: " + hotel.breedte + "x" + hotel.hoogte + ", " + hotel.ruimtes.size() + " ruimtes");
            return true;

        } catch (IOException e) {
            System.err.println("Fout bij laden layout: " + e.getMessage());
            return false;
        }
    }

    // maakt de juiste subklasse aan op basis van het AreaType uit de JSON
    private Ruimte maakRuimte(String areaType, JSONObject obj) {
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
    private int[] parsePositie(String positie) {
        String[] delen = positie.split(",");
        return new int[]{Integer.parseInt(delen[0].trim()), Integer.parseInt(delen[1].trim())};
    }

    // parse "breedte, hoogte" string naar int array [breedte, hoogte]
    private int[] parseDimensie(String dimensie) {
        String[] delen = dimensie.split(",");
        return new int[]{Integer.parseInt(delen[0].trim()), Integer.parseInt(delen[1].trim())};
    }
}
