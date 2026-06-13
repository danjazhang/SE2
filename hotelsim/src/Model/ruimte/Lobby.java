package Model.ruimte;

import Model.*;
import Model.events.IEventListener;
import Model.ILogger;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

// Verantwoordelijkheid: check-in en check-out van gasten afhandelen.
// Lobby erft van Ruimte via 'extends Ruimte'.
// Lobby implementeert IEventListener via 'implements IEventListener',
// wat betekent dat Lobby verplicht is om de methode onEvent() te hebben.
public class Lobby extends Ruimte implements IEventListener {

    // De x-positie van de balie op het grid.
    private int balieX;

    // De y-positie van de balie op het grid.
    private int balieY;

    // Referentie naar het hotel zodat we gasten en kamers kunnen opzoeken.
    private Hotel hotel;

    // Logger voor het sturen van berichten naar de GUI.
    private ILogger logger;

    // Service die gasten aanmaakt en opzoekt.
    private PersonenService personenService;

    // Constructor: sla alle meegegeven waarden op in dit object.
    // 'super(posX, posY, breedte, hoogte)' roept de constructor van Ruimte aan met positie en afmetingen.
    public Lobby(int posX, int posY, int breedte, int hoogte, int balieX, int balieY, Hotel hotel, ILogger logger) {
        super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
        this.hotel = hotel;
        this.logger = logger;
        this.personenService = new PersonenService(hotel);
    }

    // '@Override' betekent: deze methode vervangt onEvent() van de interface IEventListener.
    // Wordt aangeroepen door EventController bij elk binnenkomend event.
    // Als het een CHECK_IN event is, roep behandelCheckIn() aan.
    // Als het een CHECK_OUT event is, roep behandelCheckOut() aan.
    @Override
    public void onEvent(HotelEvent event) {
        if (event.getEventType() == HotelEventType.CHECK_IN) {
            behandelCheckIn(event.getGuestId(), event.getTime(), event.getData());
        } else if (event.getEventType() == HotelEventType.CHECK_OUT) {
            behandelCheckOut(event.getGuestId(), event.getTime());
        }
    }

    // Handel een check-in af: zoek een kamer, maak de gast aan en stuur hem erheen.
    private void behandelCheckIn(int gastId, int tijd, int gewensteSterren) {
        // Zoek het startvakje op aan de balie.
        Vakje startVakje = hotel.layout.krijgVakje(balieX, hotel.hoogte);

        // Zoek eerst een geschikte kamer voordat de gast aangemaakt wordt.
        // Als er geen kamer is, heeft het geen zin de gast aan te maken.
        Kamer kamer = vindGeschikteKamer(gewensteSterren);

        // Als kamer leeg is (null), is er geen geschikte kamer beschikbaar: weiger de gast.
        if (kamer == null) {
            if (logger != null) logger.log("[" + tijd + "] Lobby: gast " + gastId + " geweigerd, geen " + gewensteSterren + "★ kamer beschikbaar");
            return;
        }

        // Maak de gast aan via de service en zet hem op het startvakje.
        Gast gast = personenService.maakGast(gastId, gewensteSterren, startVakje);

        // Koppel de gast aan de kamer en bereken de route van de gast naar de kamer.
        kamer.koppelGast(gast);
        hotel.pathfinder.zetRoute(gast, kamer);
        if (logger != null) logger.log("[" + tijd + "] Lobby: gast " + gastId + " (" + gewensteSterren + "★) checkt in kamer " + kamer.getKamernummer() + " (" + kamer.sterren + "★)");
    }

    // Handel een check-out af: ontkoppel de gast van zijn kamer en stuur een schoonmaker.
    private void behandelCheckOut(int gastId, int tijd) {
        // Zoek de gast op via zijn gastId. Als de gast niet gevonden wordt (null), stop dan.
        Gast gast = personenService.vindGast(gastId);
        if (gast == null) return;

        // Sla de kamer op vóór het uitchecken, want na ontkoppelGast() is gast.kamer null.
        Kamer kamer = gast.kamer;

        // Ontkoppel de gast van zijn kamer: zet kamer op bezet=false en schoon=false.
        if (kamer != null) kamer.ontkoppelGast(gast);

        // Zoek een vrije schoonmaker voor de gewone check-out taak.
        Schoonmaker schoonmaker = personenService.vindVrijeSchoonmakerVoorCheckOut();

        // Als er een schoonmaker is én de kamer bestaat, wijs de kamer toe aan de schoonmaker.
        if (schoonmaker != null && kamer != null) {
            schoonmaker.maakKamerSchoon(kamer);
            // Bereken de route van de schoonmaker naar de kamer.
            hotel.pathfinder.zetRoute(schoonmaker, kamer);
        }

        // Markeer de gast als uitcheckend, wis zijn route en stuur hem naar de lobby.
        // Als de gast de lobby betreedt, wordt hij verwijderd via betreed() hieronder.
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

    // Zoek een geschikte kamer op basis van het gewenste aantal sterren.
    // Stap 1: zoek een kamer met exact het gewenste aantal sterren.
    // Stap 2: als die niet bestaat, zoek een kamer met meer sterren.
    // Stap 3: als ook die niet bestaat, geef null terug.
    private Kamer vindGeschikteKamer(int gewensteSterren) {
        // 'r instanceof Kamer' betekent: als de ruimte r een Kamer is.
        // '!k.isBezet()' betekent: niet bezet. '&&' betekent: en. 'k.sterren == gewensteSterren' betekent: sterren is gelijk aan gewensteSterren.
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Kamer) {
                Kamer k = (Kamer) r;
                if (!k.isBezet() && k.isSchoon() && k.sterren == gewensteSterren) return k;
            }
        }
        // 'k.sterren > gewensteSterren' betekent: sterren is groter dan gewensteSterren (upgrade).
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Kamer) {
                Kamer k = (Kamer) r;
                if (!k.isBezet() && k.isSchoon() && k.sterren > gewensteSterren) return k;
            }
        }
        return null;
    }

    // Stel een nieuwe logger in.
    public void setLogger(ILogger logger) { this.logger = logger; }

    public void toonStatusScherm() { System.out.println("Status van hotel wordt getoond..."); }

    public int getBalieX() { return balieX; }
    public int getBalieY() { return balieY; }

    // '@Override' betekent: deze methode vervangt betreed() van de bovenliggende klasse Ruimte.
    // Als een uitcheckende gast de lobby betreedt, verwijder hem dan uit het hotel.
    @Override
    public void betreed(Model.persoon.Persoon p) {
        // Roep eerst de betreed() van Ruimte aan zodat de gast in de aanwezigenlijst staat.
        super.betreed(p);
        // Als p een Gast is, behandel hem dan als Gast.
        if (p instanceof Gast) {
            Gast gast = (Gast) p;
            // Als uitcheckend gelijk is aan true, verwijder de gast dan volledig.
            if (gast.uitcheckend) {
                // Verwijder de gast van zijn huidige vakje en zet huidigVakje op null.
                if (gast.huidigVakje != null) {
                    gast.huidigVakje.verwijderPersoon(gast);
                    gast.huidigVakje = null;
                }
                // Wis de route zodat de gast nergens meer naartoe gaat.
                gast.wisRoute();
                // Verwijder de gast uit de personenlijst van het hotel.
                hotel.personen.remove(gast);
            }
        }
    }
}
