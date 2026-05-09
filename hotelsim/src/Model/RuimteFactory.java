package Model;

import Model.ruimte.*;
import Model.ruimte.Ruimte;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class RuimteFactory {

    // Voor elke verdieping onthouden we wat het volgende vrije kamernummer is.
    // Zo lopen kamernummers niet allemaal in een lange rij door elkaar,
    // maar krijgt elke verdieping een eigen nummerreeks.
    private Map<Integer, Integer> volgendeKamernummersPerVerdieping = new HashMap<>();
    // De onderste kamerlaag telt als verdieping 1.
    // Daardoor beginnen de kamers direct boven de lobby bij 101, 102, 103, ...
    private int ondersteKamerPosY;
    private ILogger logger;

    public RuimteFactory(ILogger logger){
        this.logger = logger;
    }

    public RuimteFactory(ILogger logger, int ondersteKamerPosY){
        this.logger = logger;
        this.ondersteKamerPosY = ondersteKamerPosY;
    }

    // Maak op basis van de JSON het juiste type ruimte aan.
    // De factory verzamelt de aanmaaklogica op een plek, zodat andere klassen
    // niet zelf hoeven te weten hoe een Kamer, Restaurant of Bioscoop wordt opgebouwd.
    public Ruimte maakRuimte(String areaType, JSONObject obj) {
        switch (areaType) {
            case "Room":
                Kamer kamer = new Kamer();
                // Haal alleen het getal uit een tekst zoals "5 sterren".
                // De JSON bevat hier tekst, maar in de klasse willen we een int bewaren.
                kamer.sterren = Integer.parseInt(obj.getString("Classification").split(" ")[0]);
                // Geef de kamer een nummer dat past bij zijn verdieping.
                // Onderaan beginnen kamers bij 101, daarboven bij 201, enzovoort.
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

    // Bereken een kamernummer op basis van de verticale positie in de layout.
    // De onderste kamerlaag wordt verdieping 1, zodat die laag nummers 101, 102, ...
    // krijgt en de laag erboven 201, 202, ... krijgt.
    private int maakKamernummer(JSONObject obj) {
        int posY = obj.has("_posY") ? Math.max(1, obj.getInt("_posY")) : 1;
        // In de layout staat een kleine y-waarde juist hoger op het scherm.
        // Voor kamernummers willen we precies het omgekeerde:
        // de onderste kamerlaag moet verdieping 1 zijn.
        int verdieping = ondersteKamerPosY > 0 ? Math.max(1, ondersteKamerPosY - posY + 1) : 1;
        // Elke verdieping krijgt zijn eigen reeks kamernummers.
        // Daardoor begint verdieping 1 bij 101, verdieping 2 bij 201
        // en verdieping 3 bij 301.
        int volgendKamernummer = volgendeKamernummersPerVerdieping.getOrDefault(verdieping, verdieping * 100 + 1);
        // Na het uitdelen van dit nummer schuiven we het volgende vrije nummer
        // op dezelfde verdieping een stap op.
        volgendeKamernummersPerVerdieping.put(verdieping, volgendKamernummer + 1);
        return volgendKamernummer;
    }
}
