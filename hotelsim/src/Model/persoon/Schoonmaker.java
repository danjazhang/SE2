package Model.persoon;

import Model.ILogger;
import Model.Pathfinder;
import Model.layout.Vakje;
import Model.ruimte.Kamer;

public class Schoonmaker extends Persoon {

    // voor beide Schoonmakers hetzelfde
    private static final int SCHOONMAAKDUUR = 6;

    public boolean bezig;
    public Kamer kamer;
    public Vakje wachtVakje;
    private int resterendeSchoonmaakTicks;
    private ILogger logger;
    private int huidigeTijd;
    private boolean noodSchoonmaker;

    // C: logger meegegeven
    public Schoonmaker(ILogger logger) {
        this.bezig = false;
        this.kamer = null;
        this.resterendeSchoonmaakTicks = 0;
        this.logger = logger;
        this.huidigeTijd = 0;
        this.noodSchoonmaker = false;
    }

    // C:zonder logger
    public Schoonmaker() {
        this.bezig = false;
        this.kamer = null;
        this.resterendeSchoonmaakTicks = 0;
        this.huidigeTijd = 0;
        this.noodSchoonmaker = false;
    }


    // Zet bezig op true zodat de schoonmaker weet dat hij een taak heeft.
    public void maakKamerSchoon(Kamer k) {
        this.kamer = k;
        this.bezig = true;
    }

    // deze methode vervangt beweeg() van de klasse Persoon.
    @Override
    public void beweeg() {
        // om bij te houden of ik net in een kamer ben geweest
        Kamer oudeKamer = null;
        if (huidigVakje != null && huidigVakje.ruimte instanceof Kamer) {
            // en cast ik hem naar Kamer zodat ik kamer-eigenschappen kan gebruiken
            oudeKamer = (Kamer) huidigVakje.ruimte;
        }

        // Als de schoonmaker bezig  is aan, én kamer niet leeg is, én huidigVakje niet leeg is,
        // én de ruimte op huidigVakje gelijk is aan (==) de doelkamer,
        // én resterendeSchoonmaakTicks groter is dan 0:
        // tel dan één tick af.
        if (bezig && kamer != null && huidigVakje != null && huidigVakje.ruimte == kamer && resterendeSchoonmaakTicks > 0) {
            resterendeSchoonmaakTicks--;
            // Als resterendeSchoonmaakTicks nu gelijk is aan 0, is de schoonmaak klaar.
            if (resterendeSchoonmaakTicks == 0) rondSchoonmaakAf();
            return;
        }

        // Roep de beweeg() van de supper klasse Persoon aan. voor bij het niet schoonmaken
        super.beweeg();

        // Als de schoonmaker bezig  is aan, én kamer niet leeg is, én huidigVakje niet leeg is,
        // en de ruimte van het huidige vakje gelijk is aan de kamer,
        // en de oude kamer niet gelijk is aan de huidige kamer,
        // dan zet ik resterende schoonmaakticks gelijk aan de vaste schoonmaakduur.”
        if (bezig && kamer != null && huidigVakje != null && huidigVakje.ruimte == kamer && oudeKamer != kamer) {
            resterendeSchoonmaakTicks = SCHOONMAAKDUUR;
            // als de logger niet leeg is, dus niet null, dan log ik een bericht met de huidige tijd
            // en zeg ik dat de schoonmaker begint met het schoonmaken van de kamer met dat kamernummer
            if (logger != null) logger.log("[" + huidigeTijd + "] Schoonmaker begint kamer " + kamer.getKamernummer() + " schoon te maken");
        }
    }

    @Override
    public void evacueer(Vakje uitgang, Pathfinder pathfinder) {
        // Als huidigVakje leeg is  of pathfinder leeg is , stop dan.
        if (huidigVakje == null || pathfinder == null) return;
        // Wis alleen de route, niet de kamertoewijzing.
        // De kamer blijft bewaard zodat de schoonmaker na het alarm verder kan.
        wisRoute();
        // Gebruik altijd de trap, nooit de lift.
        pathfinder.zetRouteTrap(this, uitgang);
    }

    // Wis de huidige route en reset de schoonmaakteller, dan zet een nieuwe route naar de kamer.
    public void zetRouteNaarKamer(Vakje doelVakje) {
        wisRoute();
        resterendeSchoonmaakTicks = 0;
        zetRouteViaTrap(doelVakje);
    }

    // Setters en getters: methoden om private variabelen van buitenaf in te stellen of op te vragen.
    public void setLogger(ILogger logger) { this.logger = logger; }
    public void setHuidigeTijd(int huidigeTijd) { this.huidigeTijd = huidigeTijd; }
    public void setWachtVakje(Vakje wachtVakje) { this.wachtVakje = wachtVakje; }
    public void setNoodSchoonmaker(boolean noodSchoonmaker) { this.noodSchoonmaker = noodSchoonmaker; }
    public boolean isNoodSchoonmaker() { return noodSchoonmaker; }

    // Maak de kamer schoon en stuur de schoonmaker terug naar zijn wachtplek.
    // Dit is een 'private' methode: alleen deze klasse mag hem aanroepen.
    private void rondSchoonmaakAf() {
        // Roep schoonmaken() aan op de kamer zodat kamer.schoon gelijk wordt aan true.
        kamer.schoonmaken();
        if (logger != null) logger.log("[" + huidigeTijd + "] Schoonmaker heeft " + kamer.getKamernummer() + " schoon gemaakt");
        // Zet bezig op false en wis de kamertoewijzing zodat de schoonmaker weer beschikbaar is.
        bezig = false;
        kamer = null;
        // Als er een wachtplek is én de schoonmaker is er nog niet, stuur hem er dan naartoe.
        if (wachtVakje != null && huidigVakje != null && huidigVakje != wachtVakje) {
            wisRoute();
            zetRouteViaTrap(wachtVakje);
        }
    }

    // Zet altijd een route via de trap, nooit via de lift.
    // Als er geen pathfinder is, zet dan gewoon het doel direct.
    private void zetRouteViaTrap(Vakje doelVakje) {
        if (doelVakje == null) return;
        if (getPathfinder() != null) {
            getPathfinder().zetRouteTrap(this, doelVakje);
        } else {
            zetDoel(doelVakje);
        }
    }

}
