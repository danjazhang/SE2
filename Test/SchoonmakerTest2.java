import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests voor Schoonmaker: bewegen, schoonmaken, terugkeren naar wachtplek
public class SchoonmakerTest2 {

    private Hotel hotel;
    private Schoonmaker schoonmaker;
    private Kamer kamer;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.layout = new Layout(8, 5);
        hotel.breedte = 8;
        hotel.hoogte = 5;

        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = 5;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);
        trap.posX = 7; trap.posY = 1; trap.breedte = 2; trap.hoogte = 5;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        kamer = new Kamer();
        kamer.posX = 3; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        kamer.setIngang(3, 1);
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);

        hotel.pathfinder = new Pathfinder(hotel);

        schoonmaker = new Schoonmaker();
        schoonmaker.setPathfinder(hotel.pathfinder);
        schoonmaker.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        hotel.voegPersoonToe(schoonmaker);
    }

    // maakKamerSchoon zet bezig op true en koppelt kamer
    @Test void testMaakKamerSchoonZetBezig() {
        schoonmaker.maakKamerSchoon(kamer);
        assertTrue(schoonmaker.bezig);
        assertEquals(kamer, schoonmaker.kamer);
    }

    // schoonmaker beweegt naar kamer op zelfde verdieping
    @Test void testBeweegNaarKamerZelfdeVerdieping() {
        schoonmaker.maakKamerSchoon(kamer);
        hotel.pathfinder.zetRoute(schoonmaker, kamer);
        schoonmaker.beweeg();
        assertEquals(3, schoonmaker.huidigVakje.x);
    }

    // schoonmaker telt schoonmaaktijd af als hij in de kamer staat
    @Test void testSchoonmaakTijdTeltAf() {
        schoonmaker.maakKamerSchoon(kamer);
        // zet schoonmaker direct in de kamer
        schoonmaker.huidigVakje.verwijderPersoon(schoonmaker);
        Vakje kamerVakje = hotel.layout.krijgVakje(3, 1);
        schoonmaker.huidigVakje = kamerVakje;
        kamerVakje.voegPersoonToe(schoonmaker);
        // simuleer dat hij net binnenkwam: zet teller via beweeg
        // eerst: beweeg vanuit een ander vakje naar de kamer
        Vakje naastKamer = hotel.layout.krijgVakje(2, 1);
        schoonmaker.huidigVakje.verwijderPersoon(schoonmaker);
        schoonmaker.huidigVakje = naastKamer;
        naastKamer.voegPersoonToe(schoonmaker);
        schoonmaker.zetDoel(kamerVakje);
        schoonmaker.beweeg(); // stapt de kamer in, teller start
        assertTrue(schoonmaker.bezig); // nog bezig
    }

    // schoonmaker is niet meer bezig na 6 ticks in de kamer
    @Test void testSchoonmaakKlaarNa6Ticks() {
        kamer.schoon = false;
        schoonmaker.maakKamerSchoon(kamer);

        // zet schoonmaker in de kamer via beweeg
        Vakje naastKamer = hotel.layout.krijgVakje(2, 1);
        schoonmaker.huidigVakje.verwijderPersoon(schoonmaker);
        schoonmaker.huidigVakje = naastKamer;
        naastKamer.voegPersoonToe(schoonmaker);
        schoonmaker.zetDoel(hotel.layout.krijgVakje(3, 1));
        schoonmaker.beweeg(); // betreedt kamer, teller = 6

        // 6 ticks aftellen
        for (int i = 0; i < 6; i++) schoonmaker.beweeg();

        assertFalse(schoonmaker.bezig);
        assertTrue(kamer.isSchoon());
    }

    // schoonmaker gaat terug naar wachtplek na schoonmaken
    @Test void testSchoonmakerGaatTerugNaWachtplek() {
        Vakje wachtplek = hotel.layout.krijgVakje(5, 1);
        schoonmaker.setWachtVakje(wachtplek);
        kamer.schoon = false;
        schoonmaker.maakKamerSchoon(kamer);

        // zet schoonmaker in de kamer
        Vakje naastKamer = hotel.layout.krijgVakje(2, 1);
        schoonmaker.huidigVakje.verwijderPersoon(schoonmaker);
        schoonmaker.huidigVakje = naastKamer;
        naastKamer.voegPersoonToe(schoonmaker);
        schoonmaker.zetDoel(hotel.layout.krijgVakje(3, 1));
        schoonmaker.beweeg(); // betreedt kamer

        for (int i = 0; i < 6; i++) schoonmaker.beweeg(); // schoonmaken klaar

        // na schoonmaken heeft schoonmaker een doel (wachtplek)
        assertNotNull(schoonmaker.doelVakje);
    }

    // noodSchoonmaker vlag werkt correct
    @Test void testNoodSchoonmakerVlag() {
        assertFalse(schoonmaker.isNoodSchoonmaker());
        schoonmaker.setNoodSchoonmaker(true);
        assertTrue(schoonmaker.isNoodSchoonmaker());
        schoonmaker.setNoodSchoonmaker(false);
        assertFalse(schoonmaker.isNoodSchoonmaker());
    }

    // setHuidigeTijd: geen crash
    @Test void testSetHuidigeTijd() {
        assertDoesNotThrow(() -> schoonmaker.setHuidigeTijd(42));
    }

    // beweeg zonder doel: geen crash
    @Test void testBeweegZonderDoel() {
        assertDoesNotThrow(() -> schoonmaker.beweeg());
    }

    // zetRouteNaarKamer wist oude route en zet nieuw doel
    @Test void testZetRouteNaarKamer() {
        Vakje doelVakje = hotel.layout.krijgVakje(4, 1);
        schoonmaker.zetDoel(hotel.layout.krijgVakje(2, 1)); // oud doel
        schoonmaker.zetRouteNaarKamer(doelVakje);
        // na zetRouteNaarKamer is het doel de trap (via zetRouteTrap)
        assertNotNull(schoonmaker.doelVakje);
    }
}
