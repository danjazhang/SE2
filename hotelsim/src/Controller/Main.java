package Controller;

import Model.Hotel;
import Model.Lobby;
import Model.Schoonmaker;
import Model.Restaurant;
import Model.Fitnesruimte;
import Model.Bioscoop;
import View.HotelFrame;
import hotelevents.HotelEventManager;


public class Main {
    public static void main(String[] args) {
        Hotel hotel = new Hotel();

        HotelEventManager manager = new HotelEventManager();

        manager.register(hotel);

        Lobby lobby = new Lobby(0, 0, 10, 10, 1, 1);
        Schoonmaker schoonmaker = new Schoonmaker();
        Restaurant restaurant = new Restaurant();
        Fitnesruimte fitness = new Fitnesruimte();
        Bioscoop bioscoop = new Bioscoop();

        //registreer ruimte bij event manager zodat die events kan ontvangen
        manager.register(lobby);
        manager.register(schoonmaker);
        manager.register(restaurant);
        manager.register(fitness);
        manager.register(bioscoop);

        //open venster met hotel en event manager
        new HotelFrame(hotel, manager);
    }
}
