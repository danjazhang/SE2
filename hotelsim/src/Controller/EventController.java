package Controller;

import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;
import Model.*;
import java.util.List;
import java.util.ArrayList;

// Verantwoordelijkheid: library events ontvangen en doorsturen naar listeners
// GOTO_CINEMA en START_CINEMA worden niet meer omgezet want die logt de library al
// Alleen noodgevallen worden hier nog apart gelogd
public class EventController implements HotelEventListener {

    // event manager uit de library
    private HotelEventManager eventManager;

    // hotel controller voor toegang tot hotel data
    private HotelController hotelController;

    // logger voor grafische weergave - alleen nog voor noodgevallen
    private ILogger logger;

    // personen die genotificeerd worden
    private List<Persoon> personen = new ArrayList<>();

    // lijst van alle luisteraars (lobby, bioscoop, restaurant, etc.)
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
    public void registreerListener(IEventListener listener) {
        listeners.add(listener);
    }

    // stuur het interne event door naar alle geregistreerde listeners
    private void stuurNaarListeners(InternEvent event) {
        for (IEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    // zet een library event om naar ons eigen intern event object
    // GOTO_CINEMA en START_CINEMA worden niet omgezet want die logt de library al
    // alleen de events die de library NIET heeft worden hier aangemaakt
    private InternEvent maakInternEvent(HotelEvent evt) {
        switch (evt.getEventType()) {
            case CHECK_IN:
                return new CheckInEvent(evt.getTime(), evt.getGuestId());
            case CHECK_OUT:
                return new CheckOutEvent(evt.getTime(), evt.getGuestId());
            case START_CINEMA:
                // film start officieel, bioscoop moet filmEindTijd berekenen
                return new FilmStartEvent(evt.getTime(), evt.getGuestId());
            case NEED_FOOD:
                return new RestaurantStartEvent(evt.getTime(), evt.getGuestId());
            case GOTO_FITNESS:
                return new FitnessStartEvent(evt.getTime(), evt.getGuestId());
            case CLEANING_EMERGENCY:
                return new SchoonmaakEvent(evt.getTime(), evt.getGuestId());
            case NONE:
                // elke tick stuurt de library een NONE event
                // wij zetten dat om naar een TickEvent zodat ruimtes tijd kunnen bijhouden
                return new TickEvent(evt.getTime());
            default:
                return null;
        }
    }

    // notificeer een persoon over een event
    public void notificeerPersoon(Persoon p, HotelEvent evt) {
        // logica voor later
    }

    // ontvang library events, log ze en stuur interne events door
    @Override
    public void notify(HotelEvent evt) {
        if (hotelController == null || hotelController.getHotel() == null) return;

        // de library logt GOTO_CINEMA en START_CINEMA al correct
        // wij hoeven die niet opnieuw te loggen

        // stuur intern event door naar listeners
        InternEvent internEvent = maakInternEvent(evt);
        if (internEvent != null) {
            stuurNaarListeners(internEvent);
        }

        // noodgevallen worden hier apart afgehandeld
        switch (evt.getEventType()) {
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
