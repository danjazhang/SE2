package Controller;
import hotelevents.HotelEventListener;


import Model.*;
import hotelevents.HotelEventManager;

public class HotelController {

    private Hotel hotel; // dit is ons hotel (model)
    private HotelEventManager manager; // dit regelt events
 private LayoutController layoutController;

    public HotelController() {
 layoutController = new LayoutController();
        // nieuw hotel maken
        hotel = new Hotel();

        // event systeem maken
        manager = new HotelEventManager();

        // voorbeeld: lobby maken
        Lobby lobby = new Lobby(0, 0, 10, 10, 1, 1);
        // objects registreren bij event manager
        manager.register(lobby);
        manager.register(hotel);

    }
   public Hotel importHotel(String path, String name) {
        int id = layoutController.laadHotel(path, name);
        this.hotel = layoutController.getHotel(id);




     /* for (Ruimte r : hotel.ruimtes) {
           if (r instanceof HotelEventListener) {
               manager.register((HotelEventListener) r);
           }
       } */
        return this.hotel;
   }

    public Hotel getHotelByIndex(int index) {
        return layoutController.getHotel(index);
    }

    // view vraagt hotel data via controller
    public Hotel getHotel() {
        return hotel;
    }

    // view krijgt ook event manager
    public HotelEventManager getManager() {
        return manager;
    }

    // later: hotel laden van bestand (json)
    public void loadHotel(String path) {
        hotel.laadLayoutBestand(path);
    }
}