package Model.persoon;

import Model.events.IEventListener;
import Model.ILogger;
import Model.events.SchoonmaakEindEvent;
import Model.ruimte.Kamer;

import hotelevents.HotelEvent;
import hotelevents.HotelEventType;


// Bij CLEANING_EMERGENCY maakt hij een SchoonmaakEindEvent aan en logt de noodsituatie
public class Schoonmaker extends Persoon implements IEventListener {

    // of de schoonmaker momenteel bezig is
    public boolean bezig;

    // de kamer die de schoonmaker momenteel schoonmaakt
    public Kamer kamer;

    // logger voor het loggen naar de GUI
    private ILogger logger;

    // constructor met logger
    public Schoonmaker(ILogger logger) {
        this.bezig = false;
        this.kamer = null;
        this.logger = logger;
    }

    // lege constructor voor als er geen logger nodig is (bijv. in testen)
    public Schoonmaker() {
        this.bezig = false;
        this.kamer = null;
    }

    // wordt aangeroepen door EventController als er een library event binnenkomt
    // schoonmaker reageert alleen op CLEANING_EMERGENCY
    @Override
    public void onEvent(HotelEvent event) {
        // als het een schoonmaak noodgeval is, maak een SchoonmaakEindEvent aan en log dat
        if (event.getEventType() == HotelEventType.CLEANING_EMERGENCY) {
            SchoonmaakEindEvent eindEvent = new SchoonmaakEindEvent(event.getTime(), event.getGuestId());
            if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Schoonmaker: noodsituatie!");
            this.bezig = true;
        }
    }

    // maak een kamer schoon
    public void maakKamerSchoon(Kamer k) {
        this.kamer = k;
        this.bezig = true;
        k.schoonmaken();
        this.bezig = false;
        this.kamer = null;
    }

    // handel een noodsituatie af
    public void handelEmergency(Kamer k) {
        maakKamerSchoon(k);
    }

    // ga naar de optimale positie in het hotel
    public void gaNaarOptimalePositie() {}
}