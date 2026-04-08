package Controller;

import Model.Hotel;
import Model.HotelManager;
import Model.Layout;
import Model.LayoutParser;
import Model.ParseResultaat;
import Model.Ruimte;

// Verantwoordelijkheid: layouts laden en opslaan
public class LayoutController {

    // beheert alle geladen hotels
    private HotelManager hotelManager = new HotelManager();

    // laad een nieuw hotel vanuit een JSON bestand
    public int laadVanBestand(String bestandspad, String bestandsnaam) {
    ParseResultaat resultaat = new LayoutParser().laad(bestandspad);
    if (resultaat == null) return -1; //-1 geeft aan dat er iets fout is gegaan want id begint bij 1

    //maak nieuwe hotel
    Hotel nieuwHotel = new Hotel();
    nieuwHotel.breedte = resultaat.breedte;
    nieuwHotel.hoogte = resultaat.hoogte;
    //maak grid op basis van bovenstaande afmetingen
    nieuwHotel.layout = new Layout(resultaat.breedte, resultaat.hoogte);

    //voeg elke ruimte toe aan het hotel en plaats in grid
    for (Ruimte r : resultaat.ruimtes) {
        nieuwHotel.ruimtes.add(r);
        nieuwHotel.layout.plaatsRuimte(r);
    }

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
