package Model.ruimte;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;

import java.util.*;

public class Lift extends Ruimte {

    // huidige y-positie van de lift-cabine in het grid
    private int huidigeVerdieping = 1;

    // y-positie van de lobby — lift start hier en keert hier terug als niemand wacht
    private int lobbyVerdieping = 1;

    // alleen gasten gebruiken de lift, dus List<Gast> in plaats van List<Persoon>
    private List<Gast> passagiers = new ArrayList<>();

    // wachtrijen per y-positie — alleen gasten wachten op de lift
    private Map<Integer, Queue<Gast>> wachtrijen = new HashMap<>();

    private Hotel hotel;
    private boolean uitBedrijf = false;

    // statusmachine: RIJDEN → UITSTAPPEN (1 tick wachten) → INSTAPPEN (1 tick wachten) → RIJDEN
    private enum LiftStatus { RIJDEN, UITSTAPPEN, INSTAPPEN }
    private LiftStatus status = LiftStatus.RIJDEN;

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

    // voeg gast toe aan de wachtrij voor de opgegeven y-rij
    public void roep(Gast g, int verdieping) {
        if (uitBedrijf) return;
        wachtrijen.putIfAbsent(verdieping, new LinkedList<>());
        Queue<Gast> q = wachtrijen.get(verdieping);
        if (!q.contains(g)) q.add(g);
    }

    // één simulatie-tick
    public void tik() {
        if (uitBedrijf) {
            // tijdens alarm: alleen huidige passagiers afleveren
            if (!passagiers.isEmpty()) {
                int doel = passagiers.get(0).gewensteVerdieping;
                if (huidigeVerdieping < doel) huidigeVerdieping++;
                else if (huidigeVerdieping > doel) huidigeVerdieping--;
                updatePassagierPosities();
                uitstappen();
            }
            return;
        }

        // statusmachine: elke status duurt precies 1 tick
        if (status == LiftStatus.UITSTAPPEN) {
            // tick 1 na aankomst: passagiers stappen uit
            uitstappen();
            // alleen naar INSTAPPEN als iemand wacht, anders direct RIJDEN
            Queue<Gast> wacht = wachtrijen.get(huidigeVerdieping);
            if (wacht != null && !wacht.isEmpty()) {
                status = LiftStatus.INSTAPPEN;
            } else {
                status = LiftStatus.RIJDEN;
            }
            return;
        }

        if (status == LiftStatus.INSTAPPEN) {
            // tick 2 na aankomst: wachtenden stappen in
            instappen();
            status = LiftStatus.RIJDEN;
            return;
        }

        // RIJDEN: beweeg naar doel
        int doel = bepaalDoel();
        if (huidigeVerdieping != doel) {
            // nog niet op doel: beweeg 1 stap
            if (huidigeVerdieping < doel) huidigeVerdieping++;
            else huidigeVerdieping--;
            updatePassagierPosities();
        } else {
            // op doel aangekomen: check wat er gedaan moet worden
            updatePassagierPosities();
            boolean iemandWilUitstappen = false;
            for (Gast g : passagiers) {
                if (g.gewensteVerdieping == huidigeVerdieping) {
                    iemandWilUitstappen = true;
                    break;
                }
            }
            Queue<Gast> wacht = wachtrijen.get(huidigeVerdieping);
            boolean iemandWachtHier = wacht != null && !wacht.isEmpty();

            if (iemandWilUitstappen) {
                // iemand stapt uit: 1 tick uitstappen, dan 1 tick instappen
                status = LiftStatus.UITSTAPPEN;
            } else if (iemandWachtHier) {
                // niemand stapt uit maar iemand wacht: direct naar instappen (1 tick)
                status = LiftStatus.INSTAPPEN;
            }
        }
    }

    // update posities van passagiers naar het huidige liftvakje
    private void updatePassagierPosities() {
        Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);
        for (Gast g : passagiers) {
            if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
            g.huidigVakje = liftVakje;
            if (liftVakje != null) liftVakje.voegPersoonToe(g);
        }
    }

    // bepaal doelverdieping:
    // 1. passagiers aan boord → ga naar hun gewenste y
    // 2. iemand wacht → ga naar dichtstbijzijnde wachtrij
    // 3. niemand → terug naar lobby
    private int bepaalDoel() {
        if (!passagiers.isEmpty()) {
            return passagiers.get(0).gewensteVerdieping;
        }
        int best = -1;
        int minDist = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Queue<Gast>> entry : wachtrijen.entrySet()) {
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
    private void uitstappen() {
        Iterator<Gast> it = passagiers.iterator();
        while (it.hasNext()) {
            Gast g = it.next();
            if (g.gewensteVerdieping == huidigeVerdieping) {
                it.remove();
                g.inLift = false;
                g.gebruiktLift = false;
                g.moetUitstappen = true;
            }
        }
    }

    // laat wachtende gasten instappen als ze fysiek naast de lift staan
    private void instappen() {
        Queue<Gast> q = wachtrijen.get(huidigeVerdieping);
        if (q == null || q.isEmpty()) return;
        Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);
        Iterator<Gast> it = q.iterator();
        while (it.hasNext()) {
            Gast g = it.next();
            if (g.huidigVakje == null) continue;
            // gast moet op de wachtplek staan: x = posX+1, zelfde y
            if (g.huidigVakje.x != this.posX + 1) continue;
            if (g.huidigVakje.y != huidigeVerdieping) continue;
            it.remove();
            g.huidigVakje.verwijderPersoon(g);
            g.huidigVakje = liftVakje;
            if (liftVakje != null) liftVakje.voegPersoonToe(g);
            passagiers.add(g);
            g.inLift = true;
            g.wachtOpLift = false;
        }
    }

    public int getHuidigeVerdieping() { return huidigeVerdieping; }

    // geeft een kopie terug zodat de originele lijst niet gewijzigd kan worden van buiten
    public List<Gast> getPassagiers() { return new ArrayList<>(passagiers); }

    public int aantalWachtend(int verdieping) {
        Queue<Gast> q = wachtrijen.get(verdieping);
        if (q == null) {
            return 0;
        } else {
            return q.size();
        }
    }
}
