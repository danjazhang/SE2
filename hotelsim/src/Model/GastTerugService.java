package Model;

import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Kamer;

// Verantwoordelijkheid: een gast terugsturen naar zijn kamer na een activiteit
public class GastTerugService {

    private Hotel hotel;

    public GastTerugService(Hotel hotel) {
        this.hotel = hotel;
    }

    // stuur de gast met het opgegeven id terug naar zijn kamer
    public void stuurTerugNaarKamer(int gastId) {
        if (hotel == null || hotel.pathfinder == null) return;

        // zoek de gast op
        Gast gast = vindGast(gastId);
        if (gast == null) return;

        // gast heeft geen kamer (bijv. al uitgecheckt)
        Kamer kamer = gast.kamer;
        if (kamer == null) return;

        // bereken route terug naar de kamer
        hotel.pathfinder.zetRoute(gast, kamer);
    }

    // zoek een gast op basis van id
    private Gast vindGast(int gastId) {
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                return (Gast) p;
            }
        }
        return null;
    }
}
