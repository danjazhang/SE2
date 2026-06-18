package Model;

import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Bioscoop;
import Model.ruimte.Fitnessruimte;
import Model.ruimte.Kamer;
import Model.ruimte.Restaurant;
import Model.ruimte.Ruimte;

// Verantwoordelijkheid: bepaalt waar een gast naartoe moet op basis van events
// (restaurant, fitness, bioscoop, kamer)
// en stuurt hem via de pathfinder naar de juiste locatie
public class GastRoutingService {

    // referentie naar het hotel zodat we alle gasten en ruimtes kunnen benaderen
    private Hotel hotel;

    // constructor: koppelt deze service aan een specifiek hotel
    public GastRoutingService(Hotel hotel) {
        this.hotel = hotel;
    }

    // RESTAURANT LOGICA

    // stuurt een gast naar het dichtstbijzijnde NIET-volle restaurant
    // als alle restaurants vol zijn → gast wacht bij het dichtstbijzijnde restaurant
    public Restaurant stuurNaarRestaurant(int gastId) {

        // zoek gast op basis van id
        Gast gast = vindGast(gastId);

        // als gast niet bestaat of niet op een vakje staat → stop
        if (gast == null || gast.huidigVakje == null) return null;

        // als gast nog bezig is met terugkeren na brandalarm → geen nieuwe acties
        if (gast.keertTerugNaAlarm) return null;

        // zoek dichtstbijzijnde restaurant dat nog plaats heeft
        Restaurant doelRuimte = (Restaurant) vindDichtstbijzijndeNietVolleRuimte(gast, "restaurant");

        if (doelRuimte != null) {

            // reset eventuele wachtstatus
            gast.wachtOpRestaurant = false;
            gast.wachtRestaurant = null;

            // route naar restaurant berekenen
            hotel.pathfinder.zetRoute(gast, doelRuimte);

            return doelRuimte;
        }

        // fallback: alle restaurants zijn vol

        // zoek dichtstbijzijnde restaurant (ook al is die vol)
        Restaurant dichtstbij = (Restaurant) vindDichtstbijzijndeRuimte(gast, "restaurant");

        if (dichtstbij != null) {

            // gast gaat wachten tot er plek is
            gast.wachtOpRestaurant = true;
            gast.wachtRestaurant = dichtstbij;

            // bepaal ingang van het restaurant (of fallback positie)
            int[] ingang = dichtstbij.krijgIngang();

            // als ingang niet goed gedefinieerd is → gebruik fallback positie
            int ix = ingang[0] != 0 ? ingang[0] : dichtstbij.posX;
            int iy = ingang[1] != 0 ? ingang[1] : dichtstbij.posY;

            // wachtplek op de kaart ophalen
            Model.layout.Vakje wachtVakje = hotel.layout.krijgVakje(ix, iy);

            // stuur gast naar wachtplek
            if (wachtVakje != null) hotel.pathfinder.zetRouteTrap(gast, wachtVakje);
        }

        return dichtstbij;
    }

    // FITNESS LOGICA

    // stuurt gast naar dichtstbijzijnde fitnessruimte
    public Fitnessruimte stuurNaarFitness(int gastId) {
        Gast gast = vindGast(gastId);
        if (gast == null || gast.huidigVakje == null) return null;
        // geen acties tijdens terugkeer na evacuatie
        if (gast.keertTerugNaAlarm) return null;
        // zoek dichtstbijzijnde fitnessruimte
        Ruimte doelRuimte = vindDichtstbijzijndeRuimte(gast, "fitness");
        if (doelRuimte == null) return null;

        // route naar fitness starten
        hotel.pathfinder.zetRoute(gast, doelRuimte);
        return (Fitnessruimte) doelRuimte;
    }

    // ------------------------------------------------------------
    // BIOSCOOP LOGICA
    // ------------------------------------------------------------

    // stuurt gast naar dichtstbijzijnde bioscoop
    public Bioscoop stuurNaarBioscoop(int gastId) {
        Gast gast = vindGast(gastId);
        if (gast == null || gast.huidigVakje == null) return null;
        // geen acties tijdens herstel na brandalarm
        if (gast.keertTerugNaAlarm) return null;
        // zoek dichtstbijzijnde bioscoop
        Ruimte doelRuimte = vindDichtstbijzijndeRuimte(gast, "bioscoop");
        if (doelRuimte == null) return null;

        // route instellen
        hotel.pathfinder.zetRoute(gast, doelRuimte);
        return (Bioscoop) doelRuimte;
    }

    // ------------------------------------------------------------
    // TERUG NAAR KAMER
    // ------------------------------------------------------------

    // stuurt gast terug naar zijn eigen kamer
    public void stuurTerugNaarKamer(int gastId) {

        // veiligheid checks
        if (hotel == null || hotel.pathfinder == null) return;
        Gast gast = vindGast(gastId);
        if (gast == null) return;
        Kamer kamer = gast.kamer;
        if (kamer == null) return;
        // route naar kamer
        hotel.pathfinder.zetRoute(gast, kamer);
    }

    // zoekt gast op basis van ID
    private Gast vindGast(int gastId) {

        for (Persoon p : hotel.personen) {

            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                return (Gast) p;
            }
        }

        return null;
    }

    // zoekt dichtstbijzijnde ruimte van een bepaald type
    private Ruimte vindDichtstbijzijndeRuimte(Gast gast, String ruimteType) {

        Ruimte dichtstbij = null;
        int minAfstand = Integer.MAX_VALUE;

        // loop door alle ruimtes in het hotel
        for (Ruimte r : hotel.ruimtes) {

            boolean isJuisteType = false;

            // check type van ruimte
            if (ruimteType.equals("restaurant") && r instanceof Restaurant) isJuisteType = true;
            if (ruimteType.equals("fitness") && r instanceof Fitnessruimte) isJuisteType = true;
            if (ruimteType.equals("bioscoop") && r instanceof Bioscoop) isJuisteType = true;

            if (isJuisteType) {

                // Manhattan-afstand berekenen (x + y verschil)
                int afstand = Math.abs(r.posX - gast.huidigVakje.x)
                        + Math.abs(r.posY - gast.huidigVakje.y);

                // als dit dichterbij is → opslaan
                if (afstand < minAfstand) {
                    minAfstand = afstand;
                    dichtstbij = r;
                }
            }
        }

        return dichtstbij;
    }

    // zoekt dichtstbijzijnde NIET-volle ruimte van een bepaald type
    private Ruimte vindDichtstbijzijndeNietVolleRuimte(Gast gast, String ruimteType) {

        Ruimte dichtstbij = null;
        int minAfstand = Integer.MAX_VALUE;
        for (Ruimte r : hotel.ruimtes) {
            boolean isJuisteType = false;

            // check type + extra check voor restaurant capaciteit
            if (ruimteType.equals("restaurant") && r instanceof Restaurant) {
                if (!((Restaurant) r).isVol()) isJuisteType = true;
            }
            if (ruimteType.equals("fitness") && r instanceof Fitnessruimte) isJuisteType = true;
            if (ruimteType.equals("bioscoop") && r instanceof Bioscoop) isJuisteType = true;

            if (isJuisteType) {
                // afstand berekenen tot gast
                int afstand = Math.abs(r.posX - gast.huidigVakje.x)
                        + Math.abs(r.posY - gast.huidigVakje.y);

                // kleinste afstand bewaren
                if (afstand < minAfstand) {
                    minAfstand = afstand;
                    dichtstbij = r;
                }
            }
        }

        return dichtstbij;
    }
}