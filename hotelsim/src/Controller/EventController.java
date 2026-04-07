package Controller;

import hotelevents.HotelEventManager;
import hotelevents.HotelEventListener;
import Model.Hotel;
import Model.Ruimte;

/**
 * Controller die verantwoordelijk is voor het beheren van events.
 * Verbindt het model (Hotel + Ruimtes) met de HotelEventManager (library).
 */
public class EventController {

    // Event manager uit de library
    private HotelEventManager manager;

    /**
     * Constructor: krijgt een bestaande event manager mee
     */
    public EventController(HotelEventManager manager) {
        this.manager = manager;
    }

    /**
     * Registreer het hotel zelf als listener
     */
    public void registreerHotel(Hotel hotel) {
        manager.register(hotel);
    }

    /*
     * Registreer alle ruimtes die luisteren naar events
     */
    public void registreerRuimtes(Hotel hotel) {
        for (Ruimte r : hotel.ruimtes) {
            // Alleen ruimtes die HotelEventListener implementeren
            if (r instanceof HotelEventListener) {
                manager.register((HotelEventListener) r);
            }
        }
    }

    /**
     * Start de simulatie (events beginnen te lopen)
     */
    public void startSimulatie() {
        manager.start(20);
    }
}