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

    // constructor met logger
    public Schoonmaker(ILogger logger) {
        this.bezig = false;
        this.kamer = null;
        this.resterendeSchoonmaakTicks = 0;
        this.logger = logger;
    }

    // lege constructor voor als er geen logger nodig is (bijv. in testen)
    public Schoonmaker() {
        this.bezig = false;
        this.kamer = null;
        this.resterendeSchoonmaakTicks = 0;
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
            if (logger != null) logger.log("Schoonmaker begint kamer " + kamer.getKamernummer() + " schoon te maken");
        }
    }

    // zet een nieuwe route naar een kamer, wist de oude route eerst
    public void zetRouteNaarKamer(Vakje doelVakje) {
        wisRoute();
        resterendeSchoonmaakTicks = 0;
        if (doelVakje != null) zetDoel(doelVakje);
    }

    public void setLogger(ILogger logger) { this.logger = logger; }

    public void setWachtVakje(Vakje wachtVakje) { this.wachtVakje = wachtVakje; }

    // maak de kamer schoon en ga terug naar de wachtplek als die bekend is
    private void rondSchoonmaakAf() {
        kamer.schoonmaken();
        if (logger != null) logger.log("Schoonmaker heeft " + kamer.getKamernummer() + " schoon gemaakt");
        bezig = false;
        kamer = null;
        // ga terug naar wachtplek als die ingesteld is
        if (wachtVakje != null && huidigVakje != null && huidigVakje != wachtVakje) {
            zetDoel(wachtVakje);
        }
    }

    // ga naar de optimale positie in het hotel
    public void gaNaarOptimalePositie() {}
}
