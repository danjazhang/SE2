package Model.ruimte;

import Model.*;
import Model.IEventListener;
import Model.ILogger;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

import java.util.List;

public class Lobby extends Ruimte implements IEventListener {

    //positie van
    private int balieX;
    private int balieY;
    private Hotel hotel;
    private ILogger logger;

    public Lobby(int posX, int posY, int breedte, int hoogte, int balieX, int balieY, Hotel hotel, ILogger logger) {
        super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
        this.hotel = hotel;
        this.logger = logger;
    }

    @Override
    //onEvent wordt aangeroepen bij elk event
    public void onEvent(HotelEvent event) {
        //check of het een checkin of checkout event is en roep de juiste methode aan
        if (event.getEventType() == HotelEventType.CHECK_IN){
            behandelCheckIn(event.getGuestId(), event.getTime());
        } else if (event.getEventType()== HotelEventType.CHECK_OUT){
            behandelCheckOut(event.getGuestId(), event.getTime());
        }
    }

    private void behandelCheckIn(int gastId, int tijd) {
        // Maak de gast aan en laat hem starten bij de balie in de lobby.
        // Vanaf daar kan hij zichtbaar zijn eerste route door het hotel volgen.
        Vakje startVakje = hotel.layout.krijgVakje(balieX, hotel.hoogte);
        PersonenFactory personenFactory = new PersonenFactory();
        Gast gast = personenFactory.maakGast(gastId, 1, hotel.pathfinder, startVakje);
        Kamer toegewezenKamer = null;

        //voeg persoon toe aan personenlijst in hotel
        hotel.voegPersoonToe(gast);

        //zoek een vrije schone kamer
        Kamer kamer = vindVrijeKamer();
        if (kamer != null) {
            //koppel de gast aan kamer
            kamer.koppelGast(gast);
            toegewezenKamer = kamer;

            // De kamer wordt het einddoel van de route van de gast.
            // Pathfinder berekent daarna welke tussenstappen nodig zijn,
            // bijvoorbeeld eerst naar de lift of trap en daarna naar de juiste kamer.
            Vakje doel = hotel.layout.krijgVakje(kamer.posX, kamer.posY);
            if (startVakje != null && doel != null){
                Pathfinder pathfinder = new Pathfinder(hotel);
                List<Vakje> route = pathfinder.berekenRoute(startVakje, doel);

                // Het eerste vakje uit de route wordt meteen het huidige doel.
                // De overige vakjes bewaren we als tussenstappen, zodat de gast
                // stap voor stap de hele route kan afwerken.
                gast.zetDoel(route.get(0));
                for (int i = 1; i< route.size(); i++){
                    gast.voegTussendoelToe(route.get(i));
                }
            }
        }
        if (logger != null) {
            if (toegewezenKamer != null) {
                // We tonen hier ook het kamernummer in de log.
                // Dat is handig omdat dezelfde gast later ook naar restaurant,
                // cinema of fitness kan gaan. Zo blijft duidelijk bij welke kamer
                // die gast oorspronkelijk hoort.
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt in kamer no " + toegewezenKamer.getKamernummer());
            } else {
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt in, maar er is geen vrije kamer");
            }
        }
        hotel.notifyListeners();
    }

    private void behandelCheckOut(int gastId, int tijd) {
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                Gast gast = (Gast) p;
                // Bewaar eerst de kamer, want na het ontkoppelen heeft de gast
                // zelf geen verwijzing naar zijn oude kamer meer.
                Kamer kamer = gast.kamer;
                if (kamer != null) {
                    kamer.ontkoppelGast(gast);
                }
                // Zoek een vrije schoonmaker voor de kamer die net is vrijgekomen.
                // Zo kan de kamer weer schoon worden gemaakt voor een volgende gast.
                Schoonmaker schoonmaker = vindVrijeSchoonmaker();
                // Alleen schoonmaken als er echt een kamer bestaat
                // en er ook een vrije schoonmaker beschikbaar is.
                if (schoonmaker != null && kamer != null) {
                    schoonmaker.maakKamerSchoon(kamer);
                    stuurSchoonmakerNaarKamer(schoonmaker, kamer);
                }
                if (logger != null) {
                    if (kamer != null) {
                        // Ook bij uitchecken tonen we het kamernummer,
                        // zodat in de eventlog duidelijk blijft welke kamer vrijkomt.
                        logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt uit uit kamer " + kamer.getKamernummer());
                    } else {
                        logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt uit");
                    }
                }
                hotel.notifyListeners();
                break;
            }
        }
    }

    private Kamer vindVrijeKamer() {
        //loop door alle ruimtes
        for (Ruimte r : hotel.ruimtes) {
            //check of het een kamer is
            if (r instanceof Kamer) {
                Kamer k = (Kamer) r;
                //check of het niet bezet is en of die schoon is
                if (!k.isBezet() && k.isSchoon()) return k;
            }
        }//geef null terug als er geen vrije kamer is
        return null;
    }

    private Schoonmaker vindVrijeSchoonmaker() {
        //loop door alle personen
        for (Persoon p : hotel.personen) {
            //zoek een schoonmaker die niet bezig is
            if (p instanceof Schoonmaker && !((Schoonmaker) p).bezig) return (Schoonmaker) p;
        }
        //geef null teruug als alle schoonmakers bezig zijn
        return null;
    }

    private void stuurSchoonmakerNaarKamer(Schoonmaker schoonmaker, Kamer kamer) {
        if (schoonmaker.huidigVakje == null) return;

        Vakje doel = hotel.layout.krijgVakje(kamer.posX, kamer.posY);
        if (doel == null) return;

        List<Vakje> route = hotel.pathfinder.berekenRoute(schoonmaker.huidigVakje, doel);
        if (route.isEmpty()) return;

        schoonmaker.zetRouteNaarKamer(route.get(0));
        for (int i = 1; i < route.size(); i++) {
            schoonmaker.voegTussendoelToe(route.get(i));
        }
    }

    public void setLogger(ILogger logger) { this.logger = logger; }
    public void toonStatusScherm() { System.out.println("Status van hotel wordt getoond..."); }
    public int getBalieX() { return balieX; }
    public int getBalieY() { return balieY; }
}
