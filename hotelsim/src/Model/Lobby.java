package Model;

import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;

// Stelt de lobby voor in het hotel
// Erft van Ruimte en reageert op check-in en check-out events
public class Lobby extends Ruimte implements HotelEventListener {

    // positie van de balie in de lobby
    private int balieX;
    private int balieY;

    // constructor: maak een lobby aan met positie, afmetingen en balie positie
    public Lobby(int posX, int posY, int breedte, int hoogte, int balieX, int balieY) {
        super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
    }

    // toon het statusscherm van het hotel
    public void toonStatusScherm() { System.out.println("Status van hotel wordt getoond..."); }

    public int getBalieX() { return balieX; }
    public int getBalieY() { return balieY; }

    // reageer op check-in en check-out events
    @Override
    public void notify(HotelEvent evt) {
        switch (evt.getEventType()) {
            case CHECK_IN:
                System.out.println("[" + evt.getTime() + "] Lobby: gast " + evt.getGuestId() + " checkt in");
                break;
            case CHECK_OUT:
                System.out.println("[" + evt.getTime() + "] Lobby: gast " + evt.getGuestId() + " checkt uit");
                break;
            default: break;
        }
    }
}
