import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LayoutTest {

    @Test
    void testLayoutAanmaak(){
        //maakt nieuwe layout met 6 breed en 8 hoog
        Layout layout = new Layout(6,8);
        //vakje moet bestaan
        assertNotNull(layout.krijgVakje(1, 1));
        //buiten het grid dus moet 0
        assertNull(layout.krijgVakje(0, 0));
        assertNull(layout.krijgVakje(7, 9));
        
    }

    @Test
    void testPlaatsRuimte(){
        //maakt nieuwe layout
        Layout layout = new Layout (6,8);
        //maakt nieuwe kamer
        Kamer kamer = new Kamer ();
        //geeft kamer positie en breedte,hoogte
        kamer.posX = 1; kamer.posY = 1;
        kamer.breedte = 2; kamer.hoogte = 2;
        //plaatst kamer
        layout.plaatsRuimte(kamer);
        //kamer locatie moet kloppen
        assertEquals(kamer, layout.krijgVakje(1,1).ruimte);
        assertEquals(kamer, layout.krijgVakje(2,2).ruimte);
    }
}
