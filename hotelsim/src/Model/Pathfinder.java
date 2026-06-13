package Model;

import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.*;

// Verantwoordelijkheid: routes berekenen voor personen in het hotel.
// Bepaalt of een persoon via de lift of de trap moet gaan,
// en zet de tussendoelen op de persoon via zetDoel() en voegTussendoelToe().
public class Pathfinder {

    // De layout met alle vakjes van het grid.
    private Layout layout;

    // Het hotel met lift, trap en alle ruimtes.
    private Hotel hotel;

    // Constructor: sla het hotel en de layout op.
    public Pathfinder(Hotel hotel) {
        this.hotel = hotel;
        this.layout = hotel.layout;
    }

    // Geef het volgende vakje terug richting het doelvakje, één stap tegelijk.
    // Als huidig of doel leeg is (null), geef null terug.
    // Beweeg eerst horizontaal (zelfde y) totdat x gelijk is, dan verticaal via de trap.
    public Vakje volgendeStap(Vakje huidig, Vakje doel) {
        if (huidig == null || doel == null) return null;
        int x = huidig.x;
        int y = huidig.y;
        // Als y gelijk is aan (==) doel.y staan we op dezelfde verdieping: beweeg horizontaal.
        if (y == doel.y) {
            if (x < doel.x) x++;
            else if (x > doel.x) x--;
            return layout.krijgVakje(x, y);
        }
        // Als de persoon op de trap staat, beweeg dan verticaal.
        if (huidig.ruimte instanceof Trap) {
            if (y < doel.y) y++;
            else if (y > doel.y) y--;
            return layout.krijgVakje(x, y);
        }
        return null;
    }

    // Zet een route naar de opgegeven ruimte: kiest automatisch lift of trap.
    // Schoonmakers gaan altijd via de trap.
    // Gasten gaan via de lift als die sneller is of als het verschil groter is dan 4 verdiepingen.
    public void zetRoute(Persoon p, Ruimte bestemming) {
        Vakje start = p.huidigVakje;
        Vakje doel = layout.krijgVakje(bestemming.posX, bestemming.posY);
        if (start == null || doel == null) return;
        // Sla de eindbestemming op bij de gast zodat hij na de lift weet waar hij heen moet.
        if (p instanceof Gast g) g.eindbestemming = bestemming;
        // Als start en doel op dezelfde verdieping staan (y gelijk aan y), zet gewoon het doel.
        if (start.y == doel.y) { p.zetDoel(doel); return; }
        // Schoonmakers gaan altijd via de trap.
        if (p instanceof Schoonmaker) { routeViaTrap(p, start, doel); return; }
        // Bereken de reistijd via trap en lift, kies dan de snelste route.
        int trapTijd = Math.abs(start.y - doel.y) * hotel.trap.tijdperverdieping;
        int liftTijd = schatLiftTijd(start, doel);
        // Als de lift sneller is (liftTijd kleiner dan trapTijd) of het verschil groter is dan 4, neem de lift.
        if (liftTijd < trapTijd || Math.abs(start.y - doel.y) > 4) {
            routeViaLift(p, start, doel);
        } else {
            routeViaTrap(p, start, doel);
        }
    }

    // Zet een route altijd via de trap, nooit via de lift.
    // Wordt gebruikt tijdens het brandalarm zodat de lift volledig uitgesloten is.
    public void zetRouteTrap(Persoon p, Vakje doel) {
        Vakje start = p.huidigVakje;
        if (start == null || doel == null) return;
        // Als start en doel op dezelfde verdieping staan, zet gewoon het doel zonder trap.
        if (start.y == doel.y) { p.zetDoel(doel); return; }
        routeViaTrap(p, start, doel);
    }

    // Stel de route in via de lift: voeg liftoproep toe en zet het liftvakje als doel.
    // Als er geen liftvakje gevonden wordt, val terug op de trap.
    private void routeViaLift(Persoon p, Vakje start, Vakje doel) {
        Gast g = (Gast) p;
        Vakje liftVakje = vindLiftWachtplek(start.y);
        if (liftVakje == null) { routeViaTrap(p, start, doel); return; }
        // Zet gebruiktLift op true en sla de gewenste verdieping op.
        g.gebruiktLift = true;
        g.gewensteVerdieping = doel.y;
        // Stel het liftvakje in als het doel en roep de lift op voor deze verdieping.
        p.zetDoel(liftVakje);
        hotel.lift.roep(p, start.y);
    }

    // Stel de route in via de trap:
    // trap1 is het trapvakje op de startverdieping,
    // trap2 is het trapvakje op de doelverdieping,
    // dan het uiteindelijke doel.
    private void routeViaTrap(Persoon p, Vakje start, Vakje doel) {
        Vakje trap1 = vindTrap(start.y);
        Vakje trap2 = vindTrap(doel.y);
        if (trap1 != null) p.zetDoel(trap1);
        // Voeg trap2 alleen toe als hij niet hetzelfde is als trap1.
        if (trap2 != null && !trap2.equals(trap1)) p.voegTussendoelToe(trap2);
        p.voegTussendoelToe(doel);
    }

    // Zoek het vakje naast de lift op de opgegeven verdieping (x is lift.posX + 1).
    private Vakje vindLiftWachtplek(int y) {
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Lift) return layout.krijgVakje(r.posX + 1, y);
        }
        return null;
    }

    // Schat de liftreistijd: wachttijd tot de lift aankomt, ritduur en aantal wachtenden in de wachtrij.
    private int schatLiftTijd(Vakje start, Vakje doel) {
        Lift lift = hotel.lift;
        int wacht = Math.abs(lift.getHuidigeVerdieping() - start.y);
        int rit = Math.abs(start.y - doel.y);
        int queue = lift.aantalWachtend(start.y);
        return wacht + rit + queue;
    }

    // Zoek het trapvakje op de opgegeven verdieping.
    private Vakje vindTrap(int y) {
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Trap) return layout.krijgVakje(r.posX, y);
        }
        return null;
    }
}
