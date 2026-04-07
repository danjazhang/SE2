package Controller;
import Model.*;

// Verantwoordelijkheid: hotel data beheren
public class HotelController {

    //huidige hotel
    private Hotel hotel; 

    //beheert het laden van layouts
    private LayoutController layoutController;

    public HotelController() {
        layoutController = new LayoutController();
        // nieuw hotel maken
        hotel = new Hotel();
    }

    // geef het huidige hotel terug
    public Hotel getHotel() {
        return hotel;
    }

    public LayoutController getLayoutController() {
        return layoutController;
    }

    public boolean heeftLayout() {
        return hotel != null && hotel.layout != null;
    }
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
}