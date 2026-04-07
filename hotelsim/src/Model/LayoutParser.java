package Model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// Verantwoordelijkheid: JSON bestand lezen en omzetten naar ParseResultaat
public class LayoutParser {

    // leest een JSON bestand en geeft een ParseResultaat terug
    public ParseResultaat laad(String bestandspad) {
        try {
            String inhoud = new String(Files.readAllBytes(Paths.get(bestandspad)));
            JSONArray array = new JSONArray(inhoud);

            ParseResultaat resultaat = new ParseResultaat();

            // bepaal de maximale breedte en hoogte
            int maxX = 0, maxY = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int[] pos = parsePositie(obj.getString("Position"));
                int[] dim = parseDimensie(obj.getString("Dimension"));
                maxX = Math.max(maxX, pos[0] + dim[0] - 1);
                maxY = Math.max(maxY, pos[1] + dim[1] - 1);
            }

            resultaat.breedte = maxX;
            resultaat.hoogte = maxY;

            // maak elke ruimte aan
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String areaType = obj.getString("AreaType");
                int[] pos = parsePositie(obj.getString("Position"));
                int[] dim = parseDimensie(obj.getString("Dimension"));

                Ruimte ruimte = new RuimteMaker().maakRuimte(areaType, obj);
                ruimte.posX = pos[0];
                ruimte.posY = pos[1];
                ruimte.breedte = dim[0];
                ruimte.hoogte = dim[1];

                resultaat.ruimtes.add(ruimte);
            }

            return resultaat;

        } catch (IOException e) {
            System.out.println("Fout bij laden layout: " + e.getMessage());
            return null;
        }
    }

    private int[] parsePositie(String positie) {
        String[] delen = positie.split(",");
        return new int[]{Integer.parseInt(delen[0].trim()), Integer.parseInt(delen[1].trim())};
    }

    private int[] parseDimensie(String dimensie) {
        String[] delen = dimensie.split(",");
        return new int[]{Integer.parseInt(delen[0].trim()), Integer.parseInt(delen[1].trim())};
    }
}
