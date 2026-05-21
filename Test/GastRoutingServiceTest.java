import Model.GastRoutingService;
import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GastRoutingServiceTest {

    // hulpmethode: maak een minimaal hotel met lift, trap en pathfinder
    // elke test roept dit zelf aan zodat er geen gedeelde staat is
    private Hotel maakHotel() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(8, 4);
        hotel.breedte = 8;
        hotel.hoogte = 4;

        Lift lift = new Lift(hotel);
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

    // hulpmethode: voeg een gast toe aan het hotel op een bepaalde positie
    private Gast voegGastToe(Hotel hotel, int gastId, int x, int y) {
        Gast gast = new Gast(gastId, 1);
        gast.setPathfinder(hotel.pathfinder);
        gast.zetStartPositie(hotel.layout.krijgVakje(x, y));
        hotel.voegPersoonToe(gast);
        return gast;
    }

    // hulpmethode: voeg een restaurant toe aan het hotel op een bepaalde positie
    private Restaurant voegRestaurantToe(Hotel hotel, int x, int y) {
        Restaurant restaurant = new Restaurant();
        restaurant.posX = x; restaurant.posY = y;
        restaurant.breedte = 1; restaurant.hoogte = 1;
        hotel.ruimtes.add(restaurant);
        hotel.layout.plaatsRuimte(restaurant);
        return restaurant;
    }

    // hulpmethode: voeg een fitnessruimte toe aan het hotel op een bepaalde positie
    private Fitnessruimte voegFitnessToe(Hotel hotel, int x, int y) {
        Fitnessruimte fitness = new Fitnessruimte();
        fitness.posX = x; fitness.posY = y;
        fitness.breedte = 1; fitness.hoogte = 1;
        hotel.ruimtes.add(fitness);
        hotel.layout.plaatsRuimte(fitness);
        return fitness;
    }

    // hulpmethode: voeg een bioscoop toe aan het hotel op een bepaalde positie
    private Bioscoop voegBioscoopToe(Hotel hotel, int x, int y) {
        Bioscoop bioscoop = new Bioscoop();
        bioscoop.posX = x; bioscoop.posY = y;
        bioscoop.breedte = 1; bioscoop.hoogte = 1;
        hotel.ruimtes.add(bioscoop);
        hotel.layout.plaatsRuimte(bioscoop);
        return bioscoop;
    }

    // constructor: service wordt aangemaakt zonder crash
    @Test void testConstructor() {
        assertNotNull(new GastRoutingService(maakHotel()));
    }

    // stuurNaarRestaurant: geeft het restaurant terug als gast en restaurant bestaan
    @Test void testStuurNaarRestaurantGeeftRestaurantTerug() {
        Hotel hotel = maakHotel();
        voegGastToe(hotel, 1, 2, 1);
        voegRestaurantToe(hotel, 3, 1);
        Restaurant r = new GastRoutingService(hotel).stuurNaarRestaurant(1);
        assertNotNull(r);
    }

    // stuurNaarRestaurant: gast krijgt een route naar het restaurant
    @Test void testStuurNaarRestaurantZetRoute() {
        Hotel hotel = maakHotel();
        Gast gast = voegGastToe(hotel, 1, 2, 1);
        voegRestaurantToe(hotel, 3, 1);
        new GastRoutingService(hotel).stuurNaarRestaurant(1);
        assertNotNull(gast.doelVakje);
    }

    // stuurNaarRestaurant: geeft null terug als gast niet bestaat
    @Test void testStuurNaarRestaurantGastNietGevonden() {
        Hotel hotel = maakHotel();
        voegRestaurantToe(hotel, 3, 1);
        assertNull(new GastRoutingService(hotel).stuurNaarRestaurant(99));
    }

    // stuurNaarRestaurant: geeft null terug als er geen restaurant in het hotel is
    @Test void testStuurNaarRestaurantGeenRestaurant() {
        Hotel hotel = maakHotel();
        voegGastToe(hotel, 1, 2, 1);
        assertNull(new GastRoutingService(hotel).stuurNaarRestaurant(1));
    }

    // stuurNaarRestaurant: geeft null terug als gast geen huidigVakje heeft
    @Test void testStuurNaarRestaurantGastZonderVakje() {
        Hotel hotel = maakHotel();
        Gast gast = new Gast(1, 1); // geen startpositie
        hotel.voegPersoonToe(gast);
        voegRestaurantToe(hotel, 3, 1);
        assertNull(new GastRoutingService(hotel).stuurNaarRestaurant(1));
    }

    // stuurNaarRestaurant: kiest het dichtstbijzijnde restaurant
    @Test void testStuurNaarRestaurantKiesDichtstbij() {
        Hotel hotel = maakHotel();
        voegGastToe(hotel, 1, 2, 1);
        Restaurant dichtbij = voegRestaurantToe(hotel, 3, 1);
        voegRestaurantToe(hotel, 7, 1); // verder weg
        Restaurant gekozen = new GastRoutingService(hotel).stuurNaarRestaurant(1);
        assertEquals(dichtbij, gekozen);
    }

    // stuurNaarFitness: geeft de fitnessruimte terug als gast en fitness bestaan
    @Test void testStuurNaarFitnessGeeftFitnessTerug() {
        Hotel hotel = maakHotel();
        voegGastToe(hotel, 1, 2, 1);
        voegFitnessToe(hotel, 4, 1);
        Fitnessruimte f = new GastRoutingService(hotel).stuurNaarFitness(1);
        assertNotNull(f);
    }

    // stuurNaarFitness: gast krijgt een route naar de fitnessruimte
    @Test void testStuurNaarFitnessZetRoute() {
        Hotel hotel = maakHotel();
        Gast gast = voegGastToe(hotel, 1, 2, 1);
        voegFitnessToe(hotel, 4, 1);
        new GastRoutingService(hotel).stuurNaarFitness(1);
        assertNotNull(gast.doelVakje);
    }

    // stuurNaarFitness: geeft null terug als gast niet bestaat
    @Test void testStuurNaarFitnessGastNietGevonden() {
        Hotel hotel = maakHotel();
        voegFitnessToe(hotel, 4, 1);
        assertNull(new GastRoutingService(hotel).stuurNaarFitness(99));
    }

    // stuurNaarFitness: geeft null terug als er geen fitnessruimte is
    @Test void testStuurNaarFitnessGeenFitness() {
        Hotel hotel = maakHotel();
        voegGastToe(hotel, 1, 2, 1);
        assertNull(new GastRoutingService(hotel).stuurNaarFitness(1));
    }

    // stuurNaarBioscoop: geeft de bioscoop terug als gast en bioscoop bestaan
    @Test void testStuurNaarBioscoopGeeftBioscoopTerug() {
        Hotel hotel = maakHotel();
        voegGastToe(hotel, 1, 2, 1);
        voegBioscoopToe(hotel, 5, 1);
        Bioscoop b = new GastRoutingService(hotel).stuurNaarBioscoop(1);
        assertNotNull(b);
    }

    // stuurNaarBioscoop: gast krijgt een route naar de bioscoop
    @Test void testStuurNaarBioscoopZetRoute() {
        Hotel hotel = maakHotel();
        Gast gast = voegGastToe(hotel, 1, 2, 1);
        voegBioscoopToe(hotel, 5, 1);
        new GastRoutingService(hotel).stuurNaarBioscoop(1);
        assertNotNull(gast.doelVakje);
    }

    // stuurNaarBioscoop: geeft null terug als gast niet bestaat
    @Test void testStuurNaarBioscoopGastNietGevonden() {
        Hotel hotel = maakHotel();
        voegBioscoopToe(hotel, 5, 1);
        assertNull(new GastRoutingService(hotel).stuurNaarBioscoop(99));
    }

    // stuurNaarBioscoop: geeft null terug als er geen bioscoop is
    @Test void testStuurNaarBioscoopGeenBioscoop() {
        Hotel hotel = maakHotel();
        voegGastToe(hotel, 1, 2, 1);
        assertNull(new GastRoutingService(hotel).stuurNaarBioscoop(1));
    }

    // stuurTerugNaarKamer: gast krijgt route naar zijn kamer
    @Test void testStuurTerugNaarKamerZetRoute() {
        Hotel hotel = maakHotel();
        Gast gast = voegGastToe(hotel, 1, 2, 1);
        Kamer kamer = new Kamer();
        kamer.posX = 4; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        kamer.koppelGast(gast);
        new GastRoutingService(hotel).stuurTerugNaarKamer(1);
        assertNotNull(gast.doelVakje);
    }

    // stuurTerugNaarKamer: geen crash als gast geen kamer heeft
    @Test void testStuurTerugNaarKamerZonderKamer() {
        Hotel hotel = maakHotel();
        voegGastToe(hotel, 1, 2, 1);
        assertDoesNotThrow(() -> new GastRoutingService(hotel).stuurTerugNaarKamer(1));
    }

    // stuurTerugNaarKamer: geen crash als gast niet bestaat
    @Test void testStuurTerugNaarKamerGastNietGevonden() {
        Hotel hotel = maakHotel();
        assertDoesNotThrow(() -> new GastRoutingService(hotel).stuurTerugNaarKamer(99));
    }

    // stuurTerugNaarKamer: geen crash als hotel geen pathfinder heeft
    @Test void testStuurTerugNaarKamerZonderPathfinder() {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(3, 3);
        Gast gast = new Gast(1, 1);
        hotel.voegPersoonToe(gast);
        assertDoesNotThrow(() -> new GastRoutingService(hotel).stuurTerugNaarKamer(1));
    }
}
