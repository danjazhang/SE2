package Controller;

import Model.*;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;

import java.util.ArrayList;
import java.util.List;

// Verantwoordelijkheid:
// Deze controller beheert alle gegevens van het hotel.
// Daarnaast zorgt hij ervoor dat Views (observers/listeners)
// op de hoogte worden gebracht wanneer het model verandert.
public class HotelController {

    // Referentie naar het huidige hotel.
    // Alle hotelgegevens (kamers, personen, lobby, layout, ...)
    // zitten in dit object.
    private Hotel hotel;

    // Controller die verantwoordelijk is voor het laden
    // en beheren van hotel-layouts.
    private LayoutController layoutController;

    // Referentie naar de EventController.
    // Deze wordt gebruikt om listeners opnieuw te registreren
    // wanneer een nieuw hotel geladen wordt.
    private EventController eventController;

    // Logger voor het wegschrijven van meldingen naar logbestand of console.
    private ILogger logger;

    // Lijst van observers/listeners.
    // Dit zijn meestal Views die automatisch geüpdatet moeten worden
    // wanneer er iets verandert in het model.
    private List<ModelListener> listeners = new ArrayList<>();

    // Constructor.
    // Wordt uitgevoerd wanneer een HotelController wordt aangemaakt.
    public HotelController() {

        // Maak een nieuwe LayoutController aan.
        layoutController = new LayoutController();

        // Maak een leeg hotel aan zodat er altijd een hotel-object bestaat.
        hotel = new Hotel();
    }

    // Geeft het huidige hotel terug.
    // Andere klassen kunnen hiermee toegang krijgen tot de hotelgegevens.
    public Hotel getHotel() {
        return hotel;
    }

    // Geeft de LayoutController terug.
    public LayoutController getLayoutController() {
        return layoutController;
    }

    // Controleert of het hotel een layout heeft.
    public boolean heeftLayout() {
        return hotel != null && hotel.layout != null;
    }

    // Stelt de logger in voor deze controller.
    public void setLogger(ILogger logger){

        // Bewaar de logger lokaal.
        this.logger = logger;

        // Geef dezelfde logger ook door aan de LayoutController.
        layoutController.setLogger(logger);
    }

    // Koppelt een EventController aan deze controller.
    public void setEventController(EventController eventController){

        // Bewaar de referentie zodat we later methodes kunnen oproepen.
        this.eventController = eventController;
    }

    // Vervangt het huidige hotel door een nieuw hotel.
    public void setHotel(Hotel hotel) {

        // Bewaar het nieuwe hotel.
        this.hotel = hotel;

        // Controleer of er een lobby bestaat.
        if (hotel.lobby != null){

            // Geef de logger door aan de lobby.
            // Hierdoor kan de lobby zelf berichten loggen.
            hotel.lobby.setLogger(logger);
        }

        // Loop door alle personen van het hotel.
        for (Persoon p : hotel.personen) {

            // Controleer of deze persoon een schoonmaker is.
            if (p instanceof Schoonmaker) {

                // Typecast: zet Persoon om naar Schoonmaker
                // zodat we Schoonmaker-methodes kunnen gebruiken.
                ((Schoonmaker) p).setLogger(logger);
            }
        }

        // Als een EventController gekoppeld is,
        // moeten alle listeners opnieuw geregistreerd worden
        // voor het nieuwe hotel.
        if (eventController != null) {

            eventController.registreerHotelListeners(hotel);
        }
    }

    // Voegt een nieuwe observer/listener toe.
    // Een listener is meestal een View die wil weten
    // wanneer het model verandert.
    public void voegListenerToe(ModelListener modelListener) {

        // Voeg de listener toe aan de lijst.
        listeners.add(modelListener);
    }

    // Stuurt een melding naar alle geregistreerde listeners.
    // Dit wordt gebruikt in het Observer Pattern:
    // wanneer het model verandert, worden alle Views automatisch geüpdatet.
    public void notifyListeners() {

        // Doorloop alle listeners.
        for (ModelListener modelListener : listeners)

            // Roep de methode modelGewijzigd() op.
            // Elke listener beslist zelf wat er daarna moet gebeuren.
            modelListener.modelGewijzigd();
    }
}