package Model.persoon;

import Model.ILogger;
import Model.layout.Vakje;
import Model.ruimte.Kamer;

// Verantwoordelijkheid: bewegen, schoonmaaktijd aftellen en kamer schoonmaken.
// Eventkeuze en taaktoewijzing gebeuren buiten deze klasse,
// zodat de schoonmaker zelf alleen uitvoert.
public class Schoonmaker extends Persoon {

    // aantal ticks dat een schoonmaakbeurt duurt
    private static final int SCHOONMAAKDUUR = 6;

    // of de schoonmaker momenteel bezig is
    public boolean bezig;

    // de kamer die de schoonmaker momenteel schoonmaakt
    public Kamer kamer;

    // vaste wachtplek waar de schoonmaker naartoe gaat als hij klaar is
    public Vakje wachtVakje;

    // houdt bij hoeveel schoonmaakticks er nog over zijn
    private int resterendeSchoonmaakTicks;

    // logger voor het loggen naar de GUI
    private ILogger logger;

    // laatst bekende eventtijd voor consistente logberichten
    private int huidigeTijd;

    // bepaalt of deze schoonmaker in de eerste plaats voor noodgevallen bedoeld is
    private boolean noodSchoonmaker;

    // constructor met logger
    public Schoonmaker(ILogger logger) {
        this.bezig = false;
        this.kamer = null;
        this.resterendeSchoonmaakTicks = 0;
        this.logger = logger;
        this.huidigeTijd = 0;
        this.noodSchoonmaker = false;
    }

    // lege constructor voor als er geen logger nodig is (bijv. in testen)
    public Schoonmaker() {
        this.bezig = false;
        this.kamer = null;
        this.resterendeSchoonmaakTicks = 0;
        this.huidigeTijd = 0;
        this.noodSchoonmaker = false;
    }

    // wijs een kamer toe die schoongemaakt moet worden
    // de echte schoonmaak start pas als de schoonmaker in de kamer aankomt
    public void maakKamerSchoon(Kamer k) {
        this.kamer = k;
        this.bezig = true;
    }

    // overschrijft beweeg() van Persoon om schoonmaaktijd af te tellen als de schoonmaker in de kamer staat
    @Override
    public void beweeg() {
        Kamer oudeKamer = null;

        if (huidigVakje != null) {
            if (huidigVakje.ruimte instanceof Kamer) {
                oudeKamer = (Kamer) huidigVakje.ruimte;
            }
        }

        // als de schoonmaker al in de doelkamer staat, tel schoonmaaktijd af
        if (bezig && kamer != null && huidigVakje != null && huidigVakje.ruimte == kamer && resterendeSchoonmaakTicks > 0) {
            resterendeSchoonmaakTicks--;
            if (resterendeSchoonmaakTicks == 0) {
                rondSchoonmaakAf();
            }
            return;
        }

        super.beweeg();

        // check of de schoonmaker net de doelkamer is binnengekomen
        if (bezig && kamer != null && huidigVakje != null && huidigVakje.ruimte == kamer && oudeKamer != kamer) {
            resterendeSchoonmaakTicks = SCHOONMAAKDUUR;
            if (logger != null) logger.log("[" + huidigeTijd + "] Schoonmaker begint kamer " + kamer.getKamernummer() + " schoon te maken");
        }
    }

    // zet een nieuwe route naar een kamer, wist de oude route eerst
    public void zetRouteNaarKamer(Vakje doelVakje) {
        wisRoute();
        resterendeSchoonmaakTicks = 0;
        zetRouteViaTrap(doelVakje);
    }

    public void setLogger(ILogger logger) { this.logger = logger; }

    public void setHuidigeTijd(int huidigeTijd) { this.huidigeTijd = huidigeTijd; }

    public void setWachtVakje(Vakje wachtVakje) { this.wachtVakje = wachtVakje; }

    // markeer deze schoonmaker als voorkeurskeuze voor noodgevallen of gewone checkout-schoonmaak
    public void setNoodSchoonmaker(boolean noodSchoonmaker) { this.noodSchoonmaker = noodSchoonmaker; }

    public boolean isNoodSchoonmaker() { return noodSchoonmaker; }

    // maak de kamer schoon en ga terug naar de wachtplek als die bekend is
    private void rondSchoonmaakAf() {
        kamer.schoonmaken();
        if (logger != null) logger.log("[" + huidigeTijd + "] Schoonmaker heeft " + kamer.getKamernummer() + " schoon gemaakt");
        bezig = false;
        kamer = null;
        // ga ook voor de terugweg via de pathfinder, zodat de schoonmaker
        // net als op de heenweg steeds de trap blijft gebruiken
        if (wachtVakje != null && huidigVakje != null && huidigVakje != wachtVakje) {
            wisRoute();
            zetRouteViaTrap(wachtVakje);
        }
    }

    // gebruik altijd de traproute wanneer de schoonmaker een nieuw doel krijgt;
    // zo loopt hij niet dwars door kamers heen bij een andere verdieping
    private void zetRouteViaTrap(Vakje doelVakje) {
        if (doelVakje == null) return;
        if (getPathfinder() != null) {
            getPathfinder().zetRouteTrap(this, doelVakje);
        } else {
            zetDoel(doelVakje);
        }
    }

    // ga naar de optimale positie in het hotel
    public void gaNaarOptimalePositie() {}
}
