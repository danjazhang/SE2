import Model.Hotel;
import Model.Pathfinder;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import Model.ruimte.Trap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersoonTest {

    // Ik maak met deze hulpmethode een hotel met layout, lift en trap,
    // zodat een persoon in de testen echt kan bewegen.
    private Hotel maakHotel(int breedte, int hoogte) {
        Hotel hotel = new Hotel();
        hotel.layout = new Layout(breedte, hoogte);
        hotel.breedte = breedte;
        hotel.hoogte = hoogte;

        Lift lift = new Lift(hotel);
        lift.posX = 1; lift.posY = 1; lift.breedte = 1; lift.hoogte = hoogte;
        hotel.lift = lift;
        hotel.ruimtes.add(lift);
        hotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(2);
        trap.posX = breedte; trap.posY = 1; trap.breedte = 1; trap.hoogte = hoogte;
        hotel.trap = trap;
        hotel.ruimtes.add(trap);
        hotel.layout.plaatsRuimte(trap);

        hotel.pathfinder = new Pathfinder(hotel);
        return hotel;
    }

    // Ik maak een nieuwe gast aan; ik verwacht dat huidigVakje en doelVakje nog null zijn.
    @Test void testConstructor() {
        Gast p = new Gast(1, 1);
        assertNull(p.huidigVakje);
        assertNull(p.doelVakje);
    }

    // Ik zet een doelvakje op een persoon; ik verwacht dat dit vakje als doel bewaard wordt.
    @Test void testZetDoel() {
        Gast p = new Gast(1, 1);
        Vakje v = new Vakje();
        p.zetDoel(v);
        assertEquals(v, p.doelVakje);
    }

    // Ik geef een persoon een startpositie; ik verwacht dat hij op dat vakje terechtkomt.
    @Test void testZetStartPositie() {
        Gast p = new Gast(1, 1);
        Vakje v = new Vakje();
        p.zetStartPositie(v);
        assertEquals(v, p.huidigVakje);
        assertTrue(v.krijgPersonen().contains(p));
    }

    // Ik laat een persoon zonder doel bewegen; ik verwacht dat dit geen crash geeft.
    @Test void testBeweegZonderDoelCrashetNiet() {
        assertDoesNotThrow(() -> new Gast(1, 1).beweeg());
    }

    // Ik laat een persoon zonder pathfinder bewegen; ik verwacht dat dit geen crash geeft.
    @Test void testBeweegZonderPathfinderCrashetNiet() {
        Gast p = new Gast(1, 1);
        Layout layout = new Layout(3, 3);
        p.zetStartPositie(layout.krijgVakje(1, 1));
        p.zetDoel(layout.krijgVakje(3, 1));
        assertDoesNotThrow(() -> p.beweeg());
    }

    // Ik laat een persoon naar rechts bewegen; ik verwacht dat hij precies één stap opschuift.
    @Test void testBeweegNaarDoel() {
        Hotel hotel = maakHotel(5, 3);
        Gast p = new Gast(1, 1);
        p.setPathfinder(hotel.pathfinder);
        p.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        p.zetDoel(hotel.layout.krijgVakje(4, 1));
        p.beweeg();
        assertEquals(3, p.huidigVakje.x);
    }

    // Ik laat een persoon in de y-richting bewegen; ik verwacht dat zijn y-coördinaat met één verandert.
    @Test void testBeweegInYRichting() {
        Hotel hotel = maakHotel(5, 5);
        Gast p = new Gast(1, 1);
        p.setPathfinder(hotel.pathfinder);
        p.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        p.zetDoel(hotel.layout.krijgVakje(2, 3));
        p.beweeg();
        assertEquals(2, p.huidigVakje.y);
    }

    // Ik voeg een tussendoel toe zonder bestaand doel; ik verwacht dat dit tussendoel meteen het doel wordt.


    // Ik voeg een tussendoel toe terwijl er al een doel is; ik verwacht dat het huidige doel onveranderd blijft.
    @Test void testVoegTussendoelToeMetDoel() {
        Gast p = new Gast(1, 1);
        Vakje v1 = new Vakje();
        Vakje v2 = new Vakje();
        p.zetDoel(v1);
        p.voegTussendoelToe(v2);
        assertEquals(v1, p.doelVakje);
    }

    // Ik bereik een eerste doel met een tussendoel erachter; ik verwacht dat het tussendoel daarna actief wordt.
    @Test void testTussendoelWordtDoelNaAankomen() {
        Hotel hotel = maakHotel(5, 3);
        Gast p = new Gast(1, 1);
        p.setPathfinder(hotel.pathfinder);
        Vakje start = hotel.layout.krijgVakje(2, 1);
        Vakje tussendoel = hotel.layout.krijgVakje(3, 1);
        p.zetStartPositie(start);
        p.zetDoel(start);
        p.voegTussendoelToe(tussendoel);
        p.beweeg();
        assertEquals(tussendoel, p.doelVakje);
    }

    // Ik laat een persoon vanuit een kamer vertrekken; ik verwacht dat die kamer hem daarna niet meer bevat.
    @Test void testBeweegVerlaatRuimte() {
        Hotel hotel = maakHotel(5, 3);
        Gast p = new Gast(1, 1);
        p.setPathfinder(hotel.pathfinder);
        Kamer kamer = new Kamer();
        kamer.posX = 2; kamer.posY = 1; kamer.breedte = 1; kamer.hoogte = 1;
        hotel.ruimtes.add(kamer);
        hotel.layout.plaatsRuimte(kamer);
        p.zetStartPositie(hotel.layout.krijgVakje(2, 1));
        p.zetDoel(hotel.layout.krijgVakje(4, 1));
        p.beweeg();
        assertFalse(kamer.getAanwezigen().contains(p));
    }

    // Ik stel een pathfinder in op een persoon; ik verwacht dat dit zonder crash lukt.
    @Test void testSetPathfinder() {
        Hotel hotel = maakHotel(5, 3);
        Gast p = new Gast(1, 1);
        assertDoesNotThrow(() -> p.setPathfinder(hotel.pathfinder));
    }

    // Ik roep voerTaakUit aan op een gast; ik verwacht dat dit geen crash geeft.
    @Test void testVoerTaakUit() {
        assertDoesNotThrow(() -> new Gast(1, 1).voerTaakUit());
    }
}
