package Controller;

import Model.Hotel;
import Model.HotelManager;

public class LayoutController {

    // beheert alle geladen hotels
    private HotelManager hotelManager = new HotelManager();

    // laad een nieuw hotel vanuit een bestandspad en sla het op
    public int laadHotel(String bestandspad, String bestandsnaam) {
        Hotel nieuwHotel = new Hotel();
        nieuwHotel.laadLayoutBestand(bestandspad);

        int id = hotelManager.addLayout(bestandsnaam, nieuwHotel.layout);
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
