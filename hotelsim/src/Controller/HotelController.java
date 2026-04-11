/*package Controller;
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
      /*  return this.hotel;
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
} */
package Controller;


// import voor model klassen
import Model.*;
import hotelevents.HotelEventListener;

// import voor event systeem (library)
import hotelevents.HotelEventManager;

/**
 * Controller klasse (verbindt View en Model)
 */
public class HotelController {

    // het huidige hotel (model)
    private Hotel hotel;

    // Onthoudt het id van de laatst geladen layout,
    // zodat de view dit correct kan koppelen in de dropdown.
    private int laatsteHotelId = -1;

    // event manager (BELANGRIJK: slechts één instance gebruiken)
    private HotelEventManager manager;

    // controller die layouts beheert (JSON laden)
    private LayoutController layoutController;

    /**
     * Constructor: initialiseert alles
     */
    public HotelController(HotelEventManager manager) {

        // gebruik dezelfde manager die vanuit Main komt
        this.manager = manager;

        // layout controller maken
        layoutController = new LayoutController();

        // nieuw hotel maken (leeg)
        hotel = new Hotel();

        // voorbeeld: hotel zelf registreren als listener
        // zodat het events kan ontvangen
        manager.register(hotel);

    }

    /**
     * Importeer hotel vanuit JSON bestand
     */
    public Hotel importHotel(String path, String name) {

        // Laad een nieuwe layout in en onthoud welk id erbij hoort.
        int id = layoutController.laadHotel(path, name);
        this.laatsteHotelId = id;

        // haal het geladen hotel op
        this.hotel = layoutController.getHotel(id);

        // BELANGRIJK:
        // registreer alle ruimtes die events kunnen ontvangen
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof hotelevents.HotelEventListener) {
                manager.register((hotelevents.HotelEventListener) r);
            }
        }

            if (hotel.lobby instanceof HotelEventListener) {
                manager.register((HotelEventListener) hotel.lobby); }
       // manager.start();

        // geef het nieuwe hotel terug aan de view
        return this.hotel;
    }

    /**
     * Haal hotel op basis van index (voor dropdown)
     */
    public Hotel getHotelByIndex(int index) {
        return layoutController.getHotel(index);
    }

    public Hotel getHotelById(int id) {
        return layoutController.getHotel(id);
    }

    // Geeft het id van de meest recent geïmporteerde layout terug.
    public int getLaatsteHotelId() {
        return laatsteHotelId;
    }

    /**
     * Geef huidig hotel terug
     */
    public Hotel getHotel() {
        return hotel;
    }

    /**
     * Geef event manager terug (voor GUI / Frame)
     */
    public HotelEventManager getManager() {
        return manager;
    }

    /**
     * Laad layout direct (zonder LayoutController)
     */
    public void loadHotel(String path) {
        hotel.laadLayoutBestand(path);
    }
}
