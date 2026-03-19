
public class Main {
    public static void main(String[] args) {
        Hotel hotel = new Hotel();


        // pas het pad aan naar waar jouw JSON file staat
        hotel.laadLayoutBestand("layout.json");

        new HotelFrame(hotel);

        // test: print alle ruimtes
        for (Ruimte r : hotel.ruimtes) {
            System.out.println(r.getClass().getSimpleName() +
                " op positie (" + r.posX + ", " + r.posY + ")" +
                " dimensie " + r.breedte + "x" + r.hoogte);
        }

        HotelEventManager manager = new HotelEventManager();

        // listeners maken
        Lobby lobby = new Lobby();
        Schoonmaker schoonmaker = new Schoonmaker();

        // registreren
        manager.register(lobby);
        manager.register(schoonmaker);

        // event maken
        HotelEvent evt = new HotelEvent(1, HotelEventType.CHECK_IN, 101, 0);

        // event triggeren
        manager.triggerEvent(evt);

    }
}
