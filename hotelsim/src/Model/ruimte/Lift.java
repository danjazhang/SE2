package Model.ruimte;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;

import java.util.*;

public class Lift extends Ruimte {

    // huidige verdieping van lift
    private int huidigeVerdieping = 1;

    // lobby verdieping
    private int lobbyVerdieping = 1;

    // personen in lift
    private List<Persoon> passagiers = new ArrayList<>();

    // wachtrijen per verdieping
    private Map<Integer, Queue<Persoon>> wachtrijen = new HashMap<>();

    // referentie naar hotel
    private Hotel hotel;

    // of de lift buiten gebruik is tijdens brandalarm
    private boolean uitBedrijf = false;

    public Lift(Hotel hotel) {
        this.hotel = hotel;
        this.huidigeVerdieping = 1;
        this.lobbyVerdieping = 1;
    }

    // zet de lift buiten gebruik of terug in gebruik
    public void zetUitBedrijf(boolean uitBedrijf) {
        this.uitBedrijf = uitBedrijf;
    }

    // maak wachtrijen aan
    public void initWachtrijen(int maxVerdiepingen) {
        for (int i = 1; i <= maxVerdiepingen; i++) {
            wachtrijen.put(i, new LinkedList<>());
        }
    }

    // roep lift op — wordt genegeerd als lift buiten gebruik is
    public void roep(Persoon p, int verdieping) {
        // geen nieuwe oproepen tijdens brandalarm
        if (uitBedrijf) return;

        wachtrijen.putIfAbsent(verdieping, new LinkedList<>());
        Queue<Persoon> q = wachtrijen.get(verdieping);
        if (!q.contains(p)) q.add(p);
    }

    // lift tick
    public void tik() {
        // buiten gebruik: huidige rit afmaken, passagiers uitladen, dan stoppen
        if (uitBedrijf) {
            if (!passagiers.isEmpty()) {
                // maak huidige beweging af naar eerstvolgende verdieping
                int doel = passagiers.get(0) instanceof Gast g ? g.gewensteVerdieping : lobbyVerdieping;
                if (huidigeVerdieping < doel) huidigeVerdieping++;
                else if (huidigeVerdieping > doel) huidigeVerdieping--;
                updatePassagierPosities();
                uitladen();
            }
            // geen nieuwe passagiers inladen tijdens brandalarm
            return;
        }

        // normale werking
        int doel = bepaalDoel();
        if (huidigeVerdieping < doel) huidigeVerdieping++;
        else if (huidigeVerdieping > doel) huidigeVerdieping--;

        updatePassagierPosities();
        uitladen();
        inladen();
    }

    // update posities van passagiers
    private void updatePassagierPosities() {
        Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);
        for (Persoon p : passagiers) {
            if (p.huidigVakje != null) p.huidigVakje.verwijderPersoon(p);
            p.huidigVakje = liftVakje;
            if (liftVakje != null) liftVakje.voegPersoonToe(p);
        }
    }

    // bepaal doelverdieping
    private int bepaalDoel() {
        for (Persoon p : passagiers) {
            if (p instanceof Gast g) return g.gewensteVerdieping;
        }
        int best = lobbyVerdieping;
        int minDist = Integer.MAX_VALUE;
        for (int verdieping : wachtrijen.keySet()) {
            if (!wachtrijen.get(verdieping).isEmpty()) {
                int dist = Math.abs(huidigeVerdieping - verdieping);
                if (dist < minDist) { minDist = dist; best = verdieping; }
            }
        }
        return best;
    }

    // laat passagiers uitstappen
    private void uitladen() {
        Iterator<Persoon> it = passagiers.iterator();
        while (it.hasNext()) {
            Persoon p = it.next();
            if (p instanceof Gast g && g.gewensteVerdieping == huidigeVerdieping) {
                it.remove();
                g.inLift = false;
                g.gebruiktLift = false;
                g.moetUitstappen = true;
            }
        }
    }

    // laat wachtenden instappen — alleen als lift in gebruik is
    private void inladen() {
        Queue<Persoon> q = wachtrijen.get(huidigeVerdieping);
        if (q == null) return;
        while (!q.isEmpty()) {
            Persoon p = q.poll();
            Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);
            if (p.huidigVakje != null) p.huidigVakje.verwijderPersoon(p);
            p.huidigVakje = liftVakje;
            if (liftVakje != null) liftVakje.voegPersoonToe(p);
            passagiers.add(p);
            if (p instanceof Gast g) { g.inLift = true; g.wachtOpLift = false; }
        }
    }

    public int getHuidigeVerdieping() { return huidigeVerdieping; }
    public List<Persoon> getPassagiers() { return passagiers; }
    public int aantalWachtend(int verdieping) {
        Queue<Persoon> q = wachtrijen.get(verdieping);
        return q == null ? 0 : q.size();
    }
}
