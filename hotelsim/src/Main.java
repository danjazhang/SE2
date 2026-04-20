package Controller;

import Controller.EventController;
import Controller.HotelController;
import Controller.SimulatieController;
import View.HotelView;
import View.EventLogView;
import Model.*;
import hotelevents.HotelEventManager;

// Verantwoordelijkheid: applicatie opstarten en alle listeners registreren
public class Main {

    private static SimulatieController simulatieController;
    private static HotelView hotelView;

    public static void main(String[] args) {

        // maak eventlogview aan voor logging naar de GUI
        EventLogView logView = new EventLogView();

        // maak controllers aan
        HotelController hotelController = new HotelController();
        HotelEventManager eventManager = new HotelEventManager();
        EventController eventController = new EventController(eventManager);

        // koppel controllers aan elkaar
        eventController.setLogger(logView);
        eventController.setHotelController(hotelController);
        eventController.registreer();
        hotelController.setEventController(eventController);
        hotelController.setLogger(logView);

        //maak simulatiecontroller aan
        simulatieController = new SimulatieController(eventManager, eventController, hotelController);

        // open het venster
        hotelView = new HotelView(hotelController, logView, eventController, simulatieController);
    }
}