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

    // Onze eigen snelheidsstand voor bewegingen in de simulatie.
    // Dit getal bepaalt niet de library-events zelf, maar wel hoe snel personen bewegen.
    private int snelheid = 1;
    // Teller voor NONE-ticks, zodat we in de langzame stand bewegingen kunnen overslaan.
    private int tikTeller = 0;

    //constructor
    public SimulatieController(HotelEventManager eventManager, EventController eventController, HotelController hotelController) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;
    }

    public void start() {
        // Start de library altijd op dezelfde basisinstelling.
        // Het echte verschil tussen langzaam, normaal en snel regelen we zelf in tik().
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

    //personen bewegen per tik
    public void tik() {
        Hotel hotel = hotelController.getHotel();
        if (hotel == null) return;

        tikTeller++;

        int stappenPerTik = 1;
        if (snelheid <= 0) {
            // Langzaam: laat personen maar om de twee NONE-ticks bewegen.
            if (tikTeller % 2 != 0) {
                hotel.notifyListeners();
                return;
            }
        } else if (snelheid >= 4) {
            // Snel: laat personen binnen een enkele NONE-tick meerdere stappen zetten.
            stappenPerTik = snelheid;
        }

        for (int stap = 0; stap < stappenPerTik; stap++) {
            for (Persoon p : hotel.personen) {
                p.beweeg();
            }
        }
        hotel.notifyListeners();
    }
}
