package Controller;

import Model.Hotel;
import Model.persoon.Persoon;
import hotelevents.HotelEventManager;



// Verantwoordelijkheid: simulatie starten, pauzeren en stoppen
public class SimulatieController {

    
    // event manager voor start/stop/pauze
    private HotelEventManager eventManager;

     // event controller voor het starten van events
    private EventController eventController;

    //hotel controller voor het beheren van de hotel
    private HotelController hotelController;

    //constructor
    public SimulatieController(HotelEventManager eventManager, EventController eventController, HotelController hotelController) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;
    }

    public void start() {
        eventManager.start(0);
    }
    public void pauzeer() {
        eventManager.pauze();
    }
    public void stop() {
        eventManager.stop();
    }

    //personen bewegen per tik
    public void tik() {
        Hotel hotel = hotelController.getHotel();
        if (hotel == null) return;
        for (Persoon p : hotel.personen) {
            p.beweeg();
        }
        hotel.notifyListeners();
    }
}
