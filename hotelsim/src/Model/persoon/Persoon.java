package Model.persoon;

import Model.Pathfinder;
import Model.layout.Vakje;

import java.util.Queue;
import java.util.LinkedList;

public abstract class Persoon {

    public Vakje huidigVakje;
    public Vakje doelVakje;

    private Pathfinder pathfinder;
    private Queue<Vakje> tussendoelen = new LinkedList<>();

    public Persoon() {
        this.huidigVakje = null;
        this.doelVakje = null;
    }

    public void setPathfinder(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    public Pathfinder getPathfinder() {
        return this.pathfinder;
    }

    public void zetDoel(Vakje v) {
        this.doelVakje = v;
    }

    public void voegTussendoelToe(Vakje v) {
        tussendoelen.add(v);
    }

    public void zetStartPositie(Vakje v) {
        huidigVakje = v;
        v.voegPersoonToe(this);
    }

    public void beweeg() {

        // Gasten in lift bewegen niet zelf
        if (this instanceof Gast g) {
            if (g.inLift) return;
        }

        // Geen doel
        if (doelVakje == null && tussendoelen.isEmpty()) return;
        if (huidigVakje == null) return;

        // Doel bereikt: pak volgende
        if (huidigVakje.equals(doelVakje)) {
            doelVakje = tussendoelen.poll();
        }

        if (doelVakje == null) return;

        // Gast wacht op lift
        if (this instanceof Gast g) {
            if (g.wachtOpLift) return;
        }

        if (pathfinder == null) return;

        Vakje nieuw = pathfinder.volgendeStap(huidigVakje, doelVakje);
        if (nieuw == null) return;

        // Verplaats
        huidigVakje.verwijderPersoon(this);
        if (huidigVakje.ruimte != null) huidigVakje.ruimte.verlaat(this);

        huidigVakje = nieuw;
        nieuw.voegPersoonToe(this);
        if (nieuw.ruimte != null) nieuw.ruimte.betreed(this);
    }

    public void wisRoute() {
        doelVakje = null;
        tussendoelen.clear();
    }

    public void voerTaakUit() {}
}
