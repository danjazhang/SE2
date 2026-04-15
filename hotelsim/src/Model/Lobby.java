package Model;

import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

// Stelt de lobby voor in het hotel
// Erft van Ruimte en implementeert IEventListener
// De lobby is verantwoordelijk voor check-in en check-out logica (single responsibility)
public class Lobby extends Ruimte implements IEventListener {

    // positie van de balie in de lobby
    private int balieX;
    private int balieY;

    // logger voor het loggen naar de GUI
    private ILogger logger;

    // constructor: maak een lobby aan met positie, afmetingen, balie positie en logger
    public Lobby(int posX, int posY, int breedte, int hoogte, int balieX, int balieY, ILogger logger) {
        super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
        this.logger = logger;
    }

    // wordt aangeroepen door EventController als er een library event binnenkomt
    // lobby reageert alleen op CHECK_IN en CHECK_OUT, de rest negeert hij
    @Override
    public void onEvent(HotelEvent event) {
        // als het een check-in event is, log dat de gast incheckt
        if (event.getEventType() == HotelEventType.CHECK_IN) {
            if (logger != null) logger.log("[" + event.getTime() + "] Lobby: gast " + event.getGuestId() + " checkt in");
        }
        // als het een check-out event is, log dat de gast uitcheckt
        else if (event.getEventType() == HotelEventType.CHECK_OUT) {
            if (logger != null) logger.log("[" + event.getTime() + "] Lobby: gast " + event.getGuestId() + " checkt uit");
        }
    }

    // toon het statusscherm van het hotel
    public void toonStatusScherm() { System.out.println("Status van hotel wordt getoond..."); }

    public int getBalieX() { return balieX; }
    public int getBalieY() { return balieY; }
}
