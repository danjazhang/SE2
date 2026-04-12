
package Controller;

import View.HotelView;
import hotelevents.HotelEventManager;
import View.EventLogView;

// Verantwoordelijkheid: applicatie opstarten
public class Main {

    private static SimulatieController simulatieController;
    private static HotelView hotelView;

    public static void main(String[] args) {
        
        //maak eventlogview aan
        EventLogView logView = new EventLogView();

        // maak controllers aan
        HotelController hotelController = new HotelController();
        HotelEventManager eventManager = new HotelEventManager();
        EventController eventController = new EventController(eventManager);
    
        //koppel controllers aan elkaar
        eventController.setLogger(logView);
        eventController.setHotelController(hotelController);
        eventController.registreer();

        //maak simulatiecontroller aan
        simulatieController = new SimulatieController(eventManager, eventController, hotelController);

        //open het venster
       hotelView = new HotelView(hotelController, logView, eventController, simulatieController);
    }
}