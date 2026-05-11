package Model;

import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Bioscoop;
import Model.ruimte.Fitnessruimte;
import Model.ruimte.Restaurant;
import Model.ruimte.Ruimte;

// Verantwoordelijkheid: gasten naar de juiste ruimte sturen op basis van events
// Zoekt de gast op, zoekt de dichtstbijzijnde ruimte van het gevraagde type
// en berekent de route via de pathfinder
public class GastRoutingService {

    private Hotel hotel;

    public GastRoutingService(Hotel hotel) {
        this.hotel = hotel;
    }

    // stuur een gast naar het dichtstbijzijnde restaurant
    public void stuurNaarRestaurant(int gastId) {
        stuurNaarRuimte(gastId, "restaurant");
    }

    // stuur een gast naar de dichtstbijzijnde fitnessruimte
    public void stuurNaarFitness(int gastId) {
        stuurNaarRuimte(gastId, "fitness");
    }

    // stuur een gast naar de dichtstbijzijnde bioscoop
    public void stuurNaarBioscoop(int gastId) {
        stuurNaarRuimte(gastId, "bioscoop");
    }

    // stuur een gast naar de dichtstbijzijnde ruimte van het opgegeven type
    private void stuurNaarRuimte(int gastId, String ruimteType) {
        if (hotel == null || hotel.pathfinder == null) return;

        // zoek de gast op
        Gast gast = vindGast(gastId);
        if (gast == null || gast.huidigVakje == null) return;

        // zoek de dichtstbijzijnde ruimte van het gevraagde type
        Ruimte doelRuimte = vindDichtstbijzijndeRuimte(gast, ruimteType);
        if (doelRuimte == null) return;

        // bereken en zet de route
        hotel.pathfinder.zetRoute(gast, doelRuimte);
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

    // zoek de dichtstbijzijnde ruimte van het opgegeven type
    private Ruimte vindDichtstbijzijndeRuimte(Gast gast, String ruimteType) {
        Ruimte dichtstbij = null;
        int minAfstand = Integer.MAX_VALUE;

        for (Ruimte r : hotel.ruimtes) {
            boolean isJuisteType = false;
            if (ruimteType.equals("restaurant") && r instanceof Restaurant) isJuisteType = true;
            if (ruimteType.equals("fitness") && r instanceof Fitnessruimte) isJuisteType = true;
            if (ruimteType.equals("bioscoop") && r instanceof Bioscoop) isJuisteType = true;

            if (isJuisteType) {
                int afstand = Math.abs(r.posX - gast.huidigVakje.x) + Math.abs(r.posY - gast.huidigVakje.y);
                if (afstand < minAfstand) {
                    minAfstand = afstand;
                    dichtstbij = r;
                }
            }
        }
        return dichtstbij;
    }
}
