package Model;

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

    // wordt aangeroepen door EventController als er een intern event binnenkomt
    // lobby reageert alleen op check-in en check-out, de rest negeert hij
    @Override
    public void onEvent(InternEvent event) {
        // als het een check-in event is, log dat de gast incheckt
        if (event instanceof CheckInEvent) {
            if (logger != null) logger.log("[" + event.getTijd() + "] Lobby: gast " + event.getGastId() + " checkt in");
        }
        // als het een check-out event is, log dat de gast uitcheckt
        else if (event instanceof CheckOutEvent) {
            if (logger != null) logger.log("[" + event.getTijd() + "] Lobby: gast " + event.getGastId() + " checkt uit");
        }
    }

    // toon het statusscherm van het hotel
    public void toonStatusScherm() { System.out.println("Status van hotel wordt getoond..."); }

    public int getBalieX() { return balieX; }
    public int getBalieY() { return balieY; }
}

