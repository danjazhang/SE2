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

    // hotel controller voor het beheren van de hotel
    private HotelController hotelController;

    // snelheidsstand voor bewegingen: bepaalt hoe snel personen bewegen
    private int snelheid = 1;

    // teller voor tiks, zodat we in de langzame stand bewegingen kunnen overslaan
    private int tikTeller = 0;

    // constructor
    public SimulatieController(HotelEventManager eventManager, EventController eventController, HotelController hotelController) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;
    }

    public void start() {
        // start de library op basisinstelling, snelheid van personen regelen we zelf in tik()
        eventManager.start(1);
    }

    public void pauzeer() {
        eventManager.pauze();
    }

    public void stop() {
        eventManager.stop();
    }

    public void setSnelheid(int snelheid) {
        this.snelheid = snelheid;
    }

    // personen bewegen per tik
    public void tik() {
        Hotel hotel = hotelController.getHotel();
        if (hotel == null) return;

        tikTeller++;

        int stappenPerTik = 1;
        if (snelheid <= 0) {
            // langzaam: personen bewegen maar om de twee tiks
            if (tikTeller % 2 != 0) {
                hotelController.notifyListeners();
                return;
            }
        } else if (snelheid >= 4) {
            // snel: personen zetten meerdere stappen per tik
            stappenPerTik = snelheid;
        }

        for (int stap = 0; stap < stappenPerTik; stap++) {
            for (Persoon p : hotel.personen) {
                p.beweeg();
            }
        }
        hotelController.notifyListeners();
    }
}
