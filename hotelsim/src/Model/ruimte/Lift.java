package Model.ruimte;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;

import java.util.*;

public class Lift extends Ruimte {

    // Huidige verdieping van lift
    private int huidigeVerdieping = 1;

    // Lobby verdieping
    private int lobbyVerdieping = 1;

    // Personen in lift
    private List<Persoon> passagiers =
            new ArrayList<>();

    // Wachtrijen per verdieping
    private Map<Integer, Queue<Persoon>>
            wachtrijen = new HashMap<>();

    // Referentie naar hotel
    private Hotel hotel;

    public Lift(Hotel hotel) {
        this.hotel = hotel;
        this.huidigeVerdieping = 1;
        this.lobbyVerdieping = 1;
    }

    // Maak wachtrijen aan
    public void initWachtrijen( int maxVerdiepingen ) {

        for (int i = 1; i <= maxVerdiepingen; i++) {
            wachtrijen.put( i, new LinkedList<>() );
        }
    }

    // Roep lift op
    public void roep( Persoon p, int verdieping) {

        wachtrijen.putIfAbsent( verdieping, new LinkedList<>());

        Queue<Persoon> q = wachtrijen.get(verdieping);

        // Voorkom dubbele entries
        if (!q.contains(p)) {
            q.add(p);
        }
    }

    // Lift tick
    public void tik() {

        // Bepaal doelverdieping
        int doel = bepaalDoel();

        // Beweeg richting doel
        if (huidigeVerdieping < doel) {
            huidigeVerdieping++;
        } else if (
                huidigeVerdieping > doel
        ) {
            huidigeVerdieping--;
        }

        // Update posities passagiers
        updatePassagierPosities();

        // Laat passagiers uitstappen
        uitladen();

        // Laat wachtenden instappen
        inladen();
    }

    // Update posities van passagiers
    private void updatePassagierPosities() {

        Vakje liftVakje = hotel.layout.krijgVakje( this.posX, huidigeVerdieping );

        for (Persoon p : passagiers) {

            // Verwijder uit oud vakje
            if (p.huidigVakje != null) {
                p.huidigVakje.verwijderPersoon(p);
            }

            // Zet nieuwe positie
            p.huidigVakje = liftVakje;

            // Voeg toe aan nieuw vakje
            if (liftVakje != null) {
                liftVakje.voegPersoonToe(p);
            }
        }
    }

    // Bepaal doelverdieping
    private int bepaalDoel() {

        // Passagiers hebben prioriteit
        for (Persoon p : passagiers) {
            if (p instanceof Gast g) {
                return g.gewensteVerdieping;
            }
        }

        // Zoek dichtstbijzijnde wachtende
        int best = lobbyVerdieping;

        int minDist = Integer.MAX_VALUE;

        for (int verdieping : wachtrijen.keySet()) {

            if ( !wachtrijen.get(verdieping).isEmpty()) {
                int dist = Math.abs( huidigeVerdieping - verdieping);
                if (dist < minDist) {
                    minDist = dist;
                    best = verdieping;
                }
            }
        }
        return best;
    }

    // Laat passagiers uitstappen
    private void uitladen() {

        Iterator<Persoon> it = passagiers.iterator();

        while (it.hasNext()) {
            Persoon p = it.next();

            if (p instanceof Gast g) {

                // Bestemming bereikt
                if ( g.gewensteVerdieping == huidigeVerdieping) {

                    // Verwijder uit lift
                    it.remove();
                    // Reset flags
                    g.inLift = false;
                    g.gebruiktLift = false;
                    g.moetUitstappen = true;
                }
            }
        }
    }

    // Laat wachtenden instappen
    private void inladen() {

        Queue<Persoon> q = wachtrijen.get(huidigeVerdieping);

        // Geen wachtrij
        if (q == null) {
            return;
        }

        while (!q.isEmpty()) {
            Persoon p = q.poll();

            // Lift positie
            Vakje liftVakje = hotel.layout.krijgVakje( this.posX, huidigeVerdieping );

            // Verwijder uit oud vakje
            if (p.huidigVakje != null) {
                p.huidigVakje.verwijderPersoon(p);
            }

            // Zet in lift
            p.huidigVakje = liftVakje;

            // Voeg toe aan liftvakje
            if (liftVakje != null) {
                liftVakje.voegPersoonToe(p);
            }

            // Voeg toe aan passagiers
            passagiers.add(p);

            // Update gaststatus
            if (p instanceof Gast g) {
                g.inLift = true;
                g.wachtOpLift = false;
            }
        }
    }

    // Geef huidige verdieping
    public int getHuidigeVerdieping() {
        return huidigeVerdieping;
    }

    // Geef passagiers terug
    public List<Persoon> getPassagiers() {
        return passagiers;
    }

    // Geef aantal wachtenden
    public int aantalWachtend( int verdieping) {
        Queue<Persoon> q =
                wachtrijen.get(verdieping);
        return q == null ? 0 : q.size();
    }
}