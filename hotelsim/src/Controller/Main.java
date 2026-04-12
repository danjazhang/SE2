package Controller;

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

        // maak alle ruimtes en personen aan met de logger
        // zo kunnen ze zelf naar de GUI loggen
        Lobby lobby = new Lobby(0, 0, 10, 10, 1, 1, logView);
        Bioscoop bioscoop = new Bioscoop(logView);
        Restaurant restaurant = new Restaurant(logView);
        Fitnessruimte fitnessruimte = new Fitnessruimte(logView);
        Schoonmaker schoonmaker = new Schoonmaker(logView);

        // registreer alle listeners bij de EventController
        // elke ruimte of persoon verwerkt zijn eigen events zelf
        eventController.registreerListener(lobby);
        eventController.registreerListener(bioscoop);
        eventController.registreerListener(restaurant);
        eventController.registreerListener(fitnessruimte);
        eventController.registreerListener(schoonmaker);

        // maak simulatiecontroller aan
        simulatieController = new SimulatieController(eventManager, eventController);

        // open het venster
        hotelView = new HotelView(hotelController, logView, eventController, simulatieController);
    }
}
