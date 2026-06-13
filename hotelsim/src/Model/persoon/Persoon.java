package Model.persoon;

import Model.Pathfinder;
import Model.layout.Vakje;
import java.util.LinkedList;
import java.util.Queue;

// Verantwoordelijkheid: basisklasse voor alle personen in het hotel.
public abstract class Persoon {

    public Vakje huidigVakje;
    public Vakje doelVakje;
    private Pathfinder pathfinder;

    // queue van type Vakje met de naam tussendoelen is een nieuwe LinkedList, die rij wordt opgeslagen.
    private Queue<Vakje> tussendoelen = new LinkedList<>();

// start
    public Persoon() {
        this.huidigVakje = null;
        this.doelVakje = null;
    }

    // setPathfinder met een pathfinder als parameter, dus ik geef een pathfinder door aan deze methode
    public void setPathfinder(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    // geeft de opgeslagen pathfinder van dit object terug aan degene die het aanroept
    public Pathfinder getPathfinder() {
        return this.pathfinder;
    }

    // sla dit vakje op als doelvakje van dit object
    public void zetDoel(Vakje v) {
        this.doelVakje = v;
    }

    // Voeg vakje v toe aan het einde van de tussendoelenwachtrij.
    public void voegTussendoelToe(Vakje v) {
        tussendoelen.add(v);
    }

    // Zet de startpositie: sla vakje v op als huidigVakje en voeg deze persoon toe aan dat vakje.
    public void zetStartPositie(Vakje v) {
        huidigVakje = v;
        v.voegPersoonToe(this);
    }

    // kern van de bewegingslogica.
    public void beweeg() {
        //
        if (this instanceof Gast g) {
            if (g.inLift) return;
        }

        if (doelVakje == null && tussendoelen.isEmpty()) return;

        if (huidigVakje == null) return;


        if (huidigVakje.equals(doelVakje)) {
            //het volgende vakje uit de wachtrij met poll en dat wordt het nieuwe doe
            doelVakje = tussendoelen.poll();
        }

        if (doelVakje == null) return;

        if (this instanceof Gast g) {
            if (g.wachtOpLift) return;
        }

        if (pathfinder == null) return;

        //wat de volgende stap is van mijn huidige vakje naar mijn doelvakje en sla dat op in de variabele nieuw
        Vakje nieuw = pathfinder.volgendeStap(huidigVakje, doelVakje);


        if (nieuw == null) return;

        //bij verplaatsing
        huidigVakje.verwijderPersoon(this);

        // Als het huidige vakje een ruimte heeft , deze persoon de ruimte verlaat.
        if (huidigVakje.ruimte != null) huidigVakje.ruimte.verlaat(this);

        // Sla het nieuwe vakje op als huidigVakje, de persoon is nu verplaatst.
        huidigVakje = nieuw;

        // Voeg deze persoon toe aan het nieuwe vakje.
        nieuw.voegPersoonToe(this);

        // Als het nieuwe vakje een ruimte heeft , persoon betreed de ruimte.
        if (nieuw.ruimte != null) nieuw.ruimte.betreed(this);
    }

    // verwijderen van routen
    public void wisRoute() {
        doelVakje = null;
        tussendoelen.clear();
    }

    // Stuur de persoon naar de uitgang bij een brandalarm.
    public void evacueer(Vakje uitgang, Pathfinder pathfinder) {
        if (huidigVakje == null || pathfinder == null) return;
        wisRoute();
        // route berekenen met trap voor persoon
        pathfinder.zetRouteTrap(this, uitgang);
    }
}
