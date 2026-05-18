package Model.service;

import Model.Hotel;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;

// Verantwoordelijkheid: alle businesslogica voor een check-out uitvoeren.
// Deze service bewaart dus de volgorde van de use-case:
// gast zoeken en de kamer correct vrijmaken.
public class CheckOutService {

    // Het hotelmodel is nodig om personen te zoeken en kamers los te koppelen.
    private final Hotel hotel;

    public CheckOutService(Hotel hotel) {
        this.hotel = hotel;
    }

    // Voer een volledige check-out uit:
    // zoek de gast en maak de kamer vrij.
    // De schoonmaaktoewijzing hoort nu ergens anders thuis,
    // zodat deze service echt alleen check-outlogica bewaart.
    public CheckOutResult checkOutGast(int gastId) {
        Gast gast = vindGast(gastId);
        if (gast == null) return new CheckOutResult(null, null);

        // Bewaar eerst de kamer, want na het ontkoppelen weet de gast zelf niet meer
        // welke kamer hij net verlaten heeft.
        Kamer kamer = gast.kamer;
        if (kamer != null) {
            kamer.ontkoppelGast(gast);
        }

        return new CheckOutResult(kamer, null);
    }

    // Zoek in de personenlijst de gast met het juiste guestId.
    private Gast vindGast(int gastId) {
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                return (Gast) p;
            }
        }
        return null;
    }

    // Klein resultaatobject zodat Lobby alleen nog hoeft te loggen en het model te verversen.
    public static class CheckOutResult {
        // Bewaar welke kamer vrijkwam.
        // De schoonmaker blijft hier als optioneel veld bestaan,
        // zodat de bestaande interface niet onnodig verandert.
        private final Kamer kamer;
        private final Schoonmaker schoonmaker;

        public CheckOutResult(Kamer kamer, Schoonmaker schoonmaker) {
            this.kamer = kamer;
            this.schoonmaker = schoonmaker;
        }

        public Kamer getKamer() {
            return kamer;
        }

        public Schoonmaker getSchoonmaker() {
            return schoonmaker;
        }
    }
}
