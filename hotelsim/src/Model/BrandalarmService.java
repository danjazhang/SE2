package Model;

import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;

// Verantwoordelijkheid: brandalarm activeren en alle personen naar de uitgang sturen
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

        // stuur alle personen naar de uitgang via de trap
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast gast) {
                stuurNaarUitgang(gast);
            } else if (p instanceof Schoonmaker schoonmaker) {
                // schoonmaker onthoudt zijn kamer maar gaat ook naar buiten
                stuurNaarUitgang(schoonmaker);
            }
        }

        if (logger != null) logger.log("[" + tijd + "] BRANDALARM: iedereen evacueren via de trap!");
    }

    // stuur een persoon naar de uitgang via de trap, lift is niet toegestaan
    private void stuurNaarUitgang(Persoon p) {
        if (p.huidigVakje == null || hotel.pathfinder == null) return;
        // wis de huidige route zodat de persoon niet meer naar zijn oude bestemming loopt
        p.wisRoute();
        Vakje uitgang = vindUitgang();
        if (uitgang != null) {
            // gebruik altijd de trap, nooit de lift
            hotel.pathfinder.zetRouteTrap(p, uitgang);
        }
    }

    // zoek het uitgang vakje: het onderste vakje van de lobby
    private Vakje vindUitgang() {
        if (hotel.lobby == null) return null;
        return hotel.layout.krijgVakje(hotel.lobby.posX, hotel.lobby.posY);
    }
}
