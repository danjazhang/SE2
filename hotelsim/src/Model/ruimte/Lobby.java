package Model.ruimte;

import Model.*;
import Model.events.IEventListener;
import Model.ILogger;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

public class Lobby extends Ruimte implements IEventListener {

    // positie van de balie
    private int balieX;
    private int balieY;
    private Hotel hotel;
    private ILogger logger;
    private PersonenService personenService;

    public Lobby(int posX, int posY, int breedte, int hoogte, int balieX, int balieY, Hotel hotel, ILogger logger) {
        super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
        this.hotel = hotel;
        this.logger = logger;
        this.personenService = new PersonenService(hotel);
    }

    @Override
    public void onEvent(HotelEvent event) {
        // check of het een checkin of checkout event is en roep de juiste methode aan
        if (event.getEventType() == HotelEventType.CHECK_IN) {
            behandelCheckIn(event.getGuestId(), event.getTime(), event.getData());
        } else if (event.getEventType() == HotelEventType.CHECK_OUT) {
            behandelCheckOut(event.getGuestId(), event.getTime());
        }
    }

    private void behandelCheckIn(int gastId, int tijd, int gewensteSterren) {
        // zet gast op balie als startpunt
        Vakje startVakje = hotel.layout.krijgVakje(balieX, hotel.hoogte);

        // zoek eerst een geschikte kamer voordat de gast aangemaakt wordt
        Kamer kamer = vindGeschikteKamer(gewensteSterren);

        if (kamer == null) {
            // geen geschikte kamer: gast wordt geweigerd, niet aangemaakt
            if (logger != null) logger.log("[" + tijd + "] Lobby: gast " + gastId + " geweigerd, geen " + gewensteSterren + "★ kamer beschikbaar");
            return;
        }

        // maak gast aan met zijn gewenste sterrenklasse
        Gast gast = personenService.maakGast(gastId, gewensteSterren, startVakje);

        // koppel de gast aan de kamer en stuur hem erheen
        kamer.koppelGast(gast);
        hotel.pathfinder.zetRoute(gast, kamer);
        if (logger != null) logger.log("[" + tijd + "] Lobby: gast " + gastId + " (" + gewensteSterren + "★) checkt in kamer " + kamer.getKamernummer() + " (" + kamer.sterren + "★)");
    }

    private void behandelCheckOut(int gastId, int tijd) {
        // zoek de gast op basis van id
        Gast gast = personenService.vindGast(gastId);
        if (gast == null) return;
        // sla kamer op want na uitchecken is kamer null
        Kamer kamer = gast.kamer;
        if (kamer != null) kamer.ontkoppelGast(gast);
        // gebruik bij gewone check-out eerst de standaard schoonmaker
        Schoonmaker schoonmaker = personenService.vindVrijeSchoonmakerVoorCheckOut();
        // check of er een schoonmaker is en of de gast een kamer had
        if (schoonmaker != null && kamer != null) {
            schoonmaker.maakKamerSchoon(kamer);
            // stuur schoonmaker naar de kamer via een route
            hotel.pathfinder.zetRoute(schoonmaker, kamer);
        }
        // markeer gast als uitcheckend, wis oude route en stuur naar de lobby
        // zodra de gast de lobby bereikt wordt hij grafisch verwijderd via betreed()
        gast.uitcheckend = true;
        gast.wisRoute();
        hotel.pathfinder.zetRoute(gast, this);
        if (logger != null) {
            if (kamer != null) {
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt uit uit kamer " + kamer.getKamernummer());
            } else {
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt uit");
            }
        }
    }

    // zoek een geschikte kamer: eerst exact, dan hoger, anders null
    private Kamer vindGeschikteKamer(int gewensteSterren) {
        // stap 1: zoek kamer met exact het gewenste aantal sterren
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Kamer) {
                Kamer k = (Kamer) r;
                if (!k.isBezet() && k.isSchoon() && k.sterren == gewensteSterren) return k;
            }
        }
        // stap 2: zoek kamer met meer sterren dan gewenst
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Kamer) {
                Kamer k = (Kamer) r;
                if (!k.isBezet() && k.isSchoon() && k.sterren > gewensteSterren) return k;
            }
        }
        // stap 3: geen geschikte kamer gevonden
        return null;
    }

    public void setLogger(ILogger logger) { this.logger = logger; }
    public void toonStatusScherm() { System.out.println("Status van hotel wordt getoond..."); }
    public int getBalieX() { return balieX; }
    public int getBalieY() { return balieY; }

    // als een uitcheckende gast de lobby betreedt, verwijder hem grafisch
    @Override
    public void betreed(Model.persoon.Persoon p) {
        super.betreed(p);
        if (p instanceof Gast) {
            Gast gast = (Gast) p;
            if (gast.uitcheckend) {
                // verwijder van huidig vakje en uit de personenlijst
                if (gast.huidigVakje != null) {
                    gast.huidigVakje.verwijderPersoon(gast);
                    gast.huidigVakje = null;
                }
                gast.wisRoute();
                hotel.personen.remove(gast);
            }
        }
    }
}
