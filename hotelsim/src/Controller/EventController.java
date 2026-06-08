package Controller;

import Model.*;
import Model.events.IEventListener;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventManager;

import java.util.List;
import java.util.ArrayList;

// Verantwoordelijkheid: library events ontvangen en doorsturen naar listeners
public class EventController implements HotelEventListener {

    private HotelEventManager eventManager;
    private HotelController hotelController;
    private SimulatieController simulatieController;
    private ILogger logger;
    private GastRoutingService gastRoutingService;
    private List<Persoon> personen = new ArrayList<>();
    private List<IEventListener> listeners = new ArrayList<>();

    public EventController(HotelEventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setHotelController(HotelController hotelController) {
        this.hotelController = hotelController;
    }

    public void setSimulatieController(SimulatieController simulatieController) {
        this.simulatieController = simulatieController;
    }

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    public void registreer() {
        eventManager.register(this);
    }

    public void registreerListener(IEventListener listener) {
        listeners.add(listener);
    }

    public void registreerHotelListeners(Hotel hotel) {
        listeners.clear();
        gastRoutingService = new GastRoutingService(hotel);
        if (hotel == null) return;
        for (Model.ruimte.Ruimte r : hotel.ruimtes) {
            if (r instanceof IEventListener) registreerListener((IEventListener) r);
            if (r instanceof Model.ruimte.Restaurant) ((Model.ruimte.Restaurant) r).setGastTerugService(gastRoutingService);
            if (r instanceof Model.ruimte.Fitnessruimte) ((Model.ruimte.Fitnessruimte) r).setGastTerugService(gastRoutingService);
            if (r instanceof Model.ruimte.Bioscoop) ((Model.ruimte.Bioscoop) r).setGastTerugService(gastRoutingService);
        }
        for (Persoon p : hotel.personen) {
            if (p instanceof IEventListener) registreerListener((IEventListener) p);
        }
    }

    private void stuurNaarListeners(HotelEvent event) {
        for (IEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void notificeerPersoon(Persoon p, HotelEvent evt) {}

    private void stuurGastNaarRuimte(HotelEvent evt) {
        if (gastRoutingService == null) return;
        switch (evt.getEventType()) {
            case NEED_FOOD:
                Model.ruimte.Restaurant restaurant = gastRoutingService.stuurNaarRestaurant(evt.getGuestId());
                if (restaurant != null) restaurant.registreerGast(evt.getGuestId(), evt.getTime());
                break;
            case GOTO_FITNESS:
                gastRoutingService.stuurNaarFitness(evt.getGuestId());
                break;
            case GOTO_CINEMA:
                gastRoutingService.stuurNaarBioscoop(evt.getGuestId());
                break;
            default: break;
        }
    }

    @Override
    public void notify(HotelEvent evt) {
        if (hotelController == null || hotelController.getHotel() == null) return;

        Hotel hotel = hotelController.getHotel();

        for (Persoon p : hotel.personen) {
            if (p instanceof Schoonmaker) {
                //typecast, zet persoon om naar schoonmaker
                ((Schoonmaker) p).setHuidigeTijd(evt.getTime());
            }
        }

        // tijdens brandalarm geen nieuwe activiteiten sturen naar gasten
        if (!hotel.brandalarmActief) {
            stuurGastNaarRuimte(evt);
        }

        stuurNaarListeners(evt);

        switch (evt.getEventType()) {
            case EVACUATE:
                // activeer het brandalarm via de service
                new BrandalarmService(hotel, logger).activeer(evt.getTime());
                if (logger != null) logger.log("[" + evt.getTime() + "] HOTEL: evacuatie gestart!");
                break;
            case GODZILLA:
                if (logger != null) logger.log("[" + evt.getTime() + "] HOTEL: GODZILLA AANVAL!");
                break;
            case NONE:
                if (simulatieController != null) simulatieController.tik();
                break;
            default: break;
        }
    }
}
