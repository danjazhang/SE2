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

        int x = huidig.x;
        int y = huidig.y;

        // =========================
        // zelfde verdieping
        // =========================
        if (huidig.y == doel.y) {

            if (x < doel.x) x++;
            else if (x > doel.x) x--;

            return layout.krijgVakje(x, y);
        }

        // =========================
        // TRAP → altijd toegestaan
        // =========================
        if (huidig.ruimte instanceof Model.ruimte.Trap) {

            if (y < doel.y) y++;
            else if (y > doel.y) y--;

            return layout.krijgVakje(x, y);
        }

        // =========================
        // LIFT → alleen als persoon in passagierslijst zit
        // =========================
        if (huidig.ruimte instanceof Model.ruimte.Lift) {

            Lift lift = hotel.lift;

            // zoek of iemand op dit vakje echt in lift zit
            boolean zitInLift = false;

            for (Persoon p : lift.getPassagiers()) {

                if (p.huidigVakje == huidig) {
                    zitInLift = true;
                    break;
                }
            }

            if (!zitInLift) {
                return null;
            }

            if (y < doel.y) y++;
            else if (y > doel.y) y--;

            return layout.krijgVakje(x, y);
        }

        return null;
    }

    public void zetRoute(Persoon p, Ruimte r) {

        Vakje start = p.huidigVakje;
        Vakje doel = layout.krijgVakje(r.posX, r.posY);

        if (start == null || doel == null) return;

        List<Vakje> route;

        if (start.y == doel.y) {

            route = List.of(doel);

        } else if (p instanceof Schoonmaker) {

            route = trap(start, doel);

        } else {

            int trap = trapTijd(start, doel);
            int lift = liftTijd(start, doel);

            // lichte menselijke onzekerheid
            trap += Math.random() * 2;
            lift += Math.random() * 2;

            boolean liftBeter = lift < trap;

            if (Math.abs(start.y - doel.y) > 6) {
                liftBeter = true;
            }

            if (liftBeter) {

                route = lift(start, doel);

                if (p instanceof Gast g) {
                    g.gebruiktLift = true;
                }

                if (hotel.lift != null) {
                    hotel.lift.roep(p, start.y);
                }

            } else {
                route = trap(start, doel);
            }
        }

        if (route.isEmpty()) return;

        p.zetDoel(route.get(0));

        for (int i = 1; i < route.size(); i++) {
            p.voegTussendoelToe(route.get(i));
        }
    }

    private List<Vakje> lift(Vakje start, Vakje doel) {

        List<Vakje> r = new ArrayList<>();

        Vakje lift = vindLift(start.y);
        if (lift != null) r.add(lift);

        r.add(doel);

        return r;
    }

    private List<Vakje> trap(Vakje start, Vakje doel) {

        List<Vakje> r = new ArrayList<>();

        Vakje t1 = vindTrap(start.y);
        Vakje t2 = vindTrap(doel.y);

        if (t1 != null) r.add(t1);
        if (t2 != null && !t2.equals(t1)) r.add(t2);

        r.add(doel);

        return r;
    }

    private int trapTijd(Vakje s, Vakje d) {
        return Math.abs(s.y - d.y) * hotel.trap.tijdperverdieping;
    }

    private int liftTijd(Vakje s, Vakje d) {

        Lift l = hotel.lift;

        int afstandLift = Math.abs(l.getHuidigeVerdieping() - s.y);
        int rit = Math.abs(s.y - d.y);
        int wachtrij = l.aantalWachtend(s.y);

        return afstandLift + rit + wachtrij;
    }

    private Vakje vindLift(int y) {
        for (Ruimte r : hotel.ruimtes)
            if (r instanceof Lift)
                return layout.krijgVakje(r.posX, y);
        return null;
    }

    private Vakje vindTrap(int y) {
        for (Ruimte r : hotel.ruimtes)
            if (r instanceof Trap)
                return layout.krijgVakje(r.posX, y);
        return null;
    }
}