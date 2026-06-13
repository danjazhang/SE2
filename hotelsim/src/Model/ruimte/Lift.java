package Model.ruimte;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;

import java.util.*;

// Verantwoordelijkheid: passagiers vervoeren tussen verdiepingen.
// De lift beweegt elke tick één verdieping richting het doel.
// Tijdens een brandalarm (uitBedrijf is true) rijdt de lift nog één rit af maar neemt geen nieuwe passagiers meer aan.
// Lift erft van Ruimte via 'extends Ruimte'.
public class Lift extends Ruimte {

    // De verdieping waar de lift momenteel staat.
    private int huidigeVerdieping = 1;

    // De lobbyverdieping: dit is de standaard terugkeerverdieping als er niemand wacht.
    private int lobbyVerdieping = 1;

    // De lijst van personen die momenteel in de lift zitten.
    private List<Persoon> passagiers = new ArrayList<>();

    // Een map van wachtrijen per verdieping.
    // 'Map<Integer, Queue<Persoon>>' betekent: sleutel is verdieping (int), waarde is een wachtrij van Persoon.
    private Map<Integer, Queue<Persoon>> wachtrijen = new HashMap<>();

    // Referentie naar het hotel om het grid en de layout te kunnen gebruiken.
    private Hotel hotel;

    // Sla op of de lift buiten gebruik is (true = buiten gebruik, false = in gebruik).
    // Wordt true gezet als het brandalarm afgaat.
    private boolean uitBedrijf = false;

    // Constructor: sla het hotel op en zet de beginverdieping op 1.
    public Lift(Hotel hotel) {
        this.hotel = hotel;
        this.huidigeVerdieping = 1;
        this.lobbyVerdieping = 1;
    }

    // Zet uitBedrijf op de meegegeven waarde: true zet de lift buiten gebruik, false zet hem terug.
    public void zetUitBedrijf(boolean uitBedrijf) {
        this.uitBedrijf = uitBedrijf;
    }

    // Maak een lege wachtrij aan voor elke verdieping van 1 tot en met maxVerdiepingen.
    public void initWachtrijen(int maxVerdiepingen) {
        for (int i = 1; i <= maxVerdiepingen; i++) {
            wachtrijen.put(i, new LinkedList<>());
        }
    }

    // Voeg persoon p toe aan de wachtrij van de opgegeven verdieping.
    // Als uitBedrijf gelijk is aan true, wordt de oproep genegeerd.
    // 'wachtrijen.putIfAbsent(verdieping, new LinkedList<>())' betekent:
    // als er nog geen wachtrij is voor deze verdieping, maak er dan één aan.
    public void roep(Persoon p, int verdieping) {
        if (uitBedrijf) return;
        wachtrijen.putIfAbsent(verdieping, new LinkedList<>());
        Queue<Persoon> q = wachtrijen.get(verdieping);
        // Voeg alleen toe als de persoon nog niet in de wachtrij staat.
        if (!q.contains(p)) q.add(p);
    }

    // Voer één lifttick uit: beweeg de lift één verdieping, update posities, laat passagiers uitstappen en instappen.
    public void tik() {
        // Als uitBedrijf gelijk is aan true: maak de huidige rit af maar laad geen nieuwe passagiers in.
        if (uitBedrijf) {
            if (!passagiers.isEmpty()) {
                // Bepaal het doel: eerste passagier zijn gewenste verdieping, of anders de lobbyverdieping.
                int doel;
                if (passagiers.get(0) instanceof Gast) {
                    Gast g = (Gast) passagiers.get(0);
                    doel = g.gewensteVerdieping;
                } else {
                    doel = lobbyVerdieping;
                }
                // Beweeg één verdieping richting het doel.
                if (huidigeVerdieping < doel) huidigeVerdieping++;
                else if (huidigeVerdieping > doel) huidigeVerdieping--;
                updatePassagierPosities();
                uitladen();
            }
            // Stop hier: geen nieuwe passagiers inladen tijdens brandalarm.
            return;
        }

        // Normale werking: bepaal doel, beweeg één verdieping, update posities, laat uit- en instappen.
        int doel = bepaalDoel();
        if (huidigeVerdieping < doel) huidigeVerdieping++;
        else if (huidigeVerdieping > doel) huidigeVerdieping--;

        updatePassagierPosities();
        uitladen();
        inladen();
    }

