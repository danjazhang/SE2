import Model.*;
import Model.layout.Layout;
import Model.persoon.Gast;
import Model.ruimte.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GastRoutingServiceTest {

    // hulpmethode: maak een minimaal hotel met lift, trap en pathfinder
    static Hotel maakHotel() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(8, 4);
        hotel.breedte = 8;
        hotel.hoogte = 4;
        Lift lift = new Lift();
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 4;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);
        Trap trap = new Trap(2);
        trap.posX = 8; trap.posY = 1; trap.breedte = 1; trap.hoogte = 4;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);
        hotel.pathfinder = new Pathfinder(hotel);
        return hotel;
    }

    // stuurNaarRestaurant: geeft restaurant terug en zet route op gast
    @Test void testStuurNaarRestaurantZetRoute() {
        Hotel hotel = maakHotel();
        Restaurant restaurant = new Restaurant();
        restaurant.posX = 3; restaurant.posY = 1; restaurant.breedte = 1; restaurant.hoogte = 1;
        hotel.ruimtes.add(restaurant);
        hotel.layout.plaatsRuimte(restaurant);
        Gast gast = new Gast(1, 2);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);
        Restaurant r = new GastRoutingService(hotel).stuurNaarRestaurant(1);
        assertNotNull(r);
        assertNotNull(gast.doelVakje);
    }

    // stuurNaarFitness: geeft fitnessruimte terug en zet route op gast
    @Test void testStuurNaarFitnessZetRoute() {
        Hotel hotel = maakHotel();
        Fitnessruimte fitness = new Fitnessruimte();
        fitness.posX = 4; fitness.posY = 1; fitness.breedte = 1; fitness.hoogte = 1;
        hotel.ruimtes.add(fitness);
        hotel.layout.plaatsRuimte(fitness);
        Gast gast = new Gast(1, 2);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);
        Fitnessruimte f = new GastRoutingService(hotel).stuurNaarFitness(1);
        assertNotNull(f);
        assertNotNull(gast.doelVakje);
    }

    // stuurNaarBioscoop: geeft bioscoop terug en zet route op gast
    @Test void testStuurNaarBioscoopZetRoute() {
        Hotel hotel = maakHotel();
        Bioscoop bioscoop = new Bioscoop();
        bioscoop.posX = 5; bioscoop.posY = 1; bioscoop.breedte = 1; bioscoop.hoogte = 1;
        hotel.ruimtes.add(bioscoop);
        hotel.layout.plaatsRuimte(bioscoop);
        Gast gast = new Gast(1, 2);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);
        Bioscoop b = new GastRoutingService(hotel).stuurNaarBioscoop(1);
        assertNotNull(b);
        assertNotNull(gast.doelVakje);
    }

    // stuurNaarRestaurant: geeft null terug als gast niet bestaat
    @Test void testStuurNaarRestaurantOnbekendeGastGeeftNull() {
        Hotel hotel = maakHotel();
        assertNull(new GastRoutingService(hotel).stuurNaarRestaurant(99));
    }

    // stuurNaarFitness: geeft null terug als gast niet bestaat
    @Test void testStuurNaarFitnessOnbekendeGastGeeftNull() {
        Hotel hotel = maakHotel();
        assertNull(new GastRoutingService(hotel).stuurNaarFitness(99));
    }

    // stuurNaarBioscoop: geeft null terug als gast niet bestaat
    @Test void testStuurNaarBioscoopOnbekendeGastGeeftNull() {
        Hotel hotel = maakHotel();
        assertNull(new GastRoutingService(hotel).stuurNaarBioscoop(99));
    }

    // stuurNaarRestaurant: geeft null terug als er geen restaurant in het hotel is
    @Test void testStuurNaarRestaurantZonderRestaurantGeeftNull() {
        Hotel hotel = maakHotel();
        Gast gast = new Gast(1, 2);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);
        assertNull(new GastRoutingService(hotel).stuurNaarRestaurant(1));
    }

    // stuurNaarRestaurant: geeft null terug als gast geen positie heeft
    @Test void testStuurNaarRestaurantGastZonderPositieGeeftNull() {
        Hotel hotel = maakHotel();
        Restaurant restaurant = new Restaurant();
        restaurant.posX = 3; restaurant.posY = 1; restaurant.breedte = 1; restaurant.hoogte = 1;
        hotel.ruimtes.add(restaurant);
        hotel.layout.plaatsRuimte(restaurant);
        Gast gast = new Gast(1, 2);
        hotel.voegPersoonToe(gast);
        assertNull(new GastRoutingService(hotel).stuurNaarRestaurant(1));
    }

    // stuurTerugNaarKamer: zet route naar kamer op gast
    @Test void testStuurTerugNaarKamerZetRoute() {
        Hotel hotel = maakHotel();
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        Gast gast = new Gast(1, 2);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        kamer.koppelGast(gast);
        hotel.voegPersoonToe(gast);
        new GastRoutingService(hotel).stuurTerugNaarKamer(1);
        assertNotNull(gast.doelVakje);
    }

    // stuurTerugNaarKamer: geen crash als gast niet bestaat
    @Test void testStuurTerugNaarKamerOnbekendeGastCrashetNiet() {
        Hotel hotel = maakHotel();
        assertDoesNotThrow(() -> new GastRoutingService(hotel).stuurTerugNaarKamer(99));
    }

    // stuurTerugNaarKamer: geen crash als gast geen kamer heeft
    @Test void testStuurTerugNaarKamerZonderKamerCrashetNiet() {
        Hotel hotel = maakHotel();
        Gast gast = new Gast(1, 2);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(gast);
        assertDoesNotThrow(() -> new GastRoutingService(hotel).stuurTerugNaarKamer(1));
    }

    // stuurTerugNaarKamer: geen crash als pathfinder null is
    @Test void testStuurTerugNaarKamerZonderPathfinderCrashetNiet() {
        Hotel hotel = maakHotel();
        hotel.pathfinder = null;
        Gast gast = new Gast(1, 2);
        hotel.voegPersoonToe(gast);
        assertDoesNotThrow(() -> new GastRoutingService(hotel).stuurTerugNaarKamer(1));
    }
}
