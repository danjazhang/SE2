package Model;

import Model.ruimte.*;
import Model.ruimte.Ruimte;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Verantwoordelijkheid: ruimtes aanmaken op basis van het type uit de JSON
public class RuimteFactory {

    // per verdieping bijhouden wat het volgende vrije kamernummer is
    // verdieping 1 begint bij 101, verdieping 2 bij 201, etc.
    private Map<Integer, Integer> volgendeKamernummersPerVerdieping = new HashMap<>();

    // de onderste kamerlaag in de layout, gebruikt om verdiepingsnummers te berekenen
    private int ondersteKamerPosY;

    private ILogger logger;

    // constructor zonder verdiepingsinfo, kamernummers beginnen dan allemaal bij 101
    public RuimteFactory(ILogger logger) {
        this.logger = logger;
    }

    // constructor met verdiepingsinfo voor correcte kamernummers per verdieping
    public RuimteFactory(ILogger logger, int ondersteKamerPosY) {
        this.logger = logger;
        this.ondersteKamerPosY = ondersteKamerPosY;
    }

    // maakt de juiste subklasse aan op basis van het AreaType uit de JSON
    public Ruimte maakRuimte(String areaType, JSONObject obj) {
        switch (areaType) {
            case "Room":
                Kamer kamer = new Kamer();
                // haalt het getal uit bv "5 sterren"
                kamer.sterren = Integer.parseInt(obj.getString("Classification").split(" ")[0]);
                // geef de kamer een nummer passend bij zijn verdieping
                kamer.kamernummer = maakKamernummer(obj);
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

    // berekent het kamernummer op basis van de verdieping
    // verdieping 1 = onderste kamerlaag, begint bij 101
    // verdieping 2 begint bij 201, verdieping 3 bij 301, etc.
    private int maakKamernummer(JSONObject obj) {
        int posY = obj.has("_posY") ? Math.max(1, obj.getInt("_posY")) : 1;
        // kleine y-waarde is hoger op het scherm, dus omdraaien voor verdiepingsnummer
        int verdieping = ondersteKamerPosY > 0 ? Math.max(1, ondersteKamerPosY - posY + 1) : 1;
        int volgendKamernummer = volgendeKamernummersPerVerdieping.getOrDefault(verdieping, verdieping * 100 + 1);
        volgendeKamernummersPerVerdieping.put(verdieping, volgendKamernummer + 1);
        return volgendKamernummer;
    }
}
