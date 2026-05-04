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

// Verantwoordelijkheid: layouts laden en opslaan
public class LayoutController {

    // beheert alle geladen hotels
    private HotelManager hotelManager = new HotelManager();

    private ILogger logger;

    // laad een nieuw hotel vanuit een JSON bestand
    public int laadVanBestand(String bestandspad, String bestandsnaam) {
        //lees het JSON bestand via de parser
        ParseResultaat resultaat = new LayoutParser().laad(bestandspad);
        //als laden mislukt geef -1 terug, want id begint bij 1
        if (resultaat == null) return -1;

    //maak nieuwe hotel
    Hotel nieuwHotel = new Hotel();

    //grid groter maken voor lift trap en lobby
    int gridBreedte = resultaat.breedte +3;
    int gridHoogte = resultaat.hoogte+1;

    nieuwHotel.breedte = gridBreedte;
    nieuwHotel.hoogte = gridHoogte;
    //maak grid op basis van bovenstaande afmetingen
    nieuwHotel.layout = new Layout(gridBreedte, gridHoogte);

    //maak ruimtes aan via ruimtefactory en voeg toe aan hotel en grid

    RuimteFactory factory = new RuimteFactory(logger);

    //loopt door alle jsonobjecten
    for (JSONObject obj : resultaat.ruimteData) {
        //maak de juiste ruimte subklasse aan
        Ruimte r = factory.maakRuimte(obj.getString("AreaType"), obj);
        //waardes zijn allemaal opgeslagen in jsonobject 
        //set de x en y positie 
        r.posX = obj.getInt("_posX") +1; // ruimte voor lift
        r.posY = obj.getInt("_posY");
        //set de breedte en hoogte
        r.breedte = obj.getInt("_breedte");
        r.hoogte = obj.getInt("_hoogte");
        //voeg de ruimte toe aan de lijst van ruimtes in het hotel
        nieuwHotel.ruimtes.add(r);
        //plaats de ruimte op de juiste positie in het grid
        nieuwHotel.layout.plaatsRuimte(r);
    }

    // na de ruimtes loop, voor het opslaan
    // maak lift aan links
    Lift lift = new Lift();
    lift.posX = 1;
    lift.posY= 1;
    lift.breedte = 1;
    lift.hoogte = gridHoogte;
    nieuwHotel.lift = lift;
    nieuwHotel.ruimtes.add(lift);
    nieuwHotel.layout.plaatsRuimte(lift);

    // maak trap aan rechts
    Trap trap = new Trap(3);
    trap.posX = gridBreedte -1;
    trap.posY = 1;
    trap.breedte = 2;
    trap.hoogte = gridHoogte;
    nieuwHotel.trap = trap;
    nieuwHotel.ruimtes.add(trap);
    nieuwHotel.layout.plaatsRuimte(trap);

    // maak lobby aan onderin
    Lobby lobby = new Lobby(2, gridHoogte, gridBreedte -3, 1, gridBreedte/2,gridHoogte, nieuwHotel, logger);
    nieuwHotel.lobby = lobby;
    nieuwHotel.ruimtes.add(lobby);
    nieuwHotel.layout.plaatsRuimte(lobby);

    //sla de layout op in hotelmanager met bestandsnaam als naam
    nieuwHotel.pathfinder = new Pathfinder(nieuwHotel);
    int id = hotelManager.addLayout(bestandsnaam, nieuwHotel.layout);
    //addlayout geeft een volgendeid terug dit wordt opgeslagen in id
    //sla het hele hotel object op in hotelmanager met zelfde id als sleutel
    hotelManager.loadHotel(id, nieuwHotel);
    return id;
    //hotel en layout zijn aan elkaar gekoppeld via id
    }

    // maak handmatig een lege layout aan
    public int maakHandmatigeLayout(String naam, int breedte, int hoogte) {
        //maak nieuw lege hotel
        Hotel nieuwHotel = new Hotel();
        //maak lege grid
        nieuwHotel.layout = new Layout(breedte, hoogte);
        //sla afmetingen op in hotel
        nieuwHotel.breedte = breedte;
        nieuwHotel.hoogte = hoogte;
        //sla layout op met opgegeven naam en krijg id terug
        int id = hotelManager.addLayout(naam, nieuwHotel.layout);
        //sla hotel op met zelfde id
        hotelManager.loadHotel(id, nieuwHotel);
        return id;
    }

    public void setLogger(ILogger logger){
        this.logger = logger;
    }

    // geef een hotel terug op basis van id
    public Hotel getHotel(int id) {
        return hotelManager.getHotel(id);
    }

    // geef de hotelmanager terug
    public HotelManager getHotelManager() {
        return hotelManager;
    }
}
