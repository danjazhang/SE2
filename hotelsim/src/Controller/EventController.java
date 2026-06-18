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

// Verantwoordelijkheid:
// Deze controller ontvangt alle hotel-events van de HotelEventManager
// en stuurt ze door naar de juiste listeners (kamers, personen, services, ...).
public class EventController implements HotelEventListener {

    // Referentie naar de centrale eventmanager
    private HotelEventManager eventManager;

    // Referentie naar de hotelcontroller zodat we het hotel kunnen opvragen
    private HotelController hotelController;

    // Referentie naar de simulatiecontroller voor het uitvoeren van ticks
    private SimulatieController simulatieController;

    // Logger om berichten naar logbestand of console te schrijven
    private ILogger logger;

    // Service die gasten naar restaurant, fitness of bioscoop stuurt
    private GastRoutingService gastRoutingService;

    // Service die schoonmaaktaken beheert
    private SchoonmaakService schoonmaakService;

    // Deze service bestaat pas vanaf het moment dat het GODZILLA-event echt binnenkomt.
    // Daarna bewaart EventController de service zodat SimulatieController ze bij elke tick
    // opnieuw kan gebruiken voor vuuruitbreiding en slachtoffercontrole.
    private GodzillaService godzillaService;

    // Lokale lijst van personen (wordt momenteel niet gebruikt)
    private List<Persoon> personen = new ArrayList<>();

    // Alle objecten die events willen ontvangen worden hierin opgeslagen
    private List<IEventListener> listeners = new ArrayList<>();

    // Constructor
    // Ontvangt de eventmanager zodat deze controller zich later kan registreren
    public EventController(HotelEventManager eventManager) {
        this.eventManager = eventManager;
    }

    // Koppelt een HotelController aan deze controller
    public void setHotelController(HotelController hotelController) {
        this.hotelController = hotelController;
    }

    // Koppelt een SimulatieController aan deze controller
    public void setSimulatieController(SimulatieController simulatieController) {
        this.simulatieController = simulatieController;
    }

    // Stelt de logger in
    public void setLogger(ILogger logger) {
        this.logger = logger;

        // Als de schoonmaakservice al bestaat,
        // krijgt die ook dezelfde logger mee
        if (schoonmaakService != null)
            schoonmaakService.setLogger(logger);
    }

    // Registreert deze EventController bij de EventManager
    // zodat notify() wordt opgeroepen wanneer een event plaatsvindt
    public void registreer() {
        eventManager.register(this);
    }

    // Voegt een nieuwe listener toe aan de lijst
    public void registreerListener(IEventListener listener) {
        listeners.add(listener);
    }

    // Registreert alle ruimtes, personen en services die events moeten ontvangen
    public void registreerHotelListeners(Hotel hotel) {

        // Verwijder eventuele oude listeners
        listeners.clear();

        // Oude schoonmaakservice verwijderen
        schoonmaakService = null;

        // Nieuwe routingservice maken voor dit hotel
        gastRoutingService = new GastRoutingService(hotel);

        // Veiligheidscontrole
        if (hotel == null) return;

        // Loop door alle ruimtes van het hotel
        for (Model.ruimte.Ruimte r : hotel.ruimtes) {

            // Als een ruimte events kan ontvangen, registreren we die
            if (r instanceof IEventListener)
                registreerListener((IEventListener) r);

            // Restaurant krijgt routingservice zodat gasten kunnen terugkeren
            if (r instanceof Model.ruimte.Restaurant)
                ((Model.ruimte.Restaurant) r).setGastTerugService(gastRoutingService);

            // Fitnessruimte krijgt routingservice
            if (r instanceof Model.ruimte.Fitnessruimte)
                ((Model.ruimte.Fitnessruimte) r).setGastTerugService(gastRoutingService);

            // Bioscoop krijgt routingservice
            if (r instanceof Model.ruimte.Bioscoop)
                ((Model.ruimte.Bioscoop) r).setGastTerugService(gastRoutingService);
        }

        // Loop door alle personen van het hotel
        for (Persoon p : hotel.personen) {

            // Personen die events kunnen ontvangen registreren
            if (p instanceof IEventListener)
                registreerListener((IEventListener) p);
        }

        // Maak een nieuwe schoonmaakservice
        schoonmaakService = new SchoonmaakService(hotel, logger);

        // Ook de schoonmaakservice moet events ontvangen
        registreerListener(schoonmaakService);
    }

