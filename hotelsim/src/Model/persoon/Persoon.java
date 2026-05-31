package Model.persoon;
import Model.Pathfinder;
import Model.layout.Vakje;
import Model.ruimte.Gang;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Trap;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Persoon {

    // Huidige positie
    public Vakje huidigVakje;

    // Huidige bestemming (huidig actief doel)
    public Vakje doelVakje;

    // Pathfinding systeem
    private Pathfinder pathfinder;

    // Wachtrij van tussendoelen
    private Queue<Vakje> tussendoelen = new LinkedList<>();

    public Persoon() {
        this.huidigVakje = null;
        this.doelVakje = null;
    }

    public void setPathfinder(Pathfinder pathfinder) { this.pathfinder = pathfinder; }
    public Pathfinder getPathfinder() { return this.pathfinder; }
    public void zetDoel(Vakje v) { this.doelVakje = v; }
    public void voegTussendoelToe(Vakje v) { tussendoelen.add(v); }

    public void zetStartPositie(Vakje v) {
        huidigVakje = v;
        v.voegPersoonToe(this);
    }

    public void beweeg() {

        // Gasten in lift bewegen niet zelfstandig
        if (this instanceof Gast g && g.inLift) return;

        // Geen doel of positie
        if (doelVakje == null && tussendoelen.isEmpty()) return;
        if (huidigVakje == null) return;

        // Huidig doel bereikt: pak volgend tussendoel
        if (huidigVakje.equals(doelVakje)) {
            doelVakje = tussendoelen.poll();
        }
        if (doelVakje == null) return;

        // Gast wacht op lift
        if (this instanceof Gast g && g.wachtOpLift) return;

        // Geen pathfinder
        if (pathfinder == null) return;

        // Volgende stap berekenen
        Vakje nieuw = pathfinder.volgendeStap(huidigVakje, doelVakje);
        if (nieuw == null) return;

        // Mag de persoon dit vakje betreden?
        if (!isBetreedbaar(nieuw)) return;

        // Is dit het laatste doel (kamer-check vóór verplaatsing)?
        // We onthouden of het nieuwe vakje het eindpunt is (geen tussendoelen meer na dit)
        boolean isEindpunt = nieuw.equals(doelVakje) && tussendoelen.isEmpty();

        // --- Verplaats persoon ---

        // Verwijder uit oud vakje
        huidigVakje.verwijderPersoon(this);

        // Verlaat huidige ruimte (alleen voor "echte" ruimtes)
        if (huidigVakje.ruimte instanceof Kamer
                || huidigVakje.ruimte instanceof Model.ruimte.Restaurant
                || huidigVakje.ruimte instanceof Model.ruimte.Bioscoop
                || huidigVakje.ruimte instanceof Model.ruimte.Fitnessruimte
                || huidigVakje.ruimte instanceof Lobby) {
            huidigVakje.ruimte.verlaat(this);
        }

        // Zet op nieuw vakje
        huidigVakje = nieuw;
        nieuw.voegPersoonToe(this);

        // Betreed nieuwe ruimte
        if (nieuw.ruimte instanceof Kamer kamer) {
            // Gast betreedt kamer alleen als dit het eindpunt is
            if (isEindpunt) {
                if (this instanceof Gast gast && gast.kamer == kamer) {
                    gast.gaNaarkamer(); // registreert gast intern in kamer
                } else if (!(this instanceof Gast)) {
                    // schoonmaker
                    kamer.betreed(this);
                }
            }
        } else if (nieuw.ruimte instanceof Lobby
                || nieuw.ruimte instanceof Model.ruimte.Restaurant
                || nieuw.ruimte instanceof Model.ruimte.Bioscoop
                || nieuw.ruimte instanceof Model.ruimte.Fitnessruimte) {
            nieuw.ruimte.betreed(this);
        }
        // Gang, Lift, Trap: geen betreed/verlaat aanroep
    }

    // Mag de persoon dit vakje betreden?
    // Toegestaan: gang, lift, trap, lobby, leeg vakje, of het exacte doelvakje.
    private boolean isBetreedbaar(Vakje vakje) {
        if (vakje == null) return false;
        if (vakje.equals(doelVakje)) return true;
        if (vakje.ruimte == null) return true;
        if (vakje.ruimte instanceof Gang) return true;
        if (vakje.ruimte instanceof Lift) return true;
        if (vakje.ruimte instanceof Trap) return true;
        if (vakje.ruimte instanceof Lobby) return true;
        return false;
    }

    public void wisRoute() {
        doelVakje = null;
        tussendoelen.clear();
    }

    public void voerTaakUit() {}
}
