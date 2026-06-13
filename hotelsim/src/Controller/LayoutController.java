package Controller;

import Model.*;
import Model.layout.Layout;
import Model.layout.LayoutParser;
import Model.layout.ParseResultaat;
import Model.layout.Vakje;
import Model.persoon.Schoonmaker;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Ruimte;
import Model.ruimte.Trap;
import org.json.JSONObject;

// Verantwoordelijkheid: layouts laden vanuit een JSON-bestand en hotels opbouwen.
// LayoutController bouwt het volledige Hotel-object op: ruimtes, lift, trap, lobby, schoonmakers.
public class LayoutController {

    // HotelManager slaat alle geladen hotels op zodat je ze later op kunt vragen via een id.
    private HotelManager hotelManager = new HotelManager();

    // Logger voor het sturen van berichten naar de GUI.
    private ILogger logger;

    // Laad een nieuw hotel vanuit een JSON-bestand, bouw het op en sla het op.
    // Geeft het toegewezen id terug, of -1 als het laden mislukt is.
    public int laadVanBestand(String bestandspad, String bestandsnaam) {
        // Lees en verwerk het JSON-bestand via de LayoutParser.
        ParseResultaat resultaat = new LayoutParser().laad(bestandspad);
        // Als het resultaat leeg is (null), is het laden mislukt: geef -1 terug.
        if (resultaat == null) return -1;

        // Maak een nieuw leeg Hotel-object aan.
        Hotel nieuwHotel = new Hotel();

        // Vergroot het grid met 3 kolommen (voor lift en trap) en 1 rij (voor de lobby).
        int gridBreedte = resultaat.breedte + 3;
        int gridHoogte = resultaat.hoogte + 1;

        // Sla de afmetingen op in het hotel en maak het grid aan via de Layout-klasse.
        nieuwHotel.breedte = gridBreedte;
        nieuwHotel.hoogte = gridHoogte;
        nieuwHotel.layout = new Layout(gridBreedte, gridHoogte);

        // Zoek de laagste y-positie van een kamer: dat is de onderste kamerlaag.
        // Kamers op de onderste laag krijgen nummers 101, 102, ... daarboven 201, 202, etc.
        int ondersteKamerPosY = 1;
        for (JSONObject obj : resultaat.ruimteData) {
            if (obj.getString("AreaType").equals("Room")) {
                // 'Math.max(...)' geeft de grootste van de twee waarden terug.
                ondersteKamerPosY = Math.max(ondersteKamerPosY, obj.getInt("_posY"));
            }
        }

        // Maak een RuimteFactory aan die de juiste subklasse aanmaakt op basis van het AreaType.
        RuimteFactory factory = new RuimteFactory(logger, ondersteKamerPosY);

        // Loop door alle JSONObject-ruimteomschrijvingen uit het parseresultaat.
        for (JSONObject obj : resultaat.ruimteData) {
            // Maak de juiste ruimtesubklasse aan (Kamer, Restaurant, Bioscoop, Fitness of Ruimte).
            Ruimte r = factory.maakRuimte(obj.getString("AreaType"), obj);
            // Sla de positie op: voeg 1 toe aan x om ruimte te maken voor de lift aan de linkerkant.
            r.posX = obj.getInt("_posX") + 1;
            r.posY = obj.getInt("_posY");
            // Sla de afmetingen op.
            r.breedte = obj.getInt("_breedte");
            r.hoogte = obj.getInt("_hoogte");
            // Voeg de ruimte toe aan de ruimteslijst van het hotel.
            nieuwHotel.ruimtes.add(r);
            // Plaats de ruimte op de juiste vakjes in het grid.
            nieuwHotel.layout.plaatsRuimte(r);
        }

        // Maak de lift aan aan de linkerkant van het grid (x is gelijk aan 1).
        Lift lift = new Lift(nieuwHotel);
        lift.posX = 1;
        lift.posY = 1;
        lift.breedte = 1;
        lift.hoogte = gridHoogte;
        nieuwHotel.lift = lift;
        nieuwHotel.ruimtes.add(lift);
        nieuwHotel.layout.plaatsRuimte(lift);

        // Maak de trap aan aan de rechterkant van het grid (x is gelijk aan gridBreedte - 1).
        Trap trap = new Trap(3);
        trap.posX = gridBreedte - 1;
        trap.posY = 1;
        trap.breedte = 2;
        trap.hoogte = gridHoogte;
        nieuwHotel.trap = trap;
        nieuwHotel.ruimtes.add(trap);
        nieuwHotel.layout.plaatsRuimte(trap);

        // Maak de lobby aan onderin het grid (y is gelijk aan gridHoogte).
        Lobby lobby = new Lobby(2, gridHoogte, gridBreedte - 3, 1, gridBreedte / 2, gridHoogte, nieuwHotel, logger);
        nieuwHotel.lobby = lobby;
        nieuwHotel.ruimtes.add(lobby);
        nieuwHotel.layout.plaatsRuimte(lobby);

        // Maak de pathfinder aan zodat personen routes kunnen berekenen.
        nieuwHotel.pathfinder = new Pathfinder(nieuwHotel);

        // Maak twee schoonmakers aan:
        // De linker schoonmaker is de gewone schoonmaker voor check-outtaken.
        // De rechter schoonmaker is de noodschoonmaker voor CLEANING_EMERGENCY.
        PersonenFactory personenFactory = new PersonenFactory();

        // Bereken het wachtvakje links van het midden in de lobby.
        // 'Math.max(2, gridBreedte / 2 - 1)' zorgt dat x niet kleiner dan 2 wordt.
        Vakje wachtVakjeLinks = nieuwHotel.layout.krijgVakje(Math.max(2, gridBreedte / 2 - 1), gridHoogte);
        Schoonmaker schoonmakerCheckOut = personenFactory.maakSchoonmaker(nieuwHotel.pathfinder, wachtVakjeLinks);
        schoonmakerCheckOut.setWachtVakje(wachtVakjeLinks);
        nieuwHotel.voegPersoonToe(schoonmakerCheckOut);

        // Bereken het wachtvakje rechts van het midden in de lobby.
        // 'Math.min(gridBreedte - 2, gridBreedte / 2 + 1)' zorgt dat x niet te groot wordt.
        Vakje wachtVakjeRechts = nieuwHotel.layout.krijgVakje(Math.min(gridBreedte - 2, gridBreedte / 2 + 1), gridHoogte);
        Schoonmaker schoonmakerNood = personenFactory.maakSchoonmaker(nieuwHotel.pathfinder, wachtVakjeRechts);
        schoonmakerNood.setWachtVakje(wachtVakjeRechts);
        // Markeer deze schoonmaker als noodschoonmaker via setNoodSchoonmaker(true).
        schoonmakerNood.setNoodSchoonmaker(true);
        nieuwHotel.voegPersoonToe(schoonmakerNood);

        // Sla de layout op in de HotelManager met de bestandsnaam als naam, en krijg een id terug.
        int id = hotelManager.addLayout(bestandsnaam, nieuwHotel.layout);
        // Sla het volledige Hotel-object op in de HotelManager met hetzelfde id als sleutel.
        hotelManager.loadHotel(id, nieuwHotel);
        return id;
    }

    // Maak handmatig een lege layout aan zonder JSON-bestand.
    // Geeft het toegewezen id terug.
    public int maakHandmatigeLayout(String naam, int breedte, int hoogte) {
        Hotel nieuwHotel = new Hotel();
        nieuwHotel.layout = new Layout(breedte, hoogte);
        nieuwHotel.breedte = breedte;
        nieuwHotel.hoogte = hoogte;
        int id = hotelManager.addLayout(naam, nieuwHotel.layout);
        hotelManager.loadHotel(id, nieuwHotel);
        return id;
    }

    // Stel een nieuwe logger in.
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    // Geef het Hotel-object terug dat hoort bij het opgegeven id.
    public Hotel getHotel(int id) {
        return hotelManager.getHotel(id);
    }

    // Geef de HotelManager terug.
    public HotelManager getHotelManager() {
        return hotelManager;
    }
}