    // Stuurt een event door naar alle geregistreerde listeners
    private void stuurNaarListeners(HotelEvent event) {

        // Doorloop alle listeners
        for (IEventListener listener : listeners) {

            // Geef het event door
            listener.onEvent(event);
        }
    }

    // Voorlopig lege methode
    // Zou gebruikt kunnen worden om één specifieke persoon te informeren
    public void notificeerPersoon(Persoon p, HotelEvent evt) {}

    // Geeft de actieve GodzillaService terug.
    // Zolang het GODZILLA-event nog niet ontvangen is, blijft dit null.
    public GodzillaService getGodzillaService() {
        return godzillaService;
    }

    // Stuurt gasten naar de juiste ruimte afhankelijk van het event
    private void stuurGastNaarRuimte(HotelEvent evt) {

        // Zonder routingservice kunnen we niets doen
        if (gastRoutingService == null) return;

        // Kijk welk type event binnenkomt
        switch (evt.getEventType()) {

            case NEED_FOOD:

                // Gast naar restaurant sturen
                Model.ruimte.Restaurant restaurant =
                        gastRoutingService.stuurNaarRestaurant(evt.getGuestId());

                // Als een restaurant gevonden werd
                if (restaurant != null)

                    // Gast registreren in het restaurant
                    restaurant.registreerGast(
                            evt.getGuestId(),
                            evt.getTime()
                    );

                break;

            case GOTO_FITNESS:

                // Gast naar fitness sturen
                gastRoutingService.stuurNaarFitness(evt.getGuestId());
                break;

            case GOTO_CINEMA:

                // Gast naar bioscoop sturen
                gastRoutingService.stuurNaarBioscoop(evt.getGuestId());
                break;

            default:
                // Voor andere events niets doen
                break;
        }
    }

    // Deze methode wordt automatisch aangeroepen
    // wanneer de EventManager een event verstuurt
    @Override
    public void notify(HotelEvent evt) {

        // Controle of er een hotel beschikbaar is
        if (hotelController == null || hotelController.getHotel() == null)
            return;

        // Hotel ophalen
        Hotel hotel = hotelController.getHotel();

        // Loop door alle personen
        for (Persoon p : hotel.personen) {

            // Controleer of de persoon een schoonmaker is
            if (p instanceof Schoonmaker) {

                // Typecast:
                // Persoon -> Schoonmaker
                // zodat we methodes van Schoonmaker kunnen gebruiken
                ((Schoonmaker) p).setHuidigeTijd(evt.getTime());
            }
        }

        // Tijdens brandalarm en Godzilla worden geen nieuwe activiteiten gestart.
        // Anders zouden gasten nog naar restaurant, fitness of bioscoop vertrekken
        // terwijl er een noodsituatie bezig is.
        if (!hotel.brandalarmActief && !hotel.godzillaActief) {
            stuurGastNaarRuimte(evt);
        }

        // Stuur het event naar alle geregistreerde listeners
        stuurNaarListeners(evt);

        // Specifieke acties afhankelijk van het eventtype
        switch (evt.getEventType()) {

            case EVACUATE:

                // Activeer brandalarm via service
                new BrandalarmService(hotel, logger)
                        .activeer(evt.getTime());

                // Schrijf melding naar log
                if (logger != null)
                    logger.log(
                            "[" + evt.getTime() + "] HOTEL: evacuatie gestart!"
                    );

                break;

            case GODZILLA:

                // Maak de GodzillaService aan
                // precies op het moment dat het event binnenkomt
                godzillaService = new GodzillaService(hotel, logger);

                // Start de Godzilla-ramp
                godzillaService.start(evt.getTime());

                break;

            case NONE:

                // Normale simulatietick uitvoeren
                if (simulatieController != null)
                    simulatieController.tik();

                break;

            default:
                // Geen speciale actie nodig
                break;
        }

        // Controleer of de schoonmaakservice bestaat
        if (schoonmaakService != null) {

            // Laat alle wachtende schoonmaaktaken verwerken
            schoonmaakService.verwerkWachtendeTaken(evt.getTime());
        }
    }
}