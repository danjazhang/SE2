package Controller;
import Model.*;

// Verantwoordelijkheid: hotel data beheren
public class HotelController {

    //huidige hotel
    private Hotel hotel; 

    //beheert het laden van layouts
    private LayoutController layoutController;

    private EventController eventController;

    private ILogger logger;

    public HotelController() {
        //maak layoutcontroller
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

    public void setLogger(ILogger logger){
        this.logger = logger;
    }
    public void setEventController(EventController eventController){
        this.eventController = eventController;
    }
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        if (hotel.lobby != null && eventController != null){
            hotel.lobby.setLogger(logger);
            eventController.registreerListener(hotel.lobby);
        }
    }
}