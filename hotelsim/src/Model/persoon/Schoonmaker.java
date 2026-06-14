package Model.persoon;

import Model.ILogger;
import Model.Pathfinder;
import Model.layout.Vakje;
import Model.ruimte.Kamer;

// Verantwoordelijkheid: bewegen, schoonmaaktijd aftellen en kamer schoonmaken
public class Schoonmaker extends Persoon {

    // aantal ticks dat een schoonmaakbeurt duurt — instelbaar via setSchoonmaakDuur()
    private int schoonmaakDuur = 20;

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
    public void maakKamerSchoon(Kamer k) {
        this.kamer = k;
        this.bezig = true;
    }

    // overschrijft beweeg() van Persoon om schoonmaaktijd af te tellen als de schoonmaker in de kamer staat
    @Override
    public void beweeg() {
        Kamer oudeKamer = null;
        if (huidigVakje != null && huidigVakje.ruimte instanceof Kamer) {
            oudeKamer = (Kamer) huidigVakje.ruimte;
        }

        // als de schoonmaker in de doelkamer staat, tel schoonmaaktijd af
        if (bezig && kamer != null && huidigVakje != null && huidigVakje.ruimte == kamer && resterendeSchoonmaakTicks > 0) {
            resterendeSchoonmaakTicks--;
            if (resterendeSchoonmaakTicks == 0) rondSchoonmaakAf();
            return;
        }

        super.beweeg();

        // check of de schoonmaker net de doelkamer is binnengekomen
        if (bezig && kamer != null && huidigVakje != null && huidigVakje.ruimte == kamer && oudeKamer != kamer) {
            resterendeSchoonmaakTicks = schoonmaakDuur;
            if (logger != null) logger.log("[" + huidigeTijd + "] Schoonmaker begint kamer " + kamer.getKamernummer() + " schoon te maken");
        }
    }

    // overschrijft evacueer() van Persoon
    // schoonmaker onthoudt zijn kamer zodat hij die na het alarm kan afmaken
    // daarna loopt hij ook naar de uitgang via de trap
    @Override
    public void evacueer(Vakje uitgang, Pathfinder pathfinder) {
        if (huidigVakje == null || pathfinder == null) return;
        // wis alleen de route, niet de kamertoewijzing
        // kamer blijft bewaard zodat de schoonmaker na het alarm verder kan
        wisRoute();
        // gebruik altijd de trap, nooit de lift
        pathfinder.zetRouteTrap(this, uitgang);
    }

    // zet een nieuwe route naar een kamer, wist de oude route eerst
    public void zetRouteNaarKamer(Vakje doelVakje) {
        wisRoute();
        resterendeSchoonmaakTicks = 0;
        zetRouteViaTrap(doelVakje);
    }

    public void setLogger(ILogger logger) { this.logger = logger; }

    // stel de schoonmaakduur in — standaard 20 ticks
    public void setSchoonmaakDuur(int duur) { this.schoonmaakDuur = duur; }
    public int getSchoonmaakDuur() { return schoonmaakDuur; }
    public void setHuidigeTijd(int huidigeTijd) { this.huidigeTijd = huidigeTijd; }
    public void setWachtVakje(Vakje wachtVakje) { this.wachtVakje = wachtVakje; }
    public void setNoodSchoonmaker(boolean noodSchoonmaker) { this.noodSchoonmaker = noodSchoonmaker; }
    public boolean isNoodSchoonmaker() { return noodSchoonmaker; }
    public boolean staatOpWachtVakje() { return wachtVakje != null && huidigVakje == wachtVakje; }

    public void gaNaarWachtVakje() {
        if (wachtVakje != null && huidigVakje != null && huidigVakje != wachtVakje) {
            wisRoute();
            zetRouteViaTrap(wachtVakje);
        }
    }

    // maak de kamer schoon; een nieuwe taak of terugkeer naar de wachtplek
    // wordt centraal afgehandeld door de schoonmaakservice
    private void rondSchoonmaakAf() {
        kamer.schoonmaken();
        if (logger != null) logger.log("[" + huidigeTijd + "] Schoonmaker heeft " + kamer.getKamernummer() + " schoon gemaakt");
        bezig = false;
        kamer = null;
    }

    // gebruik altijd de traproute zodat de schoonmaker nooit de lift neemt
    private void zetRouteViaTrap(Vakje doelVakje) {
        if (doelVakje == null) return;
        if (getPathfinder() != null) {
            getPathfinder().zetRouteTrap(this, doelVakje);
        } else {
            zetDoel(doelVakje);
        }
    }

    public void gaNaarOptimalePositie() {}

    @Override
    public boolean isSchoonmaker() { return true; }

    @Override
    public String getStatusTekst() {
        String status;
        if (bezig && kamer != null) {
            status = "bezig met kamer " + kamer.getKamernummer();
        } else if (bezig) {
            status = "onderweg naar kamer";
        } else {
            status = "vrij inzetbaar";
        }

        String positie;
        if (huidigVakje != null) {
            positie = "(" + huidigVakje.x + "," + huidigVakje.y + ")";
        } else {
            positie = "geen positie";
        }

        return "Schoonmaker " + positie + " : " + status;
    }
}
