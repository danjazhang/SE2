package Model;

import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.*;

import java.util.*;

public class Pathfinder {

    private Layout layout;
    private Hotel hotel;

    public Pathfinder(Hotel hotel) {
        this.hotel = hotel;
        this.layout = hotel.layout;
    }

    public Vakje volgendeStap(Vakje huidig, Vakje doel) {

        if (huidig == null || doel == null) return null;

        int x = huidig.x;
        int y = huidig.y;

        // Zelfde verdieping: loop horizontaal
        if (y == doel.y) {
            if (x < doel.x) x++;
            else if (x > doel.x) x--;
            return layout.krijgVakje(x, y);
        }

        // Op trap: beweeg verticaal
        if (huidig.ruimte instanceof Trap) {
            if (y < doel.y) y++;
            else if (y > doel.y) y--;
            return layout.krijgVakje(x, y);
        }

        return null;
    }

    public void zetRoute(Persoon p, Ruimte bestemming) {

        Vakje start = p.huidigVakje;
        Vakje doel = layout.krijgVakje(bestemming.posX, bestemming.posY);

        if (start == null || doel == null) return;

        // Sla eindbestemming op bij gast
        if (p instanceof Gast g) {
            g.eindbestemming = bestemming;
        }

        // Zelfde verdieping: direct lopen
        if (start.y == doel.y) {
            p.zetDoel(doel);
            return;
        }

        // Schoonmaker gebruikt altijd trap
        if (p instanceof Schoonmaker) {
            routeViaTrap(p, start, doel);
            return;
        }

        // Gast: kies lift of trap
        int trapTijd = Math.abs(start.y - doel.y) * hotel.trap.tijdperverdieping;
        int liftTijd = schatLiftTijd(start, doel);

        if (liftTijd < trapTijd || Math.abs(start.y - doel.y) > 4) {
            routeViaLift(p, start, doel);
        } else {
            routeViaTrap(p, start, doel);
        }
    }

    private Vakje vindLiftWachtplek(int y) {

        for (Ruimte r : hotel.ruimtes) {

            if (r instanceof Lift) {

                // Rechts van lift
                return layout.krijgVakje(r.posX + 1, y);
            }
        }

        return null;
    }

    private void routeViaLift(Persoon p, Vakje start, Vakje doel) {

        Gast g = (Gast) p;

        // Vind liftvakje op huidige verdieping
        //Vakje liftVakje = vindLift(start.y);
        Vakje liftVakje = vindLiftWachtplek(start.y);
        if (liftVakje == null) {
            routeViaTrap(p, start, doel);
            return;
        }

        g.gebruiktLift = true;
        g.gewensteVerdieping = doel.y;

        // Loop eerst naar lift
        p.zetDoel(liftVakje);

        // Roep lift
        hotel.lift.roep(p, start.y);
    }

    private void routeViaTrap(Persoon p, Vakje start, Vakje doel) {

        Vakje trap1 = vindTrap(start.y);
        Vakje trap2 = vindTrap(doel.y);

        if (trap1 != null) {
            p.zetDoel(trap1);
        }
        if (trap2 != null && !trap2.equals(trap1)) {
            p.voegTussendoelToe(trap2);
        }
        p.voegTussendoelToe(doel);
    }

    private int schatLiftTijd(Vakje start, Vakje doel) {
        Lift lift = hotel.lift;
        int wacht = Math.abs(lift.getHuidigeVerdieping() - start.y);
        int rit = Math.abs(start.y - doel.y);
        int queue = lift.aantalWachtend(start.y);
        return wacht + rit + queue;
    }

    private Vakje vindLift(int y) {
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Lift) {
                return layout.krijgVakje(r.posX, y);
            }
        }
        return null;
    }

    private Vakje vindTrap(int y) {
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Trap) {
                return layout.krijgVakje(r.posX, y);
            }
        }
        return null;
    }
}
