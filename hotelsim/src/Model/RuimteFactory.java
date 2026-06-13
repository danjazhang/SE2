package Model;

import Model.ruimte.*;
import Model.ruimte.Ruimte;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Verantwoordelijkheid: de juiste ruimtesubklasse aanmaken op basis van het AreaType uit de JSON.
// RuimteFactory zorgt ervoor dat de aanroeper niet zelf hoeft te weten welke subklasse bij welk type hoort.
public class RuimteFactory {

    // Een map die per verdieping bijhoudt wat het volgende vrije kamernummer is.
    // Verdieping 1 begint bij 101, verdieping 2 bij 201, verdieping 3 bij 301, etc.
    private Map<Integer, Integer> volgendeKamernummersPerVerdieping = new HashMap<>();

    // De y-positie van de onderste kamerlaag in de layout, gebruikt om verdiepingsnummers te berekenen.
    private int ondersteKamerPosY;

    // Logger voor het doorgeven aan ruimtes die berichten naar de GUI sturen.
    private ILogger logger;

    // Constructor zonder verdiepingsinfo: alle kamernummers beginnen bij 101.
    public RuimteFactory(ILogger logger) {
        this.logger = logger;
    }

    // Constructor met verdiepingsinfo: kamernummers worden correct per verdieping berekend.
    public RuimteFactory(ILogger logger, int ondersteKamerPosY) {
        this.logger = logger;
        this.ondersteKamerPosY = ondersteKamerPosY;
    }

    // Maak de juiste subklasse aan op basis van het AreaType-veld uit de JSON.
    // 'switch (areaType)' kijkt welke tekst areaType is en voert de bijpassende code uit.
    public Ruimte maakRuimte(String areaType, JSONObject obj) {
        switch (areaType) {
            case "Room":
                Kamer kamer = new Kamer();
                // Haal het getal uit de Classification-string, bijvoorbeeld "5 sterren" geeft 5.
                // 'split(" ")[0]' splitst de string op spaties en pakt het eerste deel.
                // 'Integer.parseInt(...)' zet de tekst om naar een getal.
                kamer.sterren = Integer.parseInt(obj.getString("Classification").split(" ")[0]);
                // Geef de kamer een nummer dat past bij zijn verdieping via maakKamernummer().
                kamer.kamernummer = maakKamernummer(obj);
                return kamer;
            case "Restaurant":
                Restaurant restaurant = new Restaurant(logger);
                // 'obj.has("Capacity")' betekent: als de JSON het veld Capacity bevat, lees het dan in.
                if (obj.has("Capacity")) restaurant.capaciteit = obj.getInt("Capacity");
                return restaurant;
            case "Cinema":
                // Geef een nieuwe Bioscoop terug met de logger.
                return new Bioscoop(logger);
            case "Fitness":
                // Geef een nieuwe Fitnessruimte terug met de logger.
                return new Fitnessruimte(logger);
            default:
                // Onbekend type: geef een gewone lege Ruimte terug.
                return new Ruimte();
        }
    }

    // Bereken het kamernummer op basis van de y-positie in de JSON.
    // Verdieping 1 is de onderste kamerlaag, begint bij 101.
    // Verdieping 2 begint bij 201, verdieping 3 bij 301, etc.
    private int maakKamernummer(JSONObject obj) {
        // Haal de y-positie op uit de JSON. Als dat veld er niet is, gebruik dan 1.
        int posY;
        if (obj.has("_posY")) {
            // 'Math.max(1, ...)' zorgt dat posY nooit kleiner dan 1 wordt.
            posY = Math.max(1, obj.getInt("_posY"));
        } else {
            posY = 1;
        }

        // Bereken de verdieping: ondersteKamerPosY min posY plus 1.
        // Als ondersteKamerPosY gelijk is aan 0, gebruik dan verdieping 1.
        int verdieping;
        if (ondersteKamerPosY > 0) {
            verdieping = Math.max(1, ondersteKamerPosY - posY + 1);
        } else {
            verdieping = 1;
        }

        // Zoek op welk nummer de volgende kamer op deze verdieping krijgt.
        // Als de verdieping nog niet in de map staat, begin dan bij verdieping * 100 + 1.
        int volgendKamernummer;
        if (volgendeKamernummersPerVerdieping.containsKey(verdieping)) {
            volgendKamernummer = volgendeKamernummersPerVerdieping.get(verdieping);
        } else {
            // Verdieping 1 begint bij 101, verdieping 2 bij 201, etc.
            volgendKamernummer = verdieping * 100 + 1;
        }

        // Sla het volgende nummer op voor de volgende kamer op deze verdieping (huidige + 1).
        volgendeKamernummersPerVerdieping.put(verdieping, volgendKamernummer + 1);
        return volgendKamernummer;
    }
}
