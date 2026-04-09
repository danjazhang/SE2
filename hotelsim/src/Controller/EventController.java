package Controller;

import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventListener;
import Model.ILogger;
import Model.Persoon;
import java.util.List;
import java.util.ArrayList;

// Verantwoordelijkheid: events ontvangen en loggen
public class EventController implements HotelEventListener {

    // event manager uit de library
    private HotelEventManager eventManager;

    // hotel controller voor toegang tot hotel data
    private HotelController hotelController;

    // logger voor grafische weergave
    private ILogger logger;

    // personen die genotificeerd worden
    private List<Persoon> personen = new ArrayList<>();

    // constructor
    public EventController(HotelEventManager eventManager) {
        this.eventManager = eventManager;
    }

    // stel de hotelcontroller in
    public void setHotelController(HotelController hotelController) {
        this.hotelController = hotelController;
    }

    // stel de logger in
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    // registreer zichzelf als listener
    public void registreer() {
        eventManager.register(this);
    }

    // notificeer een persoon over een event
    public void notificeerPersoon(Persoon p, HotelEvent evt) {
        // logica voor later
    }

    // ontvang events en log ze
    @Override
    public void notify(HotelEvent evt) {
        if (hotelController == null || hotelController.getHotel() == null) return;

        switch (evt.getEventType()) {
            case CHECK_IN:
                if (logger != null) logger.log("[" + evt.getTime() + "] Lobby: gast " + evt.getGuestId() + " checkt in");
                break;
            case CHECK_OUT:
                if (logger != null) logger.log("[" + evt.getTime() + "] Lobby: gast " + evt.getGuestId() + " checkt uit");
                break;
            case GOTO_CINEMA:
                if (logger != null) logger.log("[" + evt.getTime() + "] Bioscoop: gast " + evt.getGuestId() + " komt binnen");
                break;
            case START_CINEMA:
                if (logger != null) logger.log("[" + evt.getTime() + "] Bioscoop: film start");
                break;
            case NEED_FOOD:
                if (logger != null) logger.log("[" + evt.getTime() + "] Restaurant: gast " + evt.getGuestId() + " bestelt eten");
                break;
            case GOTO_FITNESS:
                if (logger != null) logger.log("[" + evt.getTime() + "] Fitness: gast " + evt.getGuestId() + " gaat sporten");
                break;
            case CLEANING_EMERGENCY:
                if (logger != null) logger.log("[" + evt.getTime() + "] Schoonmaker: noodsituatie!");
                break;
            case EVACUATE:
                if (logger != null) logger.log("[" + evt.getTime() + "] HOTEL: evacuatie gestart!");
                break;
            case GODZILLA:
                if (logger != null) logger.log("[" + evt.getTime() + "] HOTEL: GODZILLA AANVAL!");
                break;
            default: break;
        }
    }
}
