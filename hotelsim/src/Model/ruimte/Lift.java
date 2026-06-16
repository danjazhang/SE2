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

    // reset alle wachtrijen — aanroepen na brandalarm zodat oude oproepen verdwijnen
    public void resetWachtrijen() {
        for (Queue<Gast> q : wachtrijen.values()) {
            q.clear();
        }
        status = LiftStatus.RIJDEN;
    }

    // verwijder een gast uit alle wachtrijen en passagierslijst (bij summoning/verwijdering)
    public void verwijderUitWachtrij(Gast g) {
        for (Queue<Gast> q : wachtrijen.values()) {
            q.remove(g);
        }
        passagiers.remove(g);
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
            instappen();
            status = LiftStatus.RIJDEN;
            return;
        }

        // RIJDEN: beweeg naar doel
        int doel = bepaalDoel();
        if (huidigeVerdieping != doel) {
            if (huidigeVerdieping < doel) huidigeVerdieping++;
            else huidigeVerdieping--;
            updatePassagierPosities();
        } else {
            // op doel aangekomen
            updatePassagierPosities();
            boolean iemandWilUitstappen = false;
            for (Gast g : passagiers) {
                if (g.gewensteVerdieping == huidigeVerdieping) {
                    iemandWilUitstappen = true;
                    break;
                }
            }
            Queue<Gast> wacht = wachtrijen.get(huidigeVerdieping);
            boolean iemandWachtHier = wacht != null && heeftGeldigeWachter(wacht);
            if (iemandWilUitstappen) {
                status = LiftStatus.UITSTAPPEN;
            } else if (iemandWachtHier) {
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

    /**
     * Bepaal doelverdieping:
     * 1. passagiers aan boord → ga naar hun gewenste y
     * 2. lobby heeft prioriteit als er iemand wacht (gasten komen altijd van de lobby)
     * 3. dichtstbijzijnde andere wachtrij
     * 4. niemand → terug naar lobby
     */
    private int bepaalDoel() {
        // passagiers aan boord
        if (!passagiers.isEmpty()) {
            return passagiers.get(0).gewensteVerdieping;
        }

        int lobbyMetWachter = -1;
        int best = -1;
        int minDist = Integer.MAX_VALUE;

        for (Map.Entry<Integer, Queue<Gast>> entry : wachtrijen.entrySet()) {
            if (!heeftGeldigeWachter(entry.getValue())) continue;
            int y = entry.getKey();
            if (y == lobbyVerdieping) {
                lobbyMetWachter = y;
            }
            int dist = Math.abs(huidigeVerdieping - y);
            if (dist < minDist) {
                minDist = dist;
                best = y;
            }
        }

        // lobby heeft altijd prioriteit als er niemand aan boord is
        if (lobbyMetWachter != -1) return lobbyMetWachter;
        if (best != -1) return best;
        return lobbyVerdieping;
    }

    // controleer of een wachtrij minstens één gast heeft met een geldig huidigVakje
    private boolean heeftGeldigeWachter(Queue<Gast> q) {
        if (q == null || q.isEmpty()) return false;
        for (Gast g : q) {
            if (g.huidigVakje != null) return true;
        }
        return false;
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
    // verwijder ook gasten met null huidigVakje (gesummond/verwijderd)
    private void instappen() {
        Queue<Gast> q = wachtrijen.get(huidigeVerdieping);
        if (q == null || q.isEmpty()) return;
        Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);
        Iterator<Gast> it = q.iterator();
        while (it.hasNext()) {
            Gast g = it.next();
            // verwijder gasten die niet meer bestaan
            if (g.huidigVakje == null) { it.remove(); continue; }
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
        return q == null ? 0 : q.size();
    }
}
