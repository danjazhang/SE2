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

// Verantwoordelijkheid: events van de externe library ontvangen en doorsturen naar de rest van het systeem.
// EventController is de brug tussen de library en de eigen klassen.
// De library roept notify() aan op deze controller bij elk event.
// EventController implementeert HotelEventListener zodat de library hem events kan sturen.
public class EventController implements HotelEventListener {

    // De library-eventmanager waarmee we ons registreren als luisteraar.
    private HotelEventManager eventManager;

    // Toegang tot het hotel via de HotelController.
    private HotelController hotelController;

    // De SimulatieController die we aanroepen bij elke NONE tick.
    private SimulatieController simulatieController;

    // Logger voor het sturen van berichten naar de GUI.
    private ILogger logger;

    // Service die gasten naar de juiste ruimte stuurt op basis van het eventtype.
    private GastRoutingService gastRoutingService;

    // Lijst van personen (niet meer actief in gebruik).
    private List<Persoon> personen = new ArrayList<>();

    // Lijst van alle geregistreerde luisteraars: lobby, restaurant, bioscoop, fitness, schoonmaakservice.
    // Elke luisteraar ontvangt elk event via onEvent().
    private List<IEventListener> listeners = new ArrayList<>();

    // Constructor: sla de eventmanager op zodat we ons later kunnen registreren via registreer().
    public EventController(HotelEventManager eventManager) {
        this.eventManager = eventManager;
    }

    // Sla de hotelController op zodat we via hem toegang hebben tot het hotel.
    public void setHotelController(HotelController hotelController) {
        this.hotelController = hotelController;
    }

    // Sla de simulatieController op zodat we tik() kunnen aanroepen bij NONE events.
    public void setSimulatieController(SimulatieController simulatieController) {
        this.simulatieController = simulatieController;
    }

    // Sla de logger op voor het sturen van berichten naar de GUI.
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    // Registreer deze EventController als luisteraar bij de library.
    // Na deze aanroep stuurt de library elk event naar onze notify() methode.
    public void registreer() {
        eventManager.register(this);
    }

    // Voeg één luisteraar toe aan de lijst.
    public void registreerListener(IEventListener listener) {
        listeners.add(listener);
    }

    // Registreer alle ruimtes en personen van een hotel als luisteraars.
    // Wordt aangeroepen als een nieuw hotel geladen wordt.
    // Wist eerst de oude lijst zodat er geen dubbele registraties zijn.
    public void registreerHotelListeners(Hotel hotel) {
        // Wis de oude luisteraarslijst van het vorige hotel.
        listeners.clear();

        // Maak een nieuwe GastRoutingService aan voor dit hotel.
        gastRoutingService = new GastRoutingService(hotel);

        // Als het hotel leeg is (null), stop dan.
        if (hotel == null) return;

        // Loop door alle ruimtes: als een ruimte IEventListener implementeert, registreer hem dan.
        // 'r instanceof IEventListener' betekent: als de ruimte de interface IEventListener implementeert.
        for (Model.ruimte.Ruimte r : hotel.ruimtes) {
            if (r instanceof IEventListener) registreerListener((IEventListener) r);
            // Geef ook de gastTerugService door aan ruimtes die gasten moeten terugsturen.
            if (r instanceof Model.ruimte.Restaurant) ((Model.ruimte.Restaurant) r).setGastTerugService(gastRoutingService);
            if (r instanceof Model.ruimte.Fitnessruimte) ((Model.ruimte.Fitnessruimte) r).setGastTerugService(gastRoutingService);
            if (r instanceof Model.ruimte.Bioscoop) ((Model.ruimte.Bioscoop) r).setGastTerugService(gastRoutingService);
        }

        // Loop door alle personen: als een persoon IEventListener implementeert, registreer hem dan.
        for (Persoon p : hotel.personen) {
            if (p instanceof IEventListener) registreerListener((IEventListener) p);
        }
    }

    // Stuur het event door naar alle geregistreerde luisteraars.
    // Elke luisteraar beslist zelf of hij iets doet met het event in zijn onEvent() methode.
    private void stuurNaarListeners(HotelEvent event) {
        for (IEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    // Lege methode als placeholder voor directe persoonnotificaties.
    public void notificeerPersoon(Persoon p, HotelEvent evt) {}

    // Stuur de gast naar de juiste ruimte op basis van het eventtype.
    // Wordt alleen aangeroepen als het brandalarm niet actief is.
    private void stuurGastNaarRuimte(HotelEvent evt) {
        // Als de gastRoutingService leeg is (null), stop dan.
        if (gastRoutingService == null) return;

        switch (evt.getEventType()) {
            case NEED_FOOD:
                // Stuur de gast naar het dichtstbijzijnde restaurant en registreer hem daar met zijn aankomsttijd.
                Model.ruimte.Restaurant restaurant = gastRoutingService.stuurNaarRestaurant(evt.getGuestId());
                if (restaurant != null) restaurant.registreerGast(evt.getGuestId(), evt.getTime());
                break;
            case GOTO_FITNESS:
                // Stuur de gast naar de dichtstbijzijnde fitnessruimte.
                gastRoutingService.stuurNaarFitness(evt.getGuestId());
                break;
            case GOTO_CINEMA:
                // Stuur de gast naar de dichtstbijzijnde bioscoop.
                gastRoutingService.stuurNaarBioscoop(evt.getGuestId());
                break;
            default: break;
        }
    }

    // '@Override' betekent: deze methode vervangt notify() van de interface HotelEventListener.
    // Wordt door de library aangeroepen bij elk event. Dit is het centrale inkomstpunt.
    @Override
    public void notify(HotelEvent evt) {
        // Als hotelController of het hotel leeg is (null), stop dan: er is niks om op te reageren.
        if (hotelController == null || hotelController.getHotel() == null) return;

        Hotel hotel = hotelController.getHotel();

        // Geef de huidige tick door aan alle schoonmakers zodat hun logberichten de juiste tijd tonen.
        for (Persoon p : hotel.personen) {
            if (p instanceof Schoonmaker) {
                ((Schoonmaker) p).setHuidigeTijd(evt.getTime());
            }
        }

        // Als het brandalarm actief is (brandalarmActief is gelijk aan true),
        // stuur dan geen nieuwe activiteiten naar gasten: gasten moeten evacueren.
        if (!hotel.brandalarmActief) {
            stuurGastNaarRuimte(evt);
        }

        // Stuur het event door naar alle geregistreerde luisteraars.
        stuurNaarListeners(evt);

        // Handel speciale events apart af via een switch.
        switch (evt.getEventType()) {
            case EVACUATE:
                // Activeer het brandalarm via de BrandalarmService: iedereen naar buiten via de trap.
                new BrandalarmService(hotel, logger).activeer(evt.getTime());
                if (logger != null) logger.log("[" + evt.getTime() + "] HOTEL: evacuatie gestart!");
                break;
            case GODZILLA:
                // Log de godzilla-aanval.
                if (logger != null) logger.log("[" + evt.getTime() + "] HOTEL: GODZILLA AANVAL!");
                break;
            case NONE:
                // NONE is het tick-signaal van de library: roep tik() aan zodat personen bewegen.
                if (simulatieController != null) simulatieController.tik();
                break;
            default: break;
        }
    }
}
