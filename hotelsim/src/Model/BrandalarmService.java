package Model;

import Model.layout.Vakje;
import Model.persoon.Persoon;

// Verantwoordelijkheid: brandalarm activeren en alle personen naar de uitgang sturen
// Gebruikt polymorfisme via evacueer() zodat elke persoon zijn eigen gedrag bepaalt
// Gast wist zijn route en loopt naar buiten
// Schoonmaker onthoudt zijn kamer en loopt ook naar buiten
public class BrandalarmService {

    private Hotel hotel;
    private ILogger logger;

    public BrandalarmService(Hotel hotel, ILogger logger) {
        this.hotel = hotel;
        this.logger = logger;
    }

    // activeer het brandalarm: zet lift uit en stuur iedereen naar de uitgang
    public void activeer(int tijd) {
        // markeer het alarm als actief in het hotel
        hotel.brandalarmActief = true;

        // zet de lift buiten gebruik zodat niemand er meer in kan
        if (hotel.lift != null) {
            hotel.lift.zetUitBedrijf(true);
        }

        // zoek de uitgang eenmalig op zodat we hem niet per persoon opnieuw hoeven te zoeken
        Vakje uitgang = vindUitgang();

        // roep evacueer() aan op elke persoon
        // elke subklasse beslist zelf wat er extra gebeurt via @Override
        for (Persoon p : hotel.personen) {
            p.evacueer(uitgang, hotel.pathfinder);
        }

        if (logger != null) logger.log("[" + tijd + "] BRANDALARM: iedereen evacueren via de trap!");
    }

    // zoek het uitgang vakje: het onderste vakje van de lobby
    private Vakje vindUitgang() {
        if (hotel.lobby == null) return null;
        return hotel.layout.krijgVakje(hotel.lobby.posX, hotel.lobby.posY);
    }
}
