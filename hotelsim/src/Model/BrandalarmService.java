package Model;

import Model.layout.Vakje;
import Model.persoon.Persoon;

// Verantwoordelijkheid: het brandalarm activeren en alle personen naar de uitgang sturen.

public class BrandalarmService {

    private Hotel hotel;

    private ILogger logger;

    // Constructor van BrandalarmService met hotel en logger als parameters.
    public BrandalarmService(Hotel hotel, ILogger logger) {
        // slaat op
        this.hotel = hotel;
        this.logger = logger;
    }

    // start alarm
    public void activeer(int tijd) {
        // Zet brandalarmActief op true zodat geen nieuwe activiteiten naar gasten gestuurd worden.
        hotel.brandalarmActief = true;

        // Als de lift bestaat (niet null), zet hem dan buiten gebruik via zetUitBedrijf(true).
        if (hotel.lift != null) {
            hotel.lift.zetUitBedrijf(true);
        }

        // Maak een variabele uitgang en haal de uitgang op via vindUitgang
        Vakje uitgang = vindUitgang();

        // loop personen in hotel
        for (Persoon p : hotel.personen) {
            //Roep evacueer aan op elke persoon en geef uitgang en pathfinder mee.
            p.evacueer(uitgang, hotel.pathfinder);
        }
        if (logger != null) logger.log("[" + tijd + "] BRANDALARM: iedereen evacueren via de trap!");
    }


    // bepaalt waar de uitgang van het hotel is in het grid.
    private Vakje vindUitgang() {
        if (hotel.lobby == null) return null;
        return hotel.layout.krijgVakje(hotel.lobby.posX, hotel.lobby.posY);
    }
}
