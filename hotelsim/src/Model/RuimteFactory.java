package Model;

import Model.ruimte.*;
import Model.ruimte.Kamer;
import Model.ruimte.Ruimte;
import org.json.JSONObject;

public class RuimteFactory {

    private int volgendKamernummer = 101;
    private ILogger logger;

    public RuimteFactory(ILogger logger){
        this.logger = logger;
    }

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
                Restaurant restaurant = new Restaurant(logger);
                if (obj.has("Capacity")) restaurant.capaciteit = obj.getInt("Capacity");
                return restaurant;
            case "Cinema":
                return new Bioscoop(logger);
            case "Fitness":
                return new Fitnessruimte(logger);
            default:
                return new Ruimte();
        }
    }
}