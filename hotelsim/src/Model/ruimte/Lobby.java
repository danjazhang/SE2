package Model.ruimte;

import Model.*;
import Model.IEventListener;
import Model.ILogger;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.service.CheckInService;
import Model.service.CheckOutService;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

// De lobby is het centrale start- en eindpunt voor gasten.
// Hier gebeurt check-in, check-out en de eerste toewijzing van kamers.
public class Lobby extends Ruimte implements IEventListener {

    //positie van
    private int balieX;
    private int balieY;
    private Hotel hotel;
    private ILogger logger;
    // Deze services voeren de echte use-case logica uit, zodat Lobby alleen events ontvangt.
    private CheckInService checkInService;
    private CheckOutService checkOutService;

    public Lobby(int posX, int posY, int breedte, int hoogte, int balieX, int balieY, Hotel hotel, ILogger logger) {
        super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
        this.hotel = hotel;
        this.logger = logger;
        this.checkInService = new CheckInService(hotel, balieX);
        this.checkOutService = new CheckOutService(hotel);
    }

    @Override
    //onEvent wordt aangeroepen bij elk event
    public void onEvent(HotelEvent event) {
        //check of het een checkin of checkout event is en roep de juiste methode aan
        if (event.getEventType() == HotelEventType.CHECK_IN){
            behandelCheckIn(event.getGuestId(), event.getTime());
        } else if (event.getEventType()== HotelEventType.CHECK_OUT){
            behandelCheckOut(event.getGuestId(), event.getTime());
        }
    }

    private void behandelCheckIn(int gastId, int tijd) {
        CheckInService.CheckInResult resultaat = checkInService.checkInGast(gastId);
        Kamer toegewezenKamer = resultaat.getKamer();
        if (logger != null) {
            if (toegewezenKamer != null) {
                // We tonen hier ook het kamernummer in de log.
                // Dat is handig omdat dezelfde gast later ook naar restaurant,
                // cinema of fitness kan gaan. Zo blijft duidelijk bij welke kamer
                // die gast oorspronkelijk hoort.
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt in kamer no " + toegewezenKamer.getKamernummer());
            } else {
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt in, maar er is geen vrije kamer");
            }
        }
        hotel.notifyListeners();
    }

    private void behandelCheckOut(int gastId, int tijd) {
        CheckOutService.CheckOutResult resultaat = checkOutService.checkOutGast(gastId);
        Kamer kamer = resultaat.getKamer();
        if (logger != null) {
            if (kamer != null) {
                // Ook bij uitchecken tonen we het kamernummer,
                // zodat in de eventlog duidelijk blijft welke kamer vrijkomt.
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt uit uit kamer " + kamer.getKamernummer());
            } else {
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt uit");
            }
        }
        hotel.notifyListeners();
    }

    public void setLogger(ILogger logger) { this.logger = logger; }
    public void toonStatusScherm() { System.out.println("Status van hotel wordt getoond..."); }
    public int getBalieX() { return balieX; }
    public int getBalieY() { return balieY; }
}
