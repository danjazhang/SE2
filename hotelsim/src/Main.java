import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import hotelevents.HotelEventListener;

public class Main {
    public static void main(String[] args) {
        Hotel hotel = new Hotel();

        //voor testen is dit makkelijk, hoef je neit file te kiezen hele tijd
        // pas het pad aan naar waar jouw JSON file staat

        /* hotel.laadLayoutBestand("layout.json");
        //new HotelFrame(hotel);
         test: print alle ruimtes
        for (Ruimte r : hotel.ruimtes) {
            System.out.println(r.getClass().getSimpleName() +
                " op positie (" + r.posX + ", " + r.posY + ")" +
                " dimensie " + r.breedte + "x" + r.hoogte);
        }
         */

        HotelEventManager manager = new HotelEventManager();

        // listeners maken
        Lobby lobby = new Lobby(0, 0, 10, 10, 1, 1);
        Schoonmaker schoonmaker = new Schoonmaker();
        Restaurant restaurant = new Restaurant();
        Fitnesruimte fitness = new Fitnesruimte();
        Bioscoop bioscoop = new Bioscoop();

        manager.register(lobby);
        manager.register(schoonmaker);
        manager.register(restaurant);
        manager.register(fitness);
        manager.register(bioscoop);

        // event triggeren en beginnen
        new HotelFrame(hotel, manager);
    }
}
