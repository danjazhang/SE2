package Model.ruimte;

import Model.*;
import Model.events.IEventListener;
import Model.ILogger;
import Model.layout.Vakje;
import Model.persoon.Gast;
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
        // zet gast op de balie in de lobby als startpunt
        Vakje startVakje = hotel.layout.krijgVakje(balieX, balieY);

        // zoek eerst een geschikte kamer voordat de gast aangemaakt wordt
        Kamer kamer = vindGeschikteKamer(gewensteSterren);

        if (kamer == null) {
            // geen geschikte kamer: gast wordt geweigerd, niet aangemaakt
            if (logger != null) logger.log("[" + tijd + "] Lobby: gast " + gastId + " geweigerd, geen " + gewensteSterren + "★ kamer beschikbaar");
            return;
        }

        // maak gast aan met zijn gewenste sterrenklasse
        Gast gast = personenService.maakGast(gastId, gewensteSterren, startVakje);

        // koppel de gast aan de kamer — kamer wordt altijd onthouden
        kamer.koppelGast(gast);

        if (hotel.brandalarmActief) {
            // tijdens evacuatie: kamer is onthouden maar gast gaat direct naar buiten
            // Hotel.voegPersoonToe zorgt al voor de evacuatie via brandalarmService
            if (logger != null) logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt in kamer " + kamer.getKamernummer() + " maar evacuatie is actief — gaat naar buiten");
        } else {
            // normaal: stuur gast naar zijn kamer
            hotel.pathfinder.zetRoute(gast, kamer);
            if (logger != null) logger.log("[" + tijd + "] Lobby: gast " + gastId + " (" + gewensteSterren + "★) checkt in kamer " + kamer.getKamernummer() + " (" + kamer.sterren + "★)");
        }
    }

    private void behandelCheckOut(int gastId, int tijd) {
        // zoek de gast op basis van id
        Gast gast = personenService.vindGast(gastId);
        if (gast == null) return;
        // sla kamer op want na uitchecken is kamer null
        Kamer kamer = gast.kamer;
        if (kamer != null) kamer.ontkoppelGast(gast);
        if (kamer != null) {
            hotel.voegWachtendeSchoonmaakToe(kamer);
        }
        // markeer gast als uitcheckend, wis oude route en stuur naar het midden van de lobby
        // zodra de gast de lobby betreedt wordt hij grafisch verwijderd via betreed()
        gast.uitcheckend = true;
        gast.wisRoute();
        // stuur naar balievakje (midden lobby) zodat hij visueel door het midden vertrekt
        Vakje balieVakje = hotel.layout.krijgVakje(balieX, balieY);
        if (balieVakje != null) {
            hotel.pathfinder.zetRouteTrap(gast, balieVakje);
        } else {
            hotel.pathfinder.zetRoute(gast, this);
        }
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
                if (!k.isBezet() && k.isSchoon() && k.sterren == gewensteSterren+1) return k;
            }
        }
        // stap 3: geen geschikte kamer gevonden
        return null;
    }

    public void setLogger(ILogger logger) { this.logger = logger; }

    // als een uitcheckende gast de lobby betreedt, verwijder hem alleen als hij het balievakje bereikt
    @Override
    public void betreed(Model.persoon.Persoon p) {
        // standaard gedrag van de superclass uitvoeren (bv. persoon toevoegen aan vakje)
        super.betreed(p);
        // alleen gasten hebben uitcheck-logica
        if (p instanceof Gast) {

            Gast gast = (Gast) p;
            // check: alleen verwijderen als de gast aan het uitchecken is
            // én precies op het balievakje staat (niet ergens anders in de lobby)
            if (gast.uitcheckend && gast.huidigVakje != null
                    && gast.huidigVakje.x == balieX
                    && gast.huidigVakje.y == balieY) {

                // gast eerst uit het vakje verwijderen (visueel en logisch uit grid halen)
                gast.huidigVakje.verwijderPersoon(gast);
                // zet huidige positie op null omdat hij uit het hotel verdwijnt
                gast.huidigVakje = null;
                gast.wisRoute();
                // ook verwijderen uit de lift-systemen (wachtrijen + eventuele lift status)
                if (hotel.lift != null) hotel.lift.verwijderUitWachtrij(gast);
                // gast volledig verwijderen uit de lijst van actieve personen in het hotel
                hotel.personen.remove(gast);
            }
        }
    }
}
