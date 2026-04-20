package Controller;
import Model.*;
import Model.IEventListener;
import Model.persoon.Persoon;
import Model.ruimte.Ruimte;

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
        layoutController.setLogger(logger);
    }
    public void setEventController(EventController eventController){
        this.eventController = eventController;
    }
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        //stel logger in op lobby
        if (hotel.lobby != null){
            hotel.lobby.setLogger(logger);
        }
        if (eventController != null) {
            //registreer alle iventlistener ruimter
            for (Ruimte r : hotel.ruimtes){
                if ( r instanceof IEventListener) {
                    eventController.registreerListener((IEventListener) r);
                }
            }
            //registreer schoonmakers
            for (Persoon p: hotel.personen) {
                if (p instanceof IEventListener) {
                    eventController.registreerListener((IEventListener) p);
                }
            }
        }
    }
}