package Model.ruimte;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import java.util.*;

// De Lift-klasse simuleert een lift in het hotel
// Hij verplaatst zich tussen verdiepingen en vervoert gasten
public class Lift extends Ruimte {

    // huidige verdieping (y-positie) waar de lift zich bevindt
    private int huidigeVerdieping = 1;

    // lobby-verdieping (startpunt en standaard terugkeerpunt van de lift)
    private int lobbyVerdieping = 1;

    // lijst met gasten die momenteel in de lift zitten
    private List<Gast> passagiers = new ArrayList<>();

    // wachtrijen per verdieping (key = y-positie, value = queue van wachtende gasten)
    private Map<Integer, Queue<Gast>> wachtrijen = new HashMap<>();

    // referentie naar het hotel (nodig om vakjes en layout te benaderen)
    private Hotel hotel;

    // bepaalt of de lift buiten werking is (bijv. brandalarm)
    private boolean uitBedrijf = false;

    // statusmachine van de lift:
    // RIJDEN = bewegen tussen verdiepingen
    // UITSTAPPEN = passagiers laten uitstappen
    // INSTAPPEN = nieuwe passagiers laten instappen
    private enum LiftStatus { RIJDEN, UITSTAPPEN, INSTAPPEN }

    // huidige status van de lift
    private LiftStatus status = LiftStatus.RIJDEN;

    // constructor: koppelt lift aan hotel
    public Lift(Hotel hotel) {
        this.hotel = hotel;

        // standaard start op verdieping 1
        this.huidigeVerdieping = 1;

        // lobby is ook standaard verdieping 1
        this.lobbyVerdieping = 1;
    }

    // zet de lobbyverdieping en zet de lift daar ook meteen neer
    public void setLobbyVerdieping(int y) {
        this.lobbyVerdieping = y;
        this.huidigeVerdieping = y;
    }

    // zet lift aan of uit (bijv. brandalarm)
    public void zetUitBedrijf(boolean uitBedrijf) {
        this.uitBedrijf = uitBedrijf;
    }

    // maakt wachtrijen aan voor alle verdiepingen in het hotel
    public void initWachtrijen(int maxY) {
        for (int i = 1; i <= maxY; i++) {
            wachtrijen.put(i, new LinkedList<>());
        }
    }

    // roept de lift naar een bepaalde verdieping voor een gast
    public void roep(Gast g, int verdieping) {

        // als lift uit staat: negeren
        if (uitBedrijf) return;
        // zorg dat er een wachtrij bestaat voor deze verdieping
        wachtrijen.putIfAbsent(verdieping, new LinkedList<>());
        Queue<Gast> q = wachtrijen.get(verdieping);
        // voorkom dubbele entries in de wachtrij
        if (!q.contains(g)) q.add(g);
    }

    // reset alle wachtrijen (bijv. na brandalarm)
    public void resetWachtrijen() {

        // maak alle queues leeg
        for (Queue<Gast> q : wachtrijen.values()) {
            q.clear();
        }
        // reset status van de lift
        status = LiftStatus.RIJDEN;
    }

    // verwijdert een gast uit alle wachtrijen en uit de lift zelf
    public void verwijderUitWachtrij(Gast g) {

        // loop door alle wachtrijen en verwijder gast
        for (Queue<Gast> q : wachtrijen.values()) {
            q.remove(g);
        }
        // verwijder ook uit passagiers (als hij in de lift zit)
        passagiers.remove(g);
    }

    // ------------------------------------------------------------
    // SIMULATIE TICK
    // ------------------------------------------------------------
    public void tik() {

        // als lift uit bedrijf is (bijv. brandalarm)
        if (uitBedrijf) {

            // alleen bestaande passagiers nog afleveren
            if (!passagiers.isEmpty()) {
                // eerste passagier bepaalt bestemming
                int doel = passagiers.get(0).gewensteVerdieping;
                // beweeg 1 stap richting doelverdieping
                if (huidigeVerdieping < doel) huidigeVerdieping++;
                else if (huidigeVerdieping > doel) huidigeVerdieping--;
                // update positie van passagiers in de wereld
                updatePassagierPosities();
                // laat passagiers uitstappen als ze op juiste verdieping zijn
                uitstappen();
            }

            return;
        }

        // ------------------------------------------------------------
        // STATUSMACHINE
        // ------------------------------------------------------------

        // als lift net moet laten uitstappen
        if (status == LiftStatus.UITSTAPPEN) {
            // laat mensen uitstappen
            uitstappen();
            // daarna meteen instappen als mogelijk
            instappen();
            // terug naar rijden
            status = LiftStatus.RIJDEN;
            return;
        }

        // ------------------------------------------------------------
        // BEWEGEN
        // ------------------------------------------------------------

        // bepaal waar de lift heen moet
        int doel = bepaalDoel();

        // als nog niet op bestemming
        if (huidigeVerdieping != doel) {

            // beweeg 1 verdieping omhoog of omlaag
            if (huidigeVerdieping < doel) huidigeVerdieping++;
            else huidigeVerdieping--;
            updatePassagierPosities();

        } else {

            // aangekomen: laat mensen in- en uitstappen
            updatePassagierPosities();
            uitstappen();
            instappen();
        }
    }