    // Zet alle passagiers op het vakje van de huidige verdieping.
    private void updatePassagierPosities() {
        Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);
        for (Persoon p : passagiers) {
            // Verwijder de persoon van zijn oude vakje als dat niet leeg is (null).
            if (p.huidigVakje != null) p.huidigVakje.verwijderPersoon(p);
            // Sla het nieuwe vakje op als huidigVakje van de persoon.
            p.huidigVakje = liftVakje;
            // Voeg de persoon toe aan het nieuwe vakje als dat niet leeg is (null).
            if (liftVakje != null) liftVakje.voegPersoonToe(p);
        }
    }

    // Bepaal naar welke verdieping de lift moet rijden.
    // Als er passagiers zijn, rij naar de gewenste verdieping van de eerste passagier.
    // Anders rij naar de dichtstbijzijnde wachtende persoon, of terug naar de lobbyverdieping.
    private int bepaalDoel() {
        for (Persoon p : passagiers) {
            if (p instanceof Gast g) return g.gewensteVerdieping;
        }
        int best = lobbyVerdieping;
        int minDist = Integer.MAX_VALUE;
        // 'Integer.MAX_VALUE' is de grootste mogelijke int-waarde, zodat elke afstand kleiner is.
        for (int verdieping : wachtrijen.keySet()) {
            if (!wachtrijen.get(verdieping).isEmpty()) {
                int dist = Math.abs(huidigeVerdieping - verdieping);
                // 'Math.abs(...)' geeft de absolute waarde (altijd positief).
                if (dist < minDist) { minDist = dist; best = verdieping; }
            }
        }
        return best;
    }

    // Laat passagiers uitstappen als ze op hun gewenste verdieping zijn.
    // 'Iterator' wordt gebruikt om veilig elementen te verwijderen tijdens het doorlopen van de lijst.
    private void uitladen() {
        Iterator<Persoon> it = passagiers.iterator();
        while (it.hasNext()) {
            Persoon p = it.next();
            // Als de passagier een Gast is én zijn gewensteVerdieping is gelijk aan (==) de huidigeVerdieping:
            if (p instanceof Gast g && g.gewensteVerdieping == huidigeVerdieping) {
                // Verwijder de gast uit de passagierslijst.
                it.remove();
                // Zet inLift op false, gebruiktLift op false, en moetUitstappen op true.
                g.inLift = false;
                g.gebruiktLift = false;
                g.moetUitstappen = true;
            }
        }
    }

    // Laat wachtende personen op de huidige verdieping instappen.
    private void inladen() {
        Queue<Persoon> q = wachtrijen.get(huidigeVerdieping);
        // Als er geen wachtrij is voor deze verdieping, stop dan.
        if (q == null) return;
        // Verwerk iedereen in de wachtrij: laat ze één voor één instappen.
        while (!q.isEmpty()) {
            // 'q.poll()' haalt de eerste persoon uit de wachtrij en verwijdert hem.
            Persoon p = q.poll();
            Vakje liftVakje = hotel.layout.krijgVakje(this.posX, huidigeVerdieping);
            // Verwijder de persoon van zijn huidige vakje.
            if (p.huidigVakje != null) p.huidigVakje.verwijderPersoon(p);
            // Zet de persoon op het liftvakje.
            p.huidigVakje = liftVakje;
            if (liftVakje != null) liftVakje.voegPersoonToe(p);
            // Voeg de persoon toe aan de passagierslijst.
            passagiers.add(p);
            // Als de persoon een Gast is, zet inLift op true en wachtOpLift op false.
            if (p instanceof Gast g) { g.inLift = true; g.wachtOpLift = false; }
        }
    }

    // Geef de huidige verdieping terug.
    public int getHuidigeVerdieping() { return huidigeVerdieping; }

    // Geef de passagierslijst terug.
    public List<Persoon> getPassagiers() { return passagiers; }

    // Geef het aantal wachtende personen op de opgegeven verdieping terug.
    // Als er geen wachtrij is voor de verdieping, geef dan 0 terug.
    public int aantalWachtend(int verdieping) {
        Queue<Persoon> q = wachtrijen.get(verdieping);
        if (q == null) {
            return 0;
        } else {
            return q.size();
        }
    }
}
