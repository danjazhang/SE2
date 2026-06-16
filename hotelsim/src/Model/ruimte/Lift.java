package Model.ruimte;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;

import java.util.*;

public class Lift extends Ruimte {

    // huidige y-positie van de lift-cabine in het grid
    private int huidigeVerdieping = 1;

    // y-positie van de lobby — lift start hier en keert hier terug als niemand wacht
    private int lobbyVerdieping = 1;

    // passagiers die momenteel in de lift zitten
    private List<Persoon> passagiers = new ArrayList<>();

    // wachtrijen per y-positie
    private Map<Integer, Queue<Persoon>> wachtrijen = new HashMap<>();

    private Hotel hotel;
    private boolean uitBedrijf = false;

    public Lift(Hotel hotel) {
        this.hotel = hotel;
        this.huidigeVerdieping = 1;
        this.lobbyVerdieping = 1;
    }

    // stel de y-rij van de lobby in — lift start en keert terug naar deze positie
    public void setLobbyVerdieping(int y) {
        this.lobbyVerdieping = y;
        this.huidigeVerdieping = y;
    }

    public void zetUitBedrijf(boolean uitBedrijf) {
        this.uitBedrijf = uitBedrijf;
    }

    // initialiseer wachtrijen voor alle y-rijen
    public void initWachtrijen(int maxY) {
        for (int i = 1; i <= maxY; i++) {
            wachtrijen.put(i, new LinkedList<>());
        }
    }

    // voeg persoon toe aan de wachtrij voor de opgegeven y-rij
    public void roep(Persoon p, int verdieping) {
        if (uitBedrijf) return;
        wachtrijen.putIfAbsent(verdieping, new LinkedList<>());
        Queue<Persoon> q = wachtrijen.get(verdieping);
        if (!q.contains(p)) q.add(p);
    }

    // één simulatie-tick
    public void tik() {
        if (uitBedrijf) {
            // tijdens alarm: alleen huidige passagiers afleveren
            if (!passagiers.isEmpty()) {
                int doel = lobbyVerdieping;
                if (!passagiers.isEmpty() && passagiers.get(0) instanceof Gast) {
                    doel = ((Gast) passagiers.get(0)).gewensteVerdieping;
                }
                if (huidigeVerdieping < doel) huidigeVerdieping++;
                else if (huidigeVerdieping > doel) huidigeVerdieping--;
                updatePassagierPosities();
                uitladen();
            }
            return;
        }

        // normale werking: beweeg, update, uitladen, inladen
        int doel = bepaalDoel();
        if (huidigeVerdieping < doel) huidigeVerdieping++;
        else if (huidigeVerdieping > doel) huidigeVerdieping--;

        updatePassagierPosities();
        uitladen();
        inladen();
    }

    // update posities van passagiers naar het huidige liftvakje
    private void updatePassagierPosities() {
        Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);
        for (Persoon p : passagiers) {
            if (p.huidigVakje != null) p.huidigVakje.verwijderPersoon(p);
            p.huidigVakje = liftVakje;
            if (liftVakje != null) liftVakje.voegPersoonToe(p);
        }
    }

    // bepaal doelverdieping:
    // 1. passagiers aan boord → ga naar hun gewenste y
    // 2. iemand wacht → ga naar dichtstbijzijnde wachtrij
    // 3. niemand → terug naar lobby
    private int bepaalDoel() {
        for (Persoon p : passagiers) {
            if (p instanceof Gast) {
                return ((Gast) p).gewensteVerdieping;
            }
        }
        int best = -1;
        int minDist = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Queue<Persoon>> entry : wachtrijen.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            int dist = Math.abs(huidigeVerdieping - entry.getKey());
            if (dist < minDist) {
                minDist = dist;
                best = entry.getKey();
            }
        }
        if (best != -1) return best;
        return lobbyVerdieping;
    }

    // laat passagiers uitstappen op hun gewenste y-positie
    private void uitladen() {
        Iterator<Persoon> it = passagiers.iterator();
        while (it.hasNext()) {
            Persoon p = it.next();
            if (p instanceof Gast) {
                Gast g = (Gast) p;
                if (g.gewensteVerdieping == huidigeVerdieping) {
                    it.remove();
                    g.inLift = false;
                    g.gebruiktLift = false;
                    g.moetUitstappen = true;
                }
            }
        }
    }

    // laat wachtenden instappen — alleen als ze fysiek naast de lift staan
    private void inladen() {
        Queue<Persoon> q = wachtrijen.get(huidigeVerdieping);
        if (q == null || q.isEmpty()) return;
        Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);
        Iterator<Persoon> it = q.iterator();
        while (it.hasNext()) {
            Persoon p = it.next();
            if (p.huidigVakje == null) continue;
            // persoon moet op de wachtplek staan: x = posX+1, zelfde y
            if (p.huidigVakje.x != this.posX + 1) continue;
            if (p.huidigVakje.y != huidigeVerdieping) continue;
            it.remove();
            p.huidigVakje.verwijderPersoon(p);
            p.huidigVakje = liftVakje;
            if (liftVakje != null) liftVakje.voegPersoonToe(p);
            passagiers.add(p);
            if (p instanceof Gast) {
                Gast g = (Gast) p;
                g.inLift = true;
                g.wachtOpLift = false;
            }
        }
    }

    public int getHuidigeVerdieping() { return huidigeVerdieping; }
    public List<Persoon> getPassagiers() { return passagiers; }

    public int aantalWachtend(int verdieping) {
        Queue<Persoon> q = wachtrijen.get(verdieping);
        return q == null ? 0 : q.size();
    }
}
