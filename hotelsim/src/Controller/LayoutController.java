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
    if (resultaat == null) return -1;

    Hotel nieuwHotel = new Hotel();
    nieuwHotel.breedte = resultaat.breedte;
    nieuwHotel.hoogte = resultaat.hoogte;
    nieuwHotel.layout = new Layout(resultaat.breedte, resultaat.hoogte);

    for (Ruimte r : resultaat.ruimtes) {
        nieuwHotel.ruimtes.add(r);
        nieuwHotel.layout.plaatsRuimte(r);
    }

    int id = hotelManager.addLayout(bestandsnaam, nieuwHotel.layout);
    hotelManager.loadHotel(id, nieuwHotel);
    return id;
    }

    // maak handmatig een lege layout aan
    public int maakHandmatigeLayout(String naam, int breedte, int hoogte) {
        Hotel nieuwHotel = new Hotel();
        nieuwHotel.layout = new Layout(breedte, hoogte);
        nieuwHotel.breedte = breedte;
        nieuwHotel.hoogte = hoogte;
        int id = hotelManager.addLayout(naam, nieuwHotel.layout);
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
