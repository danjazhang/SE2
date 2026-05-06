package Model.persoon;

import Model.Pathfinder;
import Model.layout.Vakje;

import java.util.Queue;
import java.util.LinkedList;

// Basisklasse voor alle personen in het hotel
// Gast en Schoonmaker erven van deze klasse
public abstract class Persoon {

    // het vakje waar de persoon zich momenteel bevindt
    public Vakje huidigVakje;

    // het vakje waar de persoon naartoe wil
    public Vakje doelVakje;

    // pathfinder voor het berekenen van de volgende stap
    private Pathfinder pathfinder;

    //queue is first in first out
    //linkedlist verwijst de elementen naar elkaar
    private Queue<Vakje> tussendoelen = new LinkedList<>();

    // constructor: persoon begint zonder positie
    public Persoon() {
        this.huidigVakje = null;
        this.doelVakje = null;
    }

    // stel de pathfinder in
    public void setPathfinder(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    // stel het doelVakje in
    public void zetDoel(Vakje v) { 
        this.doelVakje = v; 
        }

    public void voegTussendoelToe(Vakje v){
        tussendoelen.add(v);

        if (doelVakje == null){
            doelVakje = tussendoelen.poll();
        }
    }

    public void wisRoute() {
        // Maak de huidige route volledig leeg, zodat een nieuw doel netjes opnieuw kan starten.
        doelVakje = null;
        tussendoelen.clear();
    }

    //zet persoon op een startvakje
    public void zetStartPositie(Vakje v) {
        huidigVakje = v;
        //voeg aan personenlijst van dat vakje
        v.voegPersoonToe(this);
    }

    // beweeg de persoon 1 stap richting het doelVakje
    public void beweeg() {
        //als doelvakje of tussendoel leeg is dan stopt de methode
        if (doelVakje == null && tussendoelen.isEmpty()) return;
        // persoon staat nergens
        if (huidigVakje == null) return;

        if (huidigVakje == doelVakje && !tussendoelen.isEmpty()) {
        doelVakje = tussendoelen.poll();
        }//poll is eerste element ophalen en verwijderen

        if (doelVakje == null || huidigVakje == doelVakje) return;

        
        //stop als pathfinder niet bestaat
        if (pathfinder == null) return;
        //zoek vakje op nieuwe positie van persoon via pathfinder
        Vakje nieuwVakje = pathfinder.volgendeStap(huidigVakje, doelVakje);
        //stop als vakje niet bestaat
        if (nieuwVakje == null) return;

        // verwijder van huidig vakje
        huidigVakje.verwijderPersoon(this);
        //verwijder persoon uit ruimte als er eentje is
        if (huidigVakje.ruimte != null) huidigVakje.ruimte.verlaat(this);

        // zet persoon op nieuw vakje
        huidigVakje = nieuwVakje;
        //voeg persoon toe aan de personenlijst van dat vakje
        nieuwVakje.voegPersoonToe(this);
        // voeg persoon toe aan ruimte als dat bestaat
        if (nieuwVakje.ruimte != null) nieuwVakje.ruimte.betreed(this);
    }

    // voer de taak van de persoon uit
    public void voerTaakUit() {}
}
