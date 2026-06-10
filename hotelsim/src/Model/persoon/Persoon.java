package Model.persoon;

import Model.Pathfinder;
import Model.layout.Vakje;
import java.util.LinkedList;
import java.util.Queue;

// Basisklasse voor alle personen in het hotel
// Gast en Schoonmaker erven van deze klasse
public abstract class Persoon {

    // huidige positie op het grid
    public Vakje huidigVakje;

    // huidige bestemming
    public Vakje doelVakje;

    // pathfinding systeem voor het berekenen van routes
    private Pathfinder pathfinder;

    // wachtrij van tussendoelen die na het huidige doel afgewerkt worden
    private Queue<Vakje> tussendoelen = new LinkedList<>();

    public Persoon() {
        this.huidigVakje = null;
        this.doelVakje = null;
    }

    // stel de pathfinder in
    public void setPathfinder(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    // geef de pathfinder terug
    public Pathfinder getPathfinder() {
        return this.pathfinder;
    }

    // stel het hoofddoel in
    public void zetDoel(Vakje v) {
        this.doelVakje = v;
    }

    // voeg een tussendoel toe aan de wachtrij
    public void voegTussendoelToe(Vakje v) {
        tussendoelen.add(v);
    }

    // zet de startpositie van de persoon
    public void zetStartPositie(Vakje v) {
        huidigVakje = v;
        v.voegPersoonToe(this);
    }

    // verplaats de persoon één stap richting het doel
    public void beweeg() {

        // gasten in lift bewegen niet zelfstandig, de lift verplaatst hen
        if (this instanceof Gast g) {
            if (g.inLift) return;
        }

        // stop als er geen doel is
        if (doelVakje == null && tussendoelen.isEmpty()) return;

        // stop als er geen huidige positie is
        if (huidigVakje == null) return;

        // doel bereikt: pak het volgende tussendoel uit de wachtrij
        if (huidigVakje.equals(doelVakje)) {
            doelVakje = tussendoelen.poll();
        }

        // stop als er geen nieuw doel is
        if (doelVakje == null) return;

        // gast wacht op de lift, beweeg niet
        if (this instanceof Gast g) {
            if (g.wachtOpLift) return;
        }

        // stop als er geen pathfinder is
        if (pathfinder == null) return;

        // vraag de volgende stap op aan de pathfinder
        Vakje nieuw = pathfinder.volgendeStap(huidigVakje, doelVakje);

        // stop als er geen mogelijke stap is
        if (nieuw == null) return;

        // verwijder persoon van het oude vakje
        huidigVakje.verwijderPersoon(this);

        // meld vertrek uit de ruimte als die er is
        if (huidigVakje.ruimte != null) huidigVakje.ruimte.verlaat(this);

        // verplaats persoon naar het nieuwe vakje
        huidigVakje = nieuw;

        // Voeg toe aan nieuw vakje
        nieuw.voegPersoonToe(this);

        // meld binnenkomst in de ruimte als die er is
        if (nieuw.ruimte != null) nieuw.ruimte.betreed(this);
    }

    // wis de huidige route zodat de persoon stopt met bewegen
    public void wisRoute() {
        doelVakje = null;
        tussendoelen.clear();
    }

    // evacueer naar de uitgang via de trap
    // standaard gedrag: wis route en loop naar de uitgang
    // subklassen kunnen dit overschrijven voor ander gedrag
    public void evacueer(Vakje uitgang, Pathfinder pathfinder) {
        if (huidigVakje == null || pathfinder == null) return;
        // wis de huidige route zodat de persoon niet meer naar zijn oude bestemming loopt
        wisRoute();
        this.doelVakje = uitgang;
        // gebruik altijd de trap, nooit de lift
        pathfinder.zetRouteTrap(this, uitgang);
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