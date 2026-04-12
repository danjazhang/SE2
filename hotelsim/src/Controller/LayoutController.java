package Controller;

import Model.*;
import org.json.JSONObject;

// Verantwoordelijkheid: layouts laden en opslaan
public class LayoutController {

    // beheert alle geladen hotels
    private HotelManager hotelManager = new HotelManager();

    // laad een nieuw hotel vanuit een JSON bestand
    public int laadVanBestand(String bestandspad, String bestandsnaam) {
        //lees het JSON bestand via de parser
        ParseResultaat resultaat = new LayoutParser().laad(bestandspad);
        //als laden mislukt geef -1 terug, want id begint bij 1
        if (resultaat == null) return -1;

    //maak nieuwe hotel
    Hotel nieuwHotel = new Hotel();
    nieuwHotel.breedte = resultaat.breedte;
    nieuwHotel.hoogte = resultaat.hoogte;
    //maak grid op basis van bovenstaande afmetingen
    nieuwHotel.layout = new Layout(resultaat.breedte, resultaat.hoogte);

    //maak ruimtes aan via ruimtefactory en voeg toe aan hotel en grid

    RuimteFactory factory = new RuimteFactory();

    //loopt door alle jsonobjecten
    for (JSONObject obj : resultaat.ruimteData) {
        //maak de juiste ruimte subklasse aan
        Ruimte r = factory.maakRuimte(obj.getString("AreaType"), obj);
        //waardes zijn allemaal opgeslagen in jsonobject 
        //set de x en y positie 
        r.posX = obj.getInt("_posX");
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
    nieuwHotel.lift = new Lift();

    // maak trap aan rechts
    nieuwHotel.trap = new Trap(2);

    // maak lobby aan onderin
    Lobby lobby = new Lobby(1, nieuwHotel.hoogte + 1, nieuwHotel.breedte, 1, 1, nieuwHotel.hoogte + 1, nieuwHotel, null);
    nieuwHotel.lobby = lobby;
    nieuwHotel.ruimtes.add(lobby);

    //sla de layout op in hotelmanager met bestandsnaam als naam
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

    // geef een hotel terug op basis van id
    public Hotel getHotel(int id) {
        return hotelManager.getHotel(id);
    }

    // geef de hotelmanager terug
    public HotelManager getHotelManager() {
        return hotelManager;
    }
}
