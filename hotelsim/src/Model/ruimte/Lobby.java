package Model.ruimte;

import Model.*;
import Model.IEventListener;
import Model.ILogger;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

public class Lobby extends Ruimte implements IEventListener {

    //positie van de balie
    private int balieX;
    private int balieY;
    private Hotel hotel;
    private ILogger logger;
    private PersonenService personenService;

    public Lobby(int posX, int posY, int breedte, int hoogte, int balieX, int balieY, Hotel hotel, ILogger logger) {
        super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
        this.hotel = hotel;
        this.logger = logger;
        this.personenService = new PersonenService(hotel);
    }

    @Override
    public void onEvent(HotelEvent event) {
        //check of het een checkin of checkout event is en roep de juiste methode aan
        if (event.getEventType() == HotelEventType.CHECK_IN){
            behandelCheckIn(event.getGuestId(), event.getTime());
        } else if (event.getEventType()== HotelEventType.CHECK_OUT){
            behandelCheckOut(event.getGuestId(), event.getTime());
        }
    }

    private void behandelCheckIn(int gastId, int tijd) {
        //zet gast op balie als startpunt
        Vakje startVakje = hotel.layout.krijgVakje(balieX, hotel.hoogte);
        //factory maakt gast en voegt toe aan hotel
        Gast gast = personenService.maakGast(gastId, startVakje);

        //zoek een vrije schone kamer
        Kamer kamer = vindVrijeKamer();
        if (kamer != null) {
            //koppel de gast aan kamer
            kamer.koppelGast(gast);
            //bereken en zet route naar kamer via pathfinder
            hotel.pathfinder.zetRoute(gast, kamer);
        }
        if (logger != null) {
            if (kamer != null) {
                //toon kamernummer zodat duidelijk is welke kamer de gast krijgt
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt in kamer no " + kamer.getKamernummer());
            } else {
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt in, maar er is geen vrije kamer");
            }
        }
    }

    private void behandelCheckOut(int gastId, int tijd) {
        //zoek de gast op basis van id
        Gast gast = personenService.vindGast(gastId);
        if (gast == null) return;
        //sla kamer op want na uitchecken is kamer null
        Kamer kamer = gast.kamer;
        if (kamer != null) kamer.ontkoppelGast(gast);
        //zoek vrije schoonmaker
        Schoonmaker schoonmaker = personenService.vindVrijeSchoonmaker();
        //check of er een schoonmaker is en of de gast een kamer had
        if (schoonmaker != null && kamer != null) {
            schoonmaker.maakKamerSchoon(kamer);
            //stuur schoonmaker naar de kamer via een route
            hotel.pathfinder.zetRoute(schoonmaker, kamer);
        }
        if (logger != null) {
            if (kamer != null) {
                //toon kamernummer zodat duidelijk is welke kamer vrijkomt
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt uit uit kamer " + kamer.getKamernummer());
            } else {
                logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt uit");
            }
        }
    }

    private Kamer vindVrijeKamer() {
        //loop door alle ruimtes
        for (Ruimte r : hotel.ruimtes) {
            //geeft de kamer terug als die vrij en schoon is, anders null
            Kamer k = r.getVrijeKamer();
            if (k != null) return k;
        }
        //geef null terug als er geen vrije kamer is
        return null;
    }

    public void setLogger(ILogger logger) { this.logger = logger; }
    public void toonStatusScherm() { System.out.println("Status van hotel wordt getoond..."); }
    public int getBalieX() { return balieX; }
    public int getBalieY() { return balieY; }
}
