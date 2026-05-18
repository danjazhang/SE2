package Model.persoon;

import Model.Hotel;
import Model.ILogger;
import Model.layout.Vakje;
import Model.ruimte.Kamer;
import Model.ruimte.Ruimte;

// Stelt een schoonmaker voor in het hotel
// Erft van Persoon en voert de taak uit die al eerder door een service is toegewezen.
// De schoonmaker beslist dus niet meer zelf naar welke gast of kamer hij moet gaan.
public class Schoonmaker extends Persoon {

    // Aantal NONE-ticks dat een schoonmaakbeurt duurt.
    private static final int SCHOONMAAKDUUR = 15;

    // of de schoonmaker momenteel bezig is
    public boolean bezig;

    // de kamer die de schoonmaker momenteel schoonmaakt
    public Kamer kamer;

    // logger voor het loggen naar de GUI
    private ILogger logger;
    // Verwijzing naar het hotel is nodig om gasten, kamers en routes op te zoeken.
    private Hotel hotel;
    // De wachtplek is de vaste positie waar de schoonmaker terug naartoe gaat als hij klaar is.
    private Vakje wachtVakje;
    // Houdt bij hoeveel schoonmaaktijd er nog over is terwijl de schoonmaker in de kamer staat.
    private int resterendeSchoonmaakTicks;

    // constructor met logger
    public Schoonmaker(ILogger logger) {
        this.bezig = false;
        this.kamer = null;
        this.logger = logger;
        this.resterendeSchoonmaakTicks = 0;
    }

    // lege constructor voor als er geen logger nodig is (bijv. in testen)
    public Schoonmaker() {
        this.bezig = false;
        this.kamer = null;
        this.resterendeSchoonmaakTicks = 0;
    }

    // Wijs een kamer toe die schoongemaakt moet worden.
    // De echte schoonmaak gebeurt later pas wanneer de schoonmaker in die kamer is aangekomen.
    // Hierdoor zie je eerst de verplaatsing in de simulatie en pas daarna de schoonmaak zelf.
    public void maakKamerSchoon(Kamer k) {
        this.kamer = k;
        this.bezig = true;
    }

    // handel een noodsituatie af
    public void handelEmergency(Kamer k) {
        maakKamerSchoon(k);
    }

    @Override
    public void beweeg() {
        Ruimte oudeRuimte = huidigVakje != null ? huidigVakje.ruimte : null;

        // Als de schoonmaker al in de doelkamer staat, telt hij eerst de schoonmaaktijd af.
        if (bezig && kamer != null && huidigVakje != null && huidigVakje.ruimte == kamer && resterendeSchoonmaakTicks > 0) {
            resterendeSchoonmaakTicks--;
            if (resterendeSchoonmaakTicks == 0) {
                rondSchoonmaakAf();
            }
            return;
        }

        super.beweeg();

        if (!bezig || kamer == null || huidigVakje == null) return;

        Ruimte nieuweRuimte = huidigVakje.ruimte;
        if (nieuweRuimte == kamer && oudeRuimte != kamer) {
            // De schoonmaker is aangekomen en begint nu pas echt met schoonmaken.
            resterendeSchoonmaakTicks = SCHOONMAAKDUUR;
            if (logger != null) {
                logger.log("Schoonmaker begint kamer " + kamer.getKamernummer() + " schoon te maken");
            }
        }
    }

    public void zetRouteNaarKamer(Vakje doelVakje) {
        // Verwijder eerst een oude route, zodat de schoonmaker niet twee taken door elkaar krijgt.
        wisRoute();
        resterendeSchoonmaakTicks = 0;
        if (doelVakje != null) {
            zetDoel(doelVakje);
        }
    }

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public void setWachtVakje(Vakje wachtVakje) {
        this.wachtVakje = wachtVakje;
    }

    private void gaTerugNaarWachtplek() {
        if (hotel == null || huidigVakje == null || wachtVakje == null || huidigVakje == wachtVakje) return;

        // Na de taak keert de schoonmaker terug naar zijn vaste wachtplek in de lobbyzone.
        java.util.List<Vakje> route = hotel.pathfinder.berekenRoute(huidigVakje, wachtVakje);
        if (route.isEmpty()) return;

        zetRouteNaarKamer(route.get(0));
        for (int i = 1; i < route.size(); i++) {
            voegTussendoelToe(route.get(i));
        }
    }

    private void rondSchoonmaakAf() {
        // Pas na de ingestelde schoonmaaktijd wordt de kamer echt schoon gezet.
        kamer.schoonmaken();
        if (logger != null) {
            logger.log("Schoonmaker maakt kamer " + kamer.getKamernummer() + " schoon");
        }
        bezig = false;
        kamer = null;
        gaTerugNaarWachtplek();
    }

    // ga naar de optimale positie in het hotel
    public void gaNaarOptimalePositie() {}
}
