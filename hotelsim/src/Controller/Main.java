/*


 // Main klasse: startpunt van de applicatie


package Controller;

import View.HotelFrame;
import Model.Hotel;
import hotelevents.HotelEventManager;

public class Main {

    public static void main(String[] args) {

        // controller (eski sistem)
        HotelController hotelController = new HotelController();



        // event system (yeni eklediğin)
        HotelEventManager manager = new HotelEventManager();
        EventController eventController = new EventController(manager);

        // hotel'i al
         Hotel hotel = hotelController.getHotel(); // varsa

        // register
        eventController.registreerHotel(hotel);
        eventController.registreerRuimtes(hotel);
        //GUI opent
       new HotelFrame(hotelController);
        // start
        eventController.startSimulatie();
    }
} */
package Controller;

import View.HotelFrame;
import Model.Hotel;
import hotelevents.HotelEventManager;
import Model.Lobby;

public class Main {

    public static void main(String[] args) {

        //  manager maken DEZE MANAGER sturt alle events naar de juiste ruimtes
        HotelEventManager manager = new HotelEventManager();

        // geef manager aan het control zodat zij events kunnen beheren en doorsturen
        HotelController hotelController = new HotelController(manager);

        // event controller
        EventController eventController = new EventController(manager);

        // hotel toevoegen
        Hotel hotel = hotelController.getHotel();

        // register
       /* eventController.registreerHotel(hotel);
        eventController.registreerRuimtes(hotel);
        eventController.registreerLobbyManueel(); */
 //eventController.registreerLobby(hotel);
        // GUI hotel visueel te tonen
        new HotelFrame(hotelController);

        // simulatie start pas wanneer gebruiker op Start klikt
    }
}
