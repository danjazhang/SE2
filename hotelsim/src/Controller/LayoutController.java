package Controller;

import Model.*;
import Model.layout.Layout;
import Model.layout.LayoutParser;
import Model.layout.ParseResultaat;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Ruimte;
import Model.ruimte.Trap;
import org.json.JSONObject;

// Verantwoordelijkheid:
// Deze controller is verantwoordelijk voor het laden,
// aanmaken en opslaan van hotel-layouts.
// Hij zet JSON-data om naar een volledig Hotel-object.
public class LayoutController {

    // HotelManager bewaart alle geladen hotels en layouts.
    // Hierdoor kunnen ze later opnieuw opgehaald worden via een ID.
    private HotelManager hotelManager = new HotelManager();

    // Logger voor meldingen en foutopsporing.
    private ILogger logger;

    // Laadt een nieuw hotel vanuit een JSON-bestand.
    // bestandsnaam = naam die gebruikt wordt binnen de applicatie
    // Geeft een uniek ID terug van het geladen hotel.
    // Bij een fout wordt -1 teruggegeven.
    public int laadVanBestand(String bestandspad, String bestandsnaam) {

        // Lees het JSON-bestand in via de LayoutParser.
        ParseResultaat resultaat = new LayoutParser().laad(bestandspad);

        // Als het laden mislukt is, stop dan onmiddellijk.
        // We gebruiken -1 omdat geldige ID's vanaf 1 beginnen.
        if (resultaat == null) return -1;

        // Maak een volledig nieuw hotel-object aan.
        Hotel nieuwHotel = new Hotel();

        // =====================================================
        // BEREKEN AFMETINGEN VAN HET HOTELGRID
        // =====================================================

        // Het originele JSON-bestand bevat enkel de kamers en ruimtes.
        int gridBreedte = resultaat.breedte + 3;

        // We voegen extra rijen toe:
        int gridHoogte = resultaat.hoogte + 3;

        // Bewaar de berekende afmetingen in het hotel.
        nieuwHotel.breedte = gridBreedte;
        nieuwHotel.hoogte = gridHoogte;

        // Maak een nieuwe lege layout met deze afmetingen.
        nieuwHotel.layout = new Layout(gridBreedte, gridHoogte);

        // =====================================================
        // ZOEK DE ONDERSTE KAMER
        // =====================================================

        // Wordt gebruikt voor kamernummering.
        int ondersteKamerPosY = 1;

        // Loop door alle ruimtes uit de JSON.
        for (JSONObject obj : resultaat.ruimteData) {

            // Controleer of deze ruimte een kamer is.
            if (obj.getString("AreaType").equals("Room")) {

                // Zoek de grootste Y-positie.
                // +2 omdat kamers later twee rijen naar beneden verschoven worden.
                ondersteKamerPosY = Math.max(
                        ondersteKamerPosY,
                        obj.getInt("_posY") + 2
                );
            }
        }

        // =====================================================
        // RUIMTES AANMAKEN
        // =====================================================

        // De RuimteFactory maakt de juiste ruimteklasse aan op basis van de AreaType uit de JSON.
        RuimteFactory factory =
                new RuimteFactory(logger, ondersteKamerPosY);

        // Doorloop alle ruimtegegevens uit de JSON.
        for (JSONObject obj : resultaat.ruimteData) {

            // Maak een nieuwe ruimte aan.
            Ruimte r =
                    factory.maakRuimte(
                            obj.getString("AreaType"),
                            obj
                    );

            // Positie aanpassen:
            //
            // +1 omdat links een lift komt.
            r.posX = obj.getInt("_posX") + 1;

            // +2 omdat onderaan plaats moet zijn
            // voor buiten en de lobby.
            r.posY = obj.getInt("_posY") + 2;

            // Breedte uit JSON overnemen.
            r.breedte = obj.getInt("_breedte");

            // Hoogte uit JSON overnemen.
            r.hoogte = obj.getInt("_hoogte");

            // Voeg ruimte toe aan de lijst van ruimtes.
            nieuwHotel.ruimtes.add(r);

            // Plaats ruimte in de layout-grid.
            nieuwHotel.layout.plaatsRuimte(r);
        }

        // =====================================================
        // VASTE VERDIEPINGEN
        // =====================================================

        // y = 1 stelt de buitenwereld voor.
        // Hier moeten gasten naartoe evacueren.
        int buitenPosY = 1;

        // y = 2 is de lobby.
        int lobbyPosY = 2;

        // Vanaf y = 3 beginnen de echte kamers.
        int kamersStartY = 3;

        // =====================================================
        // LIFT AANMAKEN
        // =====================================================

        // Maak een lift aan.
        Lift lift = new Lift(nieuwHotel);
        // Lift staat helemaal links.
        lift.posX = 1;
        // Lift begint vanaf de eerste kamerverdieping.
        lift.posY = kamersStartY;
        // Lift is één vak breed.
        lift.breedte = 1;
        // Lift loopt over alle verdiepingen.
        lift.hoogte = gridHoogte - kamersStartY + 1;
        // Maak wachtrijen voor alle verdiepingen.
        lift.initWachtrijen(gridHoogte);
        // Stel de lobbyverdieping in.
        lift.setLobbyVerdieping(lobbyPosY);
        // Bewaar lift in het hotel.
        nieuwHotel.lift = lift;
        // Voeg lift toe aan de ruimtelijst.
        nieuwHotel.ruimtes.add(lift);
        // Plaats lift in de layout.
        nieuwHotel.layout.plaatsRuimte(lift);

        // =====================================================
        // TRAP AANMAKEN
        // =====================================================

        // Maak een trap aan.
        Trap trap = new Trap(3);
        // Trap staat helemaal rechts.
        trap.posX = gridBreedte - 1;
        // Start op eerste kamerverdieping.
        trap.posY = kamersStartY;
        // Trap is twee vakken breed.
        trap.breedte = 2;
        // Trap loopt over alle verdiepingen.
        trap.hoogte = gridHoogte - kamersStartY + 1;
        // Bewaar trap in hotel.
        nieuwHotel.trap = trap;
        // Voeg trap toe aan lijst.
        nieuwHotel.ruimtes.add(trap);
        // Plaats trap in layout.
        nieuwHotel.layout.plaatsRuimte(trap);

        // =====================================================
        // LOBBY AANMAKEN
        // =====================================================

        Lobby lobby = new Lobby(
                1,
                lobbyPosY,
                gridBreedte - 2,
                1,
                gridBreedte / 2,
                lobbyPosY,
                nieuwHotel,
                logger
        );

        // Bewaar lobby.
        nieuwHotel.lobby = lobby;

        // Voeg lobby toe aan ruimtelijst.
        nieuwHotel.ruimtes.add(lobby);

        // Plaats lobby in layout.
        nieuwHotel.layout.plaatsRuimte(lobby);

        // =====================================================
        // EXTRA HOTELCOMPONENTEN
        // =====================================================

        // Maak een pathfinder.
        // Deze berekent routes door het hotel.
        nieuwHotel.pathfinder = new Pathfinder(nieuwHotel);

        // Maak een brandalarmservice.
        BrandalarmService brandalarmService =
                new BrandalarmService(nieuwHotel, logger);

        // Bewaar de service in het hotel.
        nieuwHotel.brandalarmService = brandalarmService;

        // =====================================================
        // STANDAARD SCHOONMAKERS AANMAKEN
        // =====================================================

        // PersonenFactory maakt standaard personeel aan.
        PersonenFactory personenFactory = new PersonenFactory();

        // Voeg standaard schoonmakers toe aan het hotel.
        personenFactory.maakStandaardSchoonmakers(
                nieuwHotel,
                gridBreedte,
                gridHoogte,
                lobbyPosY
        );

        // =====================================================
        // OPSLAAN IN HOTELMANAGER
        // =====================================================

        // Sla de layout op en ontvang een uniek ID.
        int id =
                hotelManager.addLayout(
                        bestandsnaam,
                        nieuwHotel.layout
                );

        // Sla het volledige hotel op met hetzelfde ID.
        hotelManager.loadHotel(id, nieuwHotel);

        // Geef het ID terug.
        return id;
    }

    // Maak handmatig een volledig lege layout.
    public int maakHandmatigeLayout(String naam, int breedte, int hoogte) {

        // Maak een nieuw hotel.
        Hotel nieuwHotel = new Hotel();

        // Maak een lege layout-grid.
        nieuwHotel.layout = new Layout(breedte, hoogte);

        // Bewaar de afmetingen in het hotel.
        nieuwHotel.breedte = breedte;
        nieuwHotel.hoogte = hoogte;

        // Sla layout op en ontvang een ID.
        int id =
                hotelManager.addLayout(
                        naam,
                        nieuwHotel.layout
                );

        // Sla ook het hotel op met hetzelfde ID.
        hotelManager.loadHotel(id, nieuwHotel);

        // Geef het ID terug.
        return id;
    }

    // Stelt de logger in.
    public void setLogger(ILogger logger) {

        // Bewaar de logger zodat deze later gebruikt kan worden.
        this.logger = logger;
    }

    // Haal een hotel op aan de hand van zijn ID.
    public Hotel getHotel(int id) {

        // Vraag het hotel op uit de HotelManager.
        return hotelManager.getHotel(id);
    }
}