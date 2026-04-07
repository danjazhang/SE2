package Controller;

import Model.Hotel;
import hotelevents.HotelEventManager;


// Verantwoordelijkheid: simulatie starten, pauzeren en stoppen
public class SimulatieController {

    
    // event manager voor start/stop/pauze
    private HotelEventManager eventManager;

     // event controller voor het starten van events
    private EventController eventController;

    //constructor
    public SimulatieController(HotelEventManager eventManager, EventController eventController) {
        this.eventManager = eventManager;
        this.eventController = eventController;
    }

    public void start() { eventManager.start(0); }
    public void pauzeer() { eventManager.pauze(); }
    public void stop() { eventManager.stop(); }
}
