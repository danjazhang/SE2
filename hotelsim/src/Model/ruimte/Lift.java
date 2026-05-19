package Model.ruimte;

import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;

import java.util.*;

public class Lift extends Ruimte {

    private int huidigeVerdieping = 1;
    private int lobbyVerdieping = 1;
    private int doelVerdieping = 1;

    public static final int TICKS_PER_VERDIEPING = 2;
    public static final int TICKS_INSTAPPEN = 1;

    private List<Persoon> passagiers = new ArrayList<>();

    // wachtrij per verdieping (simpel en stabiel)
    private Map<Integer, Queue<Persoon>> wachtrijen = new HashMap<>();

    public Lift() {
        this.huidigeVerdieping = 1;
        this.lobbyVerdieping = 1;
        this.doelVerdieping = 1;
    }

    public void initWachtrijen(int maxVerdiepingen) {
        for (int i = 1; i <= maxVerdiepingen; i++) {
            wachtrijen.put(i, new LinkedList<>());
        }
    }

    public void roep(Persoon p, int verdieping) {
        wachtrijen.putIfAbsent(verdieping, new LinkedList<>());
        Queue<Persoon> q = wachtrijen.get(verdieping);

        if (!q.contains(p)) {
            q.add(p);
        }
    }

    public void tik() {

        // als er passagiers in lift zitten:
        // rij naar doelverdieping
        if (!passagiers.isEmpty()) {

            beweeg();

            // aangekomen → gasten uitstappen
            if (huidigeVerdieping == doelVerdieping) {
                uitladen();
            }

            return;
        }

        // niemand in lift:
        // zoek volgende verdieping met wachtenden
        int next = vindVolgende();

        // niemand wacht → terug naar lobby
        if (next == -1) {

            doelVerdieping = lobbyVerdieping;
        }
        else {

            doelVerdieping = next;
        }

        // bewegen richting doel
        beweeg();

        // aangekomen bij wachtende gasten
        if (huidigeVerdieping == doelVerdieping) {

            inladen();
        }
    }

    private void beweeg() {
        if (huidigeVerdieping < doelVerdieping) huidigeVerdieping++;
        else if (huidigeVerdieping > doelVerdieping) huidigeVerdieping--;
    }


    private void uitladen() {

        List<Persoon> copy = new ArrayList<>(passagiers);

        for (Persoon p : copy) {

            passagiers.remove(p);

            if (p instanceof Model.persoon.Gast g) {

                g.inLift = false;
            }
        }
    }


    private void inladen() {

        Queue<Persoon> q = wachtrijen.get(huidigeVerdieping);

        if (q == null) return;

        while (!q.isEmpty()) {

            Persoon p = q.poll();

            passagiers.add(p);

            // gast zit nu in lift
            if (p instanceof Model.persoon.Gast g) {

                g.inLift = true;

                // gast tijdelijk van kaart halen
                if (g.huidigVakje != null) {
                    g.huidigVakje.verwijderPersoon(g);
                }

                g.huidigVakje = null;

                // BELANGRIJK:
                // lift weet nu waarheen
                if (g.kamer != null) {
                    doelVerdieping = g.kamer.posY;
                }
            }
        }
    }

    public void board(Persoon p) {
        if (!passagiers.contains(p)) {
            passagiers.add(p);
        }
    }

    public void unboardAll() {
        passagiers.clear();
    }

    private int vindVolgende() {

        int best = -1;
        int dist = Integer.MAX_VALUE;

        for (int f : wachtrijen.keySet()) {

            if (!wachtrijen.get(f).isEmpty()) {

                int d = Math.abs(huidigeVerdieping - f);

                if (d < dist) {
                    dist = d;
                    best = f;
                }
            }
        }

        return best;
    }

    public int getHuidigeVerdieping() {
        return huidigeVerdieping;
    }

    public List<Persoon> getPassagiers() {
        return passagiers;
    }

    public int aantalWachtend(int verdieping) {
        Queue<Persoon> q = wachtrijen.get(verdieping);
        return q == null ? 0 : q.size();
    }
}