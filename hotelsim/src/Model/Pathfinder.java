package Model;

import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.*;
import java.util.*;

/**
 * Pathfinder met BFS-routeberekening.
 *
 * Grid-structuur per verdieping (behalve begane grond):
 *   ruimte-rij  y=2N-1  ← kamers, restaurant, etc.
 *   gang-rij    y=2N    ← gang loopt volledig door (x=2..gridBreedte-2)
 *
 * Multi-verdieping ruimtes bezetten alleen hun ruimte-rijen (JSON-hoogte),
 * NIET de gang-rijen ertussen. De gang loopt altijd ononderbroken door.
 *
 * Personen lopen via de gang (horizontaal) en de trap (verticaal).
 * Om een kamer te betreden: loop op de gang naar kamer-x, stap omhoog (y-1).
 */
public class Pathfinder {

    private final Layout layout;
    private final Hotel hotel;

    public Pathfinder(Hotel hotel) {
        this.hotel = hotel;
        this.layout = hotel.layout;
    }

    // -----------------------------------------------------------------------
    // volgendeStap: één stap richting het huidige doelvakje
    // -----------------------------------------------------------------------
    public Vakje volgendeStap(Vakje huidig, Vakje doel) {
        if (huidig == null || doel == null) return null;

        int x = huidig.x;
        int y = huidig.y;

        // Op de trap: beweeg verticaal richting doel-y
        if (huidig.ruimte instanceof Trap) {
            if (y < doel.y) return layout.krijgVakje(x, y + 1);
            if (y > doel.y) return layout.krijgVakje(x, y - 1);
            if (x < doel.x) return layout.krijgVakje(x + 1, y);
            if (x > doel.x) return layout.krijgVakje(x - 1, y);
            return null;
        }

        // Zelfde rij als doel: beweeg horizontaal
        if (y == doel.y) {
            if (x < doel.x) return layout.krijgVakje(x + 1, y);
            if (x > doel.x) return layout.krijgVakje(x - 1, y);
            return null;
        }

        // In een ruimte (niet gang/lift/trap/lobby): ga omlaag naar gang
        if (huidig.ruimte != null
                && !(huidig.ruimte instanceof Gang)
                && !(huidig.ruimte instanceof Lift)
                && !(huidig.ruimte instanceof Trap)
                && !(huidig.ruimte instanceof Lobby)) {
            return layout.krijgVakje(x, y + 1);
        }

        // Op gang/lobby/lift: beweeg horizontaal naar doel-x
        if (x != doel.x) {
            if (x < doel.x) return layout.krijgVakje(x + 1, y);
            return layout.krijgVakje(x - 1, y);
        }

        // Zelfde x, andere y: stap omhoog naar kamer (y-1)
        if (y > doel.y) return layout.krijgVakje(x, y - 1);

        // Fallback: ga naar de trap
        Vakje trap = vindTrapVakje(y);
        if (trap != null) {
            if (x < trap.x) return layout.krijgVakje(x + 1, y);
            if (x > trap.x) return layout.krijgVakje(x - 1, y);
            if (y < doel.y) return layout.krijgVakje(x, y + 1);
            if (y > doel.y) return layout.krijgVakje(x, y - 1);
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // zetRoute: berekent de volledige route via BFS
    // -----------------------------------------------------------------------
    public void zetRoute(Persoon p, Ruimte bestemming) {
        Vakje start = p.huidigVakje;
        if (start == null) return;

        if (p instanceof Gast g) g.eindbestemming = bestemming;

        int[] ingang = bestemming.krijgIngang();
        Vakje doel = layout.krijgVakje(ingang[0], ingang[1]);
        if (doel == null) doel = layout.krijgVakje(bestemming.posX, bestemming.posY);
        if (doel == null) return;

        berekenEnZetRoute(p, start, doel, p instanceof Schoonmaker);
    }

    public void zetRouteTrap(Persoon p, Vakje doel) {
        Vakje start = p.huidigVakje;
        if (start == null || doel == null) return;
        berekenEnZetRoute(p, start, doel, true);
    }

    // -----------------------------------------------------------------------
    // Interne routeberekening
    // -----------------------------------------------------------------------

    private void berekenEnZetRoute(Persoon p, Vakje start, Vakje doel, boolean alleenTrap) {
        // Gasten op andere verdieping: kies lift of trap
        if (!alleenTrap && p instanceof Gast g) {
            int startGang = vindGangRij(start.y);
            int doelGang  = vindGangRij(doel.y);
            boolean zelfdeVerdieping = (start.y == doel.y)
                    || (startGang != -1 && startGang == doelGang);

            if (!zelfdeVerdieping) {
                int trapTijd = Math.abs(start.y - doel.y) * hotel.trap.tijdperverdieping;
                int liftTijd = schatLiftTijd(start, doel);
                if (liftTijd < trapTijd || Math.abs(start.y - doel.y) > 4) {
                    routeViaLift(g, start, doel, startGang, doelGang);
                    return;
                }
            }
        }

        // BFS voor de route
        List<Vakje> pad = bfs(start, doel);
        if (pad == null || pad.isEmpty()) return;

        List<Vakje> waypoints = vereenvoudigPad(pad);
        if (waypoints.isEmpty()) return;

        p.zetDoel(waypoints.get(0));
        for (int i = 1; i < waypoints.size(); i++) {
            p.voegTussendoelToe(waypoints.get(i));
        }
    }

    /**
     * BFS van start naar doel.
     * Beweegbare vakjes: gang, lift, trap, lobby, leeg, of het exacte doelvakje.
     */
    private List<Vakje> bfs(Vakje start, Vakje doel) {
        if (start.equals(doel)) return Collections.emptyList();

        Map<Vakje, Vakje> vorige = new HashMap<>();
        Queue<Vakje> wachtrij = new LinkedList<>();
        vorige.put(start, null);
        wachtrij.add(start);

        while (!wachtrij.isEmpty()) {
            Vakje huidig = wachtrij.poll();
            if (huidig.equals(doel)) return reconstrueerPad(vorige, start, doel);

            for (Vakje buur : getBuren(huidig, doel)) {
                if (!vorige.containsKey(buur)) {
                    vorige.put(buur, huidig);
                    wachtrij.add(buur);
                }
            }
        }
        return null;
    }

    /**
     * Geeft de beweegbare buren van een vakje.
     * - Horizontaal: altijd als buur betreedbaar is
     * - Verticaal omlaag: vanuit een ruimte (naar gang) of via trap
     * - Verticaal omhoog: alleen als het doelvakje direct boven ons is, of via trap
     */
    private List<Vakje> getBuren(Vakje huidig, Vakje doel) {
        List<Vakje> buren = new ArrayList<>();
        int x = huidig.x;
        int y = huidig.y;

        // Horizontale buren
        voegToe(buren, layout.krijgVakje(x - 1, y), doel);
        voegToe(buren, layout.krijgVakje(x + 1, y), doel);

        // Verticale buren
        if (huidig.ruimte instanceof Trap) {
            // Op trap: beweeg verticaal
            voegToe(buren, layout.krijgVakje(x, y - 1), doel);
            voegToe(buren, layout.krijgVakje(x, y + 1), doel);
        } else if (huidig.ruimte != null
                && !(huidig.ruimte instanceof Gang)
                && !(huidig.ruimte instanceof Lift)
                && !(huidig.ruimte instanceof Lobby)) {
            // In een ruimte: ga omlaag naar gang
            voegToe(buren, layout.krijgVakje(x, y + 1), doel);
        } else {
            // Op gang/lift/lobby: ga omhoog als doel direct boven ons is
            Vakje boven = layout.krijgVakje(x, y - 1);
            if (boven != null && boven.equals(doel)) buren.add(boven);
            // Ga omlaag als doel direct onder ons is
            Vakje onder = layout.krijgVakje(x, y + 1);
            if (onder != null && onder.equals(doel)) buren.add(onder);
        }

        return buren;
    }

    private void voegToe(List<Vakje> buren, Vakje v, Vakje doel) {
        if (v == null) return;
        if (isBetreedbaar(v, doel)) buren.add(v);
    }

    private boolean isBetreedbaar(Vakje v, Vakje doel) {
        if (v == null) return false;
        if (v.equals(doel)) return true;
        if (v.ruimte == null) return true;
        return v.ruimte instanceof Gang
            || v.ruimte instanceof Lift
            || v.ruimte instanceof Trap
            || v.ruimte instanceof Lobby;
    }

    private List<Vakje> reconstrueerPad(Map<Vakje, Vakje> vorige, Vakje start, Vakje doel) {
        List<Vakje> pad = new ArrayList<>();
        Vakje huidig = doel;
        while (huidig != null && !huidig.equals(start)) {
            pad.add(huidig);
            huidig = vorige.get(huidig);
        }
        Collections.reverse(pad);
        return pad;
    }

    /**
     * Vereenvoudigt een pad: bewaar alleen waypoints (richtingsveranderingen + eindpunt).
     */
    private List<Vakje> vereenvoudigPad(List<Vakje> pad) {
        if (pad.size() <= 1) return new ArrayList<>(pad);

        List<Vakje> waypoints = new ArrayList<>();
        waypoints.add(pad.get(0));

        for (int i = 1; i < pad.size() - 1; i++) {
            int dx1 = pad.get(i).x - pad.get(i - 1).x;
            int dy1 = pad.get(i).y - pad.get(i - 1).y;
            int dx2 = pad.get(i + 1).x - pad.get(i).x;
            int dy2 = pad.get(i + 1).y - pad.get(i).y;
            if (dx1 != dx2 || dy1 != dy2) {
                waypoints.add(pad.get(i));
            }
        }

        waypoints.add(pad.get(pad.size() - 1));
        return waypoints;
    }

    // -----------------------------------------------------------------------
    // Lift-route
    // -----------------------------------------------------------------------
    private void routeViaLift(Gast g, Vakje start, Vakje doel, int startGang, int doelGang) {
        int doelY  = doelGang  != -1 ? doelGang  : doel.y;
        int wachtY = startGang != -1 ? startGang : start.y;

        g.gewensteVerdieping = doelY;
        g.gebruiktLift = true;

        Vakje wachtplek = layout.krijgVakje(hotel.lift.posX + 1, wachtY);
        if (wachtplek == null) {
            berekenEnZetRoute(g, start, doel, true);
            return;
        }

        g.zetDoel(wachtplek);
        hotel.lift.roep(g, wachtY);
    }

    // -----------------------------------------------------------------------
    // Hulpmethoden
    // -----------------------------------------------------------------------

    public int vindGangRij(int y) {
        for (int x = 2; x <= hotel.breedte - 2; x++) {
            Vakje v = layout.krijgVakje(x, y);
            if (v != null && v.ruimte instanceof Gang) return y;
        }
        int testY = y + 1;
        if (testY < hotel.hoogte) {
            for (int x = 2; x <= hotel.breedte - 2; x++) {
                Vakje v = layout.krijgVakje(x, testY);
                if (v != null && v.ruimte instanceof Gang) return testY;
            }
        }
        return -1;
    }

    private int schatLiftTijd(Vakje start, Vakje doel) {
        Lift lift = hotel.lift;
        int wacht = Math.abs(lift.getHuidigeVerdieping() - start.y);
        int rit   = Math.abs(start.y - doel.y);
        int queue = lift.aantalWachtend(start.y);
        return wacht + rit + queue;
    }

    private Vakje vindTrapVakje(int y) {
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Trap) return layout.krijgVakje(r.posX, y);
        }
        return null;
    }
}
