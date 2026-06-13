package Model;

import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Bioscoop;
import Model.ruimte.Fitnessruimte;
import Model.ruimte.Kamer;
import Model.ruimte.Restaurant;
import Model.ruimte.Ruimte;

// Verantwoordelijkheid: gasten naar de juiste ruimte sturen op basis van events.
// Zoekt de gast op, zoekt de dichtstbijzijnde ruimte van het gevraagde type,
// en berekent de route via de pathfinder.
public class GastRoutingService {

    // Het hotel zodat we gasten en ruimtes kunnen opzoeken.
    private Hotel hotel;

    // Constructor: sla het hotel op.
    public GastRoutingService(Hotel hotel) {
        this.hotel = hotel;
    }

    // Stuur de gast met het opgegeven gastId naar het dichtstbijzijnde restaurant.
    // Geef dat restaurant terug zodat de aanroeper hem kan registreren.
    // Als de gast niet gevonden wordt of geen positie heeft, geef null terug.
    public Restaurant stuurNaarRestaurant(int gastId) {
        Gast gast = vindGast(gastId);
        if (gast == null || gast.huidigVakje == null) return null;
        Ruimte doelRuimte = vindDichtstbijzijndeRuimte(gast, "restaurant");
        if (doelRuimte == null) return null;
        hotel.pathfinder.zetRoute(gast, doelRuimte);
        // Cast de ruimte naar Restaurant zodat we hem als Restaurant kunnen teruggeven.
        return (Restaurant) doelRuimte;
    }

    // Stuur de gast met het opgegeven gastId naar de dichtstbijzijnde fitnessruimte.
    // Geef die fitnessruimte terug. Als niks gevonden wordt, geef null terug.
    public Fitnessruimte stuurNaarFitness(int gastId) {
        Gast gast = vindGast(gastId);
        if (gast == null || gast.huidigVakje == null) return null;
        Ruimte doelRuimte = vindDichtstbijzijndeRuimte(gast, "fitness");
        if (doelRuimte == null) return null;
        hotel.pathfinder.zetRoute(gast, doelRuimte);
        return (Fitnessruimte) doelRuimte;
    }

    // Stuur de gast met het opgegeven gastId naar de dichtstbijzijnde bioscoop.
    // Geef die bioscoop terug. Als niks gevonden wordt, geef null terug.
    public Bioscoop stuurNaarBioscoop(int gastId) {
        Gast gast = vindGast(gastId);
        if (gast == null || gast.huidigVakje == null) return null;
        Ruimte doelRuimte = vindDichtstbijzijndeRuimte(gast, "bioscoop");
        if (doelRuimte == null) return null;
        hotel.pathfinder.zetRoute(gast, doelRuimte);
        return (Bioscoop) doelRuimte;
    }

    // Stuur de gast met het opgegeven gastId terug naar zijn eigen kamer.
    // Als het hotel of de pathfinder leeg is (null), stop dan.
    // Als de gast geen kamer heeft (null), stop dan.
    public void stuurTerugNaarKamer(int gastId) {
        if (hotel == null || hotel.pathfinder == null) return;
        Gast gast = vindGast(gastId);
        if (gast == null) return;
        Kamer kamer = gast.kamer;
        if (kamer == null) return;
        hotel.pathfinder.zetRoute(gast, kamer);
    }

    // Zoek een gast op door de personenlijst van het hotel door te lopen.
    // 'p instanceof Gast' betekent: als de persoon een Gast is.
    // '((Gast) p).gastId == gastId' betekent: het gastId van deze gast is gelijk aan het gezochte gastId.
    private Gast vindGast(int gastId) {
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                return (Gast) p;
            }
        }
        return null;
    }

    // Zoek de dichtstbijzijnde ruimte van het opgegeven type ten opzichte van de gast.
    // Berekent de afstand via de Manhattan-formule: |x1-x2| + |y1-y2|.
    // 'Math.abs(...)' geeft de absolute waarde (altijd positief).
    // 'Integer.MAX_VALUE' is de grootste mogelijke int-waarde zodat elke echte afstand kleiner is.
    private Ruimte vindDichtstbijzijndeRuimte(Gast gast, String ruimteType) {
        Ruimte dichtstbij = null;
        int minAfstand = Integer.MAX_VALUE;

        for (Ruimte r : hotel.ruimtes) {
            // Controleer of de ruimte het juiste type is.
            boolean isJuisteType = false;
            if (ruimteType.equals("restaurant") && r instanceof Restaurant) isJuisteType = true;
            if (ruimteType.equals("fitness") && r instanceof Fitnessruimte) isJuisteType = true;
            if (ruimteType.equals("bioscoop") && r instanceof Bioscoop) isJuisteType = true;

            if (isJuisteType) {
                // Bereken de afstand tussen de ruimte en de gast.
                int afstand = Math.abs(r.posX - gast.huidigVakje.x) + Math.abs(r.posY - gast.huidigVakje.y);
                // Als deze afstand kleiner is dan de huidige minimale afstand, sla deze ruimte op als dichtstbij.
                if (afstand < minAfstand) {
                    minAfstand = afstand;
                    dichtstbij = r;
                }
            }
        }
        return dichtstbij;
    }
}
