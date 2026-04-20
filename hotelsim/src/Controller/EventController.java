package Controller;

import Model.*;
import Model.IEventListener;
import Model.persoon.Persoon;
import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;
import Model.*;
import java.util.List;
import java.util.ArrayList;

// Verantwoordelijkheid: library events ontvangen en doorsturen naar listeners
// De EventController is de brug tussen de library en de ruimtes
// Elke ruimte registreert zichzelf als IEventListener
// Alleen noodgevallen worden hier apart gelogd
public class EventController implements HotelEventListener {

    // event manager uit de library
    private HotelEventManager eventManager;

    // hotel controller voor toegang tot hotel data
    private HotelController hotelController;

    // logger voor grafische weergave - alleen voor noodgevallen
    private ILogger logger;

    // personen die genotificeerd worden
    private List<Persoon> personen = new ArrayList<>();

    // lijst van alle luisteraars (lobby, bioscoop, restaurant, etc.)
    // elke ruimte registreert zichzelf hier via registreerListener()
    private List<IEventListener> listeners = new ArrayList<>();

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

    // registreer zichzelf als listener bij de library event manager
    public void registreer() {
        eventManager.register(this);
    }

    // voeg een listener toe aan de lijst
    // elke ruimte roept dit aan om zichzelf te registreren
    public void registreerListener(IEventListener listener) {
        listeners.add(listener);
    }

    // stuur het library event door naar alle geregistreerde listeners
    private void stuurNaarListeners(HotelEvent event) {
        for (IEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    // notificeer een persoon over een event
    public void notificeerPersoon(Persoon p, HotelEvent evt) {
        // logica voor later
    }

    // ontvang library events en stuur ze door naar alle listeners
    // noodgevallen worden hier apart gelogd
    @Override
    public void notify(HotelEvent evt) {
        if (hotelController == null || hotelController.getHotel() == null) return;

        // stuur het event door naar alle listeners
        stuurNaarListeners(evt);

        // noodgevallen worden hier apart afgehandeld
        switch (evt.getEventType()) {
            case EVACUATE:
                if (logger != null) logger.log("[" + evt.getTime() + "] HOTEL: evacuatie gestart!");
                break;
            case GODZILLA:
                if (logger != null) logger.log("[" + evt.getTime() + "] HOTEL: GODZILLA AANVAL!");
                break;
            case NONE:
                Hotel hotel = hotelController.getHotel();
                if (hotel != null) {
                    for (Persoon p: hotel.personen){
                        p.beweeg();
                    }
                    hotel.notifyListeners();
                }
                break;
            default: break;
        }
    }
}
