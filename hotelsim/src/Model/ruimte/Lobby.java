package Model.ruimte;

import Model.*;
import Model.IEventListener;
import Model.ILogger;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

import java.util.List;

public class Lobby extends Ruimte implements IEventListener {

    //positie van
    private int balieX;
    private int balieY;
    private Hotel hotel;
    private ILogger logger;

    public Lobby(int posX, int posY, int breedte, int hoogte, int balieX, int balieY, Hotel hotel, ILogger logger) {
        super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
        this.hotel = hotel;
        this.logger = logger;
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
        //factory maakt gast
        //zet gast op balie als startpunt
        Vakje startVakje = hotel.layout.krijgVakje(balieX, hotel.hoogte);
        PersonenFactory personenFactory = new PersonenFactory();
        Gast gast = personenFactory.maakGast(gastId, 1, hotel.pathfinder, startVakje);

        //voeg persoon toe aan personenlijst in hotel
        hotel.voegPersoonToe(gast);

        //zoek een vrije schone kamer
        Kamer kamer = vindVrijeKamer();
        if (kamer != null) {
            //koppel de gast aan kamer
            kamer.koppelGast(gast);

            //stel kamer als doel
            Vakje doel = hotel.layout.krijgVakje(kamer.posX, kamer.posY);
            if (startVakje != null && doel != null){
                Pathfinder pathfinder = new Pathfinder(hotel);
                List<Vakje> route = pathfinder.berekenRoute(startVakje, doel);
                
                gast.zetDoel(route.get(0));
                for (int i = 1; i< route.size(); i++){
                    gast.voegTussendoelToe(route.get(i));
                }
            }
        }
        if (logger != null) logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt in");
    }

    private void behandelCheckOut(int gastId, int tijd) {
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                Gast gast = (Gast) p;
                //sla kamer op want na uitchecken is kamer null
                Kamer kamer = gast.kamer;
                kamer.ontkoppelGast(gast);
                //zoek vrije schoonmaker
                Schoonmaker schoonmaker = vindVrijeSchoonmaker();
                //check of er een schoonmaker is en of de gast een kamer had
                if (schoonmaker != null && kamer != null) {
                    schoonmaker.maakKamerSchoon(kamer);
                }
                break;
            }
        }
        if (logger != null) logger.log("[" + tijd + "] Lobby: gast " + gastId + " checkt uit");
    }

    private Kamer vindVrijeKamer() {
        //loop door alle ruimtes
        for (Ruimte r : hotel.ruimtes) {
            //check of het een kamer is
            if (r instanceof Kamer) {
                Kamer k = (Kamer) r;
                //check of het niet bezet is en of die schoon is
                if (!k.isBezet() && k.isSchoon()) return k;
            }
        }//geef null terug als er geen vrije kamer is
        return null;
    }

    private Schoonmaker vindVrijeSchoonmaker() {
        //loop door alle personen
        for (Persoon p : hotel.personen) {
            //zoek een schoonmaker die niet bezig is
            if (p instanceof Schoonmaker && !((Schoonmaker) p).bezig) return (Schoonmaker) p;
        }
        //geef null teruug als alle schoonmakers bezig zijn
        return null;
    }

    public void setLogger(ILogger logger) { this.logger = logger; }
    public void toonStatusScherm() { System.out.println("Status van hotel wordt getoond..."); }
    public int getBalieX() { return balieX; }
    public int getBalieY() { return balieY; }
}