    // ------------------------------------------------------------
    // PASSAGIERS POSITIES UPDATEN
    // ------------------------------------------------------------

    // zet alle passagiers op het juiste vakje van de lift
    private void updatePassagierPosities() {

        // vind vakje van lift op huidige verdieping
        Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);

        // update positie van elke gast in de lift
        for (Gast g : passagiers) {
            // verwijder oude positie
            if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
            // zet nieuwe positie
            g.huidigVakje = liftVakje;
            // plaats in nieuw vakje
            if (liftVakje != null) liftVakje.voegPersoonToe(g);
        }
    }

    // ------------------------------------------------------------
    // DOELBEPALING
    // ------------------------------------------------------------

    /**
     * bepaalt waar de lift heen moet:
     * 1. als er passagiers zijn → naar hun doelverdieping
     * 2. anders → naar wachtrijen
     * 3. anders → terug naar lobby
     */
    private int bepaalDoel() {

        // als er passagiers zijn → volg eerste passagier
        if (!passagiers.isEmpty()) {
            return passagiers.get(0).gewensteVerdieping;
        }

        int lobbyMetWachter = -1;
        int best = -1;
        int minDist = Integer.MAX_VALUE;

        // zoek dichtstbijzijnde wachtrij met gasten
        for (Map.Entry<Integer, Queue<Gast>> entry : wachtrijen.entrySet()) {

            if (!heeftGeldigeWachter(entry.getValue())) continue;

            int y = entry.getKey();

            // lobby heeft prioriteit
            if (y == lobbyVerdieping) {
                lobbyMetWachter = y;
            }

            // bereken afstand tot huidige positie
            int dist = Math.abs(huidigeVerdieping - y);

            if (dist < minDist) {
                minDist = dist;
                best = y;
            }
        }

        // eerst lobby, anders dichtstbijzijnde wachtrij
        if (lobbyMetWachter != -1) return lobbyMetWachter;
        if (best != -1) return best;

        // default: ga naar lobby
        return lobbyVerdieping;
    }

    // controleert of wachtrij echt bruikbare gasten bevat
    private boolean heeftGeldigeWachter(Queue<Gast> q) {

        if (q == null || q.isEmpty()) return false;
        for (Gast g : q) {
            if (g.huidigVakje != null) return true;
        }

        return false;
    }

    // ------------------------------------------------------------
    // UITSTAPPEN
    // ------------------------------------------------------------

    // laat gasten uitstappen op juiste verdieping
    private void uitstappen() {

        Iterator<Gast> it = passagiers.iterator();

        while (it.hasNext()) {

            Gast g = it.next();

            // als gast op juiste verdieping is
            if (g.gewensteVerdieping == huidigeVerdieping) {

                it.remove();
                // reset lift-status van gast
                g.inLift = false;
                g.gebruiktLift = false;

                // markeer dat hij moet uitstappen
                g.moetUitstappen = true;
            }
        }
    }

    // ------------------------------------------------------------
    // INSTAPPEN
    // ------------------------------------------------------------

    // laat gasten instappen als ze naast de lift staan
    private void instappen() {

        Queue<Gast> q = wachtrijen.get(huidigeVerdieping);

        if (q == null || q.isEmpty()) return;

        // vakje van lift op huidige verdieping
        Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);

        Iterator<Gast> it = q.iterator();

        while (it.hasNext()) {

            Gast g = it.next();

            // verwijder als gast niet meer bestaat
            if (g.huidigVakje == null) {
                it.remove();
                continue;
            }

            // gast moet fysiek naast lift staan
            if (g.huidigVakje.x != this.posX + 1) continue;
            if (g.huidigVakje.y != huidigeVerdieping) continue;

            // haal uit wachtrij
            it.remove();

            // verplaats gast in lift
            g.huidigVakje.verwijderPersoon(g);
            g.huidigVakje = liftVakje;

            if (liftVakje != null) liftVakje.voegPersoonToe(g);

            // voeg toe aan passagiers
            passagiers.add(g);

            // update status
            g.inLift = true;
            g.wachtOpLift = false;
        }
    }

    // ------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------

    public int getHuidigeVerdieping() {
        return huidigeVerdieping;
    }

    // kopie van passagierslijst (veilig voor buitengebruik)
    public List<Gast> getPassagiers() {
        return new ArrayList<>(passagiers);
    }

    // aantal wachtende gasten op een verdieping
    public int aantalWachtend(int verdieping) {

        Queue<Gast> q = wachtrijen.get(verdieping);

        if (q == null) {
            return 0;
        } else {
            return q.size();
        }
    }
}