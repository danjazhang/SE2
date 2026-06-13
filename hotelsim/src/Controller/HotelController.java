package Controller;

import Model.*;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.Ruimte;

import java.util.ArrayList;
import java.util.List;

// Verantwoordelijkheid: het hotel-object beheren en alle observers (Views) notificeren als het model verandert.
// HotelController is de brug tussen het Model en de View via het observer-patroon.
public class HotelController {

    // Het huidige hotel-object met alle data.
    private Hotel hotel;

    // De LayoutController die het laden van layouts afhandelt.
    private LayoutController layoutController;

    // De EventController die events ontvangt vanuit de library.
    private EventController eventController;

    // Logger voor het sturen van berichten naar de GUI.
    private ILogger logger;

    // Service die schoonmaak-noodgevallen afhandelt.
    private SchoonmaakService schoonmaakService;

    // Lijst van observers (Views) die een melding krijgen als het model veranderd is.
    // 'List<ModelListener>' betekent: een lijst van ModelListener-objecten.
    private List<ModelListener> listeners = new ArrayList<>();

    // Constructor: maak een nieuwe LayoutController en een leeg Hotel aan.
    public HotelController() {
        layoutController = new LayoutController();
        hotel = new Hotel();
    }

    // Geef het huidige hotel terug.
    public Hotel getHotel() {
        return hotel;
    }

    // Geef de layoutController terug.
    public LayoutController getLayoutController() {
        return layoutController;
    }

    // Geef terug of het hotel een layout heeft: hotel is niet null én hotel.layout is niet null.
    public boolean heeftLayout() {
        return hotel != null && hotel.layout != null;
    }

    // Stel een nieuwe logger in op dit object, de layoutController en de schoonmaakService.
    public void setLogger(ILogger logger) {
        this.logger = logger;
        layoutController.setLogger(logger);
        if (schoonmaakService != null) schoonmaakService.setLogger(logger);
    }

    // Sla de eventController op.
    public void setEventController(EventController eventController) {
        this.eventController = eventController;
    }

    // Stel een nieuw hotel in: sla het op, maak services aan en registreer alle luisteraars.
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        // Maak een nieuwe schoonmaakService aan voor dit hotel.
        this.schoonmaakService = new SchoonmaakService(hotel, logger);

        // Als de lobby bestaat (niet null), stel dan de logger in op de lobby.
        if (hotel.lobby != null) {
            hotel.lobby.setLogger(logger);
        }

        // Loop door alle personen: als een persoon een Schoonmaker is, stel dan zijn logger in.
        for (Persoon p : hotel.personen) {
            if (p instanceof Schoonmaker) {
                ((Schoonmaker) p).setLogger(logger);
            }
        }

        // Als de eventController bestaat (niet null), registreer dan alle hotelluisteraars
        // en voeg de schoonmaakService ook toe als luisteraar.
        if (eventController != null) {
            eventController.registreerHotelListeners(hotel);
            eventController.registreerListener(schoonmaakService);
        }
    }

    // Voeg een observer toe aan de luisteraarslijst.
    public void voegListenerToe(ModelListener modelListener) {
        listeners.add(modelListener);
    }

    // Notificeer alle observers dat het model veranderd is.
    // Elke observer roept dan modelGewijzigd() aan om zichzelf te hertekenen.
    public void notifyListeners() {
        for (ModelListener modelListener : listeners) modelListener.modelGewijzigd();
    }
}
