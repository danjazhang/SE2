package Controller;

import Model.Hotel;
import Model.Lobby;
import Model.Schoonmaker;
import Model.Restaurant;
import Model.Fitnesruimte;
import Model.Bioscoop;
import hotelevents.HotelEventManager;
import View.HotelFrame;

// Controller klasse: startpunt van de applicatie
// Maakt het hotel en alle ruimtes aan, registreert listeners en opent de UI
public class Main {
    public static void main(String[] args) {
        // maak het hotel model aan
        Hotel hotel = new Hotel();

        // maak de event manager aan die events verstuurt naar alle listeners
        HotelEventManager manager = new HotelEventManager();

        // maak alle ruimtes aan die reageren op events
        Lobby lobby = new Lobby(0, 0, 10, 10, 1, 1);
        Schoonmaker schoonmaker = new Schoonmaker();
        Restaurant restaurant = new Restaurant();
        Fitnesruimte fitness = new Fitnesruimte();
        Bioscoop bioscoop = new Bioscoop();

        // registreer alle ruimtes als listeners bij de event manager
        manager.register(lobby);
        manager.register(schoonmaker);
        manager.register(restaurant);
        manager.register(fitness);
        manager.register(bioscoop);

        // open de grafische interface (View)
        new HotelFrame(hotel, manager);
    }
}
