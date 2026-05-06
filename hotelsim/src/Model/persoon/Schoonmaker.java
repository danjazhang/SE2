package Model.persoon;

import Model.Hotel;
import Model.IEventListener;
import Model.ILogger;
import Model.events.SchoonmaakEindEvent;
import Model.layout.Vakje;
import Model.ruimte.Kamer;
import Model.ruimte.Ruimte;

import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

// Stelt een schoonmaker voor in het hotel
// Erft van Persoon en implementeert IEventListener
// De schoonmaker is verantwoordelijk voor schoonmaak logica (single responsibility)
// Bij CLEANING_EMERGENCY maakt hij een SchoonmaakEindEvent aan en logt de noodsituatie
public class Schoonmaker extends Persoon implements IEventListener {

    // Aantal NONE-ticks dat een schoonmaakbeurt duurt.
    private static final int SCHOONMAAKDUUR = 5;

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

    // wordt aangeroepen door EventController als er een library event binnenkomt
    // schoonmaker reageert alleen op CLEANING_EMERGENCY
    @Override
    public void onEvent(HotelEvent event) {
        // als het een schoonmaak noodgeval is, maak een SchoonmaakEindEvent aan en log dat
        if (event.getEventType() == HotelEventType.CLEANING_EMERGENCY) {
            // Als hij al met een kamer bezig is, negeren we een nieuw noodgeval voorlopig.
            if (bezig && kamer != null) return;
            SchoonmaakEindEvent eindEvent = new SchoonmaakEindEvent(event.getTime(), event.getGuestId());
            if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Schoonmaker: noodsituatie!");
            this.bezig = true;
            // Gebruik het guestId uit het event om de juiste kamer van die gast te zoeken.
            startNoodschoonmaakVoorGast(event.getGuestId());
        }
    }

    // Wijs een kamer toe die schoongemaakt moet worden.
    // De echte schoonmaak gebeurt later pas wanneer de schoonmaker in die kamer is aangekomen.
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

    private void startNoodschoonmaakVoorGast(int gastId) {
        if (hotel == null || huidigVakje == null) return;

        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                Gast gast = (Gast) p;
                // Zonder gekoppelde kamer is er ook geen schoonmaakdoel.
                if (gast.kamer == null) return;

                maakKamerSchoon(gast.kamer);

                Vakje doel = hotel.layout.krijgVakje(gast.kamer.posX, gast.kamer.posY);
                if (doel == null) return;

                // Bereken de volledige route naar de kamer van de gast.
                java.util.List<Vakje> route = hotel.pathfinder.berekenRoute(huidigVakje, doel);
                if (route.isEmpty()) return;

                zetRouteNaarKamer(route.get(0));
                for (int i = 1; i < route.size(); i++) {
                    voegTussendoelToe(route.get(i));
                }
                if (logger != null) {
                    logger.log("Schoonmaker gaat naar kamer " + gast.kamer.getKamernummer());
                }
                return;
            }
        }
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
