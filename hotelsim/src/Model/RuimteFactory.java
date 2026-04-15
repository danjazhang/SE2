package Model;

import Model.ruimte.*;
import org.json.JSONObject;

public class RuimteFactory {

    private int volgendKamernummer = 101;

    // maakt de juiste subklasse aan op basis van het AreaType uit de JSON
    public Ruimte maakRuimte(String areaType, JSONObject obj) {
        switch (areaType) {
            case "Room":
                Kamer kamer = new Kamer();
                // haalt het getal uit bv "5 sterren"
                kamer.sterren = Integer.parseInt(obj.getString("Classification").split(" ")[0]);
                //maakt kamer nummer
                kamer.kamernummer = volgendKamernummer++;
                return kamer;
            case "Restaurant":
                Restaurant restaurant = new Restaurant();
                if (obj.has("Capacity")) restaurant.capaciteit = obj.getInt("Capacity");
                return restaurant;
            case "Cinema":
                return new Bioscoop();
            case "Fitness":
                return new Fitnessruimte();
            default:
                return new Ruimte();
        }
    }
}
