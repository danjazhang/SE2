package Controller;
import Model.*;
import Model.ruimte.Ruimte;

import java.util.ArrayList;
import java.util.List;

// Verantwoordelijkheid: hotel data beheren en observers notificeren
public class HotelController {

    //huidige hotel
    private Hotel hotel;

    //beheert het laden van layouts
    private LayoutController layoutController;

    private EventController eventController;

    private ILogger logger;

    // lijst van observers (View) die genotificeerd worden bij wijzigingen
    private List<ModelListener> listeners = new ArrayList<>();

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
    }

    // voeg een observer toe aan de lijst
    public void voegListenerToe(ModelListener modelListener) {
        listeners.add(modelListener);
    }

    // stuur een melding naar alle observers dat het model veranderd is
    public void notifyListeners() {
        for (ModelListener modelListener : listeners) modelListener.modelGewijzigd();
    }
}