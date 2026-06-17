package Model.layout;

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

            //loop door alle json objecten in het bestand
            for (int i = 0; i < array.length(); i++) {
                //haal huidige json object op
                JSONObject obj = array.getJSONObject(i);
                //zet de positie en dimensie string om naar een array
                int[] pos = parsePositie(obj.getString("Position"));
                int[] dim = parseDimensie(obj.getString("Dimension"));
                //sla de positie en dimensie op in het json object
                obj.put("_posX", pos[0]);
                obj.put("_posY", pos[1]);
                obj.put("_breedte", dim[0]);
                obj.put("_hoogte", dim[1]);
                //voeg het json object toe aan de lijst
                resultaat.ruimteData.add(obj);
            }
            //geef parseresultaat terug
            return resultaat;

        } catch (IOException e) {
            System.out.println("Fout bij laden layout: " + e.getMessage());
            return null;
        }
    }

    // Zet een string met coördinaten (bijv. "3,5") om naar een int-array [x, y]
// Wordt gebruikt om posities uit tekst/config te kunnen lezen
    private int[] parsePositie(String positie) {

        // Splitst de string op de komma, zodat je twee losse delen krijgt
        String[] delen = positie.split(",");

        // trim() haalt spaties weg en parseInt zet tekst om naar een getal
        // resultaat: [x, y]
        return new int[]{
                Integer.parseInt(delen[0].trim()),
                Integer.parseInt(delen[1].trim())
        };
    }

    // Zet een string met afmetingen (bijv. "10,4") om naar een int-array [breedte, hoogte]
// Wordt gebruikt om dimensies van ruimtes of objecten in te lezen
    private int[] parseDimensie(String dimensie) {

        // Splitst de string op de komma in twee delen
        String[] delen = dimensie.split(",");

        // Zet beide delen om naar integers en verwijdert spaties
        // resultaat: [breedte, hoogte]
        return new int[]{
                Integer.parseInt(delen[0].trim()),
                Integer.parseInt(delen[1].trim())
        };
    }
}