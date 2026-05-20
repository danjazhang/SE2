package Model.ruimte;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;

import java.util.*;

public class Lift extends Ruimte {

    private int huidigeVerdieping = 1;
    private int lobbyVerdieping = 1;

    private List<Persoon> passagiers = new ArrayList<>();
    private Map<Integer, Queue<Persoon>> wachtrijen = new HashMap<>();

    private Hotel hotel;

    public Lift(Hotel hotel) {
        this.hotel = hotel;
        this.huidigeVerdieping = 1;
        this.lobbyVerdieping = 1;
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

        // Bepaal doel: eerste passagier of eerste wachtende
        int doel = bepaalDoel();

        // Beweeg één verdieping richting doel
        if (huidigeVerdieping < doel) {
            huidigeVerdieping++;
        } else if (huidigeVerdieping > doel) {
            huidigeVerdieping--;
        }

        // Laat passagiers uitstappen die hier moeten zijn
        uitladen();

        // Laad wachtenden in op deze verdieping
        inladen();
    }

    private int bepaalDoel() {

        // Passagiers hebben voorrang
        for (Persoon p : passagiers) {
            if (p instanceof Gast g) {
                return g.gewensteVerdieping;
            }
        }

        // Anders: dichtstbijzijnde wachtende
        int best = lobbyVerdieping;
        int minDist = Integer.MAX_VALUE;

        for (int verdieping : wachtrijen.keySet()) {
            if (!wachtrijen.get(verdieping).isEmpty()) {
                int dist = Math.abs(huidigeVerdieping - verdieping);
                if (dist < minDist) {
                    minDist = dist;
                    best = verdieping;
                }
            }
        }

        return best;
    }

    private void uitladen() {

        Iterator<Persoon> it = passagiers.iterator();

        while (it.hasNext()) {

            Persoon p = it.next();

            if (p instanceof Gast g) {

                if (g.gewensteVerdieping == huidigeVerdieping) {

                    it.remove();

                    g.inLift = false;
                    g.gebruiktLift = false;
                    g.moetUitstappen = true;
                }
            }
        }
    }

    private void inladen() {

        Queue<Persoon> q = wachtrijen.get(huidigeVerdieping);
        if (q == null) return;

        while (!q.isEmpty()) {

            Persoon p = q.poll();
            passagiers.add(p);

            if (p instanceof Gast g) {

                g.inLift = true;
                g.wachtOpLift = false;

                // Verwijder van kaart tijdens rit
                /*
                if (g.huidigVakje != null) {
                    g.huidigVakje.verwijderPersoon(g);
                    g.huidigVakje = null;
                }

                 */
                // Gast blijft zichtbaar op liftpositie
                Vakje liftVakje = g.huidigVakje;

                if (liftVakje != null) {
                    liftVakje.verwijderPersoon(g);

                    Vakje nieuwLiftVakje = hotel.layout.krijgVakje(
                            this.posX,
                            huidigeVerdieping
                    );

                    g.huidigVakje = nieuwLiftVakje;

                    if (nieuwLiftVakje != null) {
                        nieuwLiftVakje.voegPersoonToe(g);
                    }
                }
            }
        }
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
