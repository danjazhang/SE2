package Model;

import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Bioscoop;
import Model.ruimte.Fitnessruimte;
import Model.ruimte.Kamer;
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

    // stuur een gast naar het dichtstbijzijnde niet-volle restaurant
    // als alle restaurants vol zijn: laat de gast wachten buiten het dichtstbijzijnde restaurant
    public Restaurant stuurNaarRestaurant(int gastId) {
        Gast gast = vindGast(gastId);
        if (gast == null || gast.huidigVakje == null) return null;
        // Tijdens de terugkeerfase na een brandalarm negeren we nieuwe activiteiten tijdelijk.
        // Eerst moet de gast weer ordelijk het hotel in en terug naar zijn normale toestand.
        if (gast.keertTerugNaAlarm) return null;

        // zoek dichtstbijzijnd niet-vol restaurant
        Restaurant doelRuimte = (Restaurant) vindDichtstbijzijndeNietVolleRuimte(gast, "restaurant");
        if (doelRuimte != null) {
            gast.wachtOpRestaurant = false;
            gast.wachtRestaurant = null;
            hotel.pathfinder.zetRoute(gast, doelRuimte);
            return doelRuimte;
        }

        // alle restaurants vol: laat gast wachten bij het dichtstbijzijnde restaurant
        Restaurant dichtstbij = (Restaurant) vindDichtstbijzijndeRuimte(gast, "restaurant");
        if (dichtstbij != null) {
            gast.wachtOpRestaurant = true;
            gast.wachtRestaurant = dichtstbij;
            int[] ingang = dichtstbij.krijgIngang();
            int ix = ingang[0] != 0 ? ingang[0] : dichtstbij.posX;
            int iy = ingang[1] != 0 ? ingang[1] : dichtstbij.posY;
            Model.layout.Vakje wachtVakje = hotel.layout.krijgVakje(ix, iy);
            if (wachtVakje != null) hotel.pathfinder.zetRouteTrap(gast, wachtVakje);
        }
        return dichtstbij;
    }

    // stuur een gast naar de dichtstbijzijnde fitnessruimte en geef die terug
    public Fitnessruimte stuurNaarFitness(int gastId) {
        Gast gast = vindGast(gastId);
        if (gast == null || gast.huidigVakje == null) return null;
        // Ook fitness-events worden tijdelijk genegeerd zolang de gast nog terugkeert na evacuatie.
        if (gast.keertTerugNaAlarm) return null;
        Ruimte doelRuimte = vindDichtstbijzijndeRuimte(gast, "fitness");
        if (doelRuimte == null) return null;
        hotel.pathfinder.zetRoute(gast, doelRuimte);
        return (Fitnessruimte) doelRuimte;
    }

    // stuur een gast naar de dichtstbijzijnde bioscoop en geef die terug
    public Bioscoop stuurNaarBioscoop(int gastId) {
        Gast gast = vindGast(gastId);
        if (gast == null || gast.huidigVakje == null) return null;
        // Ook bioscoop-events worden tijdelijk genegeerd zolang de gast nog terugkeert na evacuatie.
        if (gast.keertTerugNaAlarm) return null;
        Ruimte doelRuimte = vindDichtstbijzijndeRuimte(gast, "bioscoop");
        if (doelRuimte == null) return null;
        hotel.pathfinder.zetRoute(gast, doelRuimte);
        return (Bioscoop) doelRuimte;
    }

    // stuur een gast terug naar zijn kamer na een activiteit
    public void stuurTerugNaarKamer(int gastId) {
        if (hotel == null || hotel.pathfinder == null) return;
        Gast gast = vindGast(gastId);
        if (gast == null) return;
        Kamer kamer = gast.kamer;
        if (kamer == null) return;
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

    // zoek de dichtstbijzijnde niet-volle ruimte van het opgegeven type
    private Ruimte vindDichtstbijzijndeNietVolleRuimte(Gast gast, String ruimteType) {
        Ruimte dichtstbij = null;
        int minAfstand = Integer.MAX_VALUE;

        for (Ruimte r : hotel.ruimtes) {
            boolean isJuisteType = false;
            if (ruimteType.equals("restaurant") && r instanceof Restaurant) {
                if (!((Restaurant) r).isVol()) isJuisteType = true;
            }
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
