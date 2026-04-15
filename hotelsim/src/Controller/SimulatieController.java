package Controller;

import Model.Hotel;
import Model.Persoon;
import hotelevents.HotelEventManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


// Verantwoordelijkheid: simulatie starten, pauzeren en stoppen
public class SimulatieController {

    
    // event manager voor start/stop/pauze
    private HotelEventManager eventManager;

     // event controller voor het starten van events
    private EventController eventController;

    //hotel controller voor het beheren van de hotel
    private HotelController hotelController;

    //dit moet later weg en hte opvragen van hoteleventmanager
    private Timer simulatieTimer;

    //constructor
    public SimulatieController(HotelEventManager eventManager, EventController eventController, HotelController hotelController) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;

        simulatieTimer = new Timer (1000, e-> stap ());
    }

    // 1 simulatiestap: beweeg alle personen
    private void stap(){
        //vraag hotel op hotelcontroller
        Hotel hotel = hotelController.getHotel();
        //stop als hotel niet bestaat
        if (hotel == null) return;
    
        // kopie zodat toevoegen van nieuwe personen geen crash geeft
        List<Persoon> kopie = new ArrayList<>(hotel.personen);
        //loop door alle personen van in het hotel
        for ( Persoon p : hotel.personen) {
            p.beweeg();
        }
        hotel.notifyListeners();
    }

    public void start() {
        eventManager.start(0);
        simulatieTimer.start();
    }
    public void pauzeer() {
        eventManager.pauze();
        simulatieTimer.stop();
    }
    public void stop() {
        eventManager.stop();
        simulatieTimer.stop();
    }
}
