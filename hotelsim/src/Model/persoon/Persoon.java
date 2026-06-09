package Model.persoon;
import Model.Pathfinder;
import Model.layout.Vakje;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Persoon {

    // Huidige positie
    public Vakje huidigVakje;

    // Huidige bestemming
    public Vakje doelVakje;

    // Pathfinding systeem
    private Pathfinder pathfinder;

    // Wachtrij van tussendoelen
    private Queue<Vakje> tussendoelen = new LinkedList<>();

    public Persoon() {
        this.huidigVakje = null;
        this.doelVakje = null;
    }

    // Zet pathfinder
    public void setPathfinder(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    // Geef pathfinder terug
    public Pathfinder getPathfinder() {
        return this.pathfinder;
    }

    // Zet hoofddoel
    public void zetDoel(Vakje v) {
        this.doelVakje = v;
    }

    // Voeg tussendoel toe
    public void voegTussendoelToe(Vakje v) {
        tussendoelen.add(v);
    }

    // Zet startpositie
    public void zetStartPositie(Vakje v) {
        huidigVakje = v;
        v.voegPersoonToe(this);
    }

    // Verplaats persoon
    public void beweeg() {

        // Gasten in lift bewegen niet zelfstandig
        if (this instanceof Gast g) {
            if (g.inLift) {
                return;
            }
        }

        // Geen doel
        if (doelVakje == null && tussendoelen.isEmpty()) {
            return;
        }

        // Geen huidige positie
        if (huidigVakje == null) {
            return;
        }

        // Doel bereikt
        // Pak volgend tussendoel
        if (huidigVakje.equals(doelVakje)) {
            doelVakje = tussendoelen.poll();
        }

        // Geen nieuw doel
        if (doelVakje == null) {
            return;
        }

        // Gast wacht op lift
        if (this instanceof Gast g) {

            if (g.wachtOpLift) {
                return;
            }
        }

        // Geen pathfinder
        if (pathfinder == null) {
            return;
        }

        // Vraag volgende stap op
        Vakje nieuw = pathfinder.volgendeStap(
                huidigVakje,
                doelVakje
        );

        // Geen mogelijke stap
        if (nieuw == null) {
            return;
        }

        // Verwijder persoon uit oud vakje
        huidigVakje.verwijderPersoon(this);

        // Meld vertrek uit ruimte
        if (huidigVakje.ruimte != null) {
            huidigVakje.ruimte.verlaat(this);
        }

        // Verplaats persoon
        huidigVakje = nieuw;

        // Voeg toe aan nieuw vakje
        nieuw.voegPersoonToe(this);

        // Meld binnenkomst in ruimte
        if (nieuw.ruimte != null) {
            nieuw.ruimte.betreed(this);
        }
    }

    // Wis route
    public void wisRoute() {

        doelVakje = null;
        tussendoelen.clear();
    }

    public void voerTaakUit() {}

    // geeft true als deze persoon een gast is — Gast overschrijft dit
    public boolean isGast() {
        return false;
    }

    // geeft true als deze persoon een schoonmaker is — Schoonmaker overschrijft dit
    public boolean isSchoonmaker() {
        return false;
    }

    // geef een statustekst terug voor het lobbyscherm
    // subklassen overschrijven dit om hun eigen status te tonen
    public String getStatusTekst() {
        return "";
    }
}