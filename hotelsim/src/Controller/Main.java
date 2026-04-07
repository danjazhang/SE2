/*package Controller;

import View.HotelFrame;

public class Main {

    public static void main(String[] args) {

        // controller maken (start van systeem)
        HotelController controller = new HotelController();

        // view openen met data van controller
        new HotelFrame(controller);
    }
}
*/

/**
 * Main klasse: startpunt van de applicatie
 */

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
}