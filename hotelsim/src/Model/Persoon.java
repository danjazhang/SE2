package Model;

import java.util.Queue;
import java.util.LinkedList;

// Basisklasse voor alle personen in het hotel
// Gast en Schoonmaker erven van deze klasse
public class Persoon {

    // het vakje waar de persoon zich momenteel bevindt
    public Vakje huidigVakje;

    // het vakje waar de persoon naartoe wil
    public Vakje doelVakje;

    //layout zodat persoon vakjes kan opzoeken
    public Layout layout;

    private Queue<Vakje> tussendoelen = new LinkedList<>();

    // constructor: persoon begint zonder positie
    public Persoon() {
        this.huidigVakje = null;
        this.doelVakje = null;
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
        }

        if (doelVakje == null || huidigVakje == doelVakje) return;

        //haal x en y coordinaten om mee te rekenen
        int huidigX = huidigVakje.x;
        int huidigY = huidigVakje.y;
        int doelX = doelVakje.x;
        int doelY = doelVakje.y;

        //beweeg 1 stap in x of y richting
        //beginnen met nieuwe positie gelijk aan huidige en dan aanpassen
        int nieuweX = huidigX;
        int nieuweY = huidigY;

        //beweeg 1 stap, eerst x daarna y
        if (huidigX < doelX) nieuweX++;
        else if (huidigX > doelX) nieuweX--;
        else if (huidigY < doelY) nieuweY++;
        else if (huidigY > doelY) nieuweY--;

        // haal het nieuwe vakje op via de layout
        //stop als layout niet bestaat
        if (layout == null) return;
        //zoek vakje op nieuwe positie van persoon via layout
        Vakje nieuwVakje = layout.krijgVakje(nieuweX, nieuweY);
        //stop als vakje niet bestaat
        if (nieuwVakje == null) return;

        // verwijder van huidig vakje
        huidigVakje.verwijderPersoon(this);
        //verwokder persoon uit ruimte als er eentje is
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
