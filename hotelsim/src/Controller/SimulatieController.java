package Controller;

import Model.Hotel;
import Model.persoon.Persoon;
import hotelevents.HotelEventManager;

// Verantwoordelijkheid: simulatie starten, pauzeren, stoppen en simulatiesnelheid beheren
public class SimulatieController {

    // event manager voor start/stop/pauze
    private HotelEventManager eventManager;

    // event controller voor het starten van events
    private EventController eventController;

    // hotel controller voor het beheren van het hotel
    private HotelController hotelController;

    // simulatiesnelheid
    private int snelheid = 1;

    // constructor
    public SimulatieController(HotelEventManager eventManager,
                               EventController eventController,
                               HotelController hotelController) {

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

    // snelheid aanpassen op basis van gebruikerskeuze
    public void pasSnelheidToe(String keuze) {

        switch (keuze) {
            case "Langzaam":
                snelheid = 0;
                break;

            case "Normaal":
                snelheid = 1;
                break;

            case "Snel":
                snelheid = 4;
                break;

            default:
                snelheid = 1;
                break;
        }
    }

    // personen bewegen per tik
    public void tik() {

        Hotel hotel = hotelController.getHotel();

        if (hotel == null) {
            return;
        }

        for (Persoon p : hotel.personen) {

            // snelheid bepaalt hoeveel keer iemand beweegt per tik
            for (int i = 0; i < snelheid; i++) {
                p.beweeg();
            }
        }

        hotelController.notifyListeners();
    }
}