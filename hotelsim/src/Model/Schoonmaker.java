package Model;

import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;

// Stelt een schoonmaker voor in het hotel
// Erft van Persoon en reageert op schoonmaak events
public class Schoonmaker extends Persoon implements HotelEventListener {

    // of de schoonmaker momenteel bezig is
    public boolean bezig;

    // de kamer die de schoonmaker momenteel schoonmaakt
    public Kamer kamer;

    // constructor: schoonmaker begint niet bezig en zonder kamer
    public Schoonmaker() {
        this.bezig = false;
        this.kamer = null;
    }

    // maak een kamer schoon
    public void maakKamerSchoon(Kamer k) {}

    // handel een noodsituatie af
    public void handelEmergency(Kamer k) {}

    // ga naar de optimale positie in het hotel
    public void gaNaarOptimalePositie() {}

    // reageer op een schoonmaak noodsituatie event
    @Override
    public void notify(HotelEvent evt) {
        if (evt.getEventType() == HotelEventType.CLEANING_EMERGENCY) {
            System.out.println("[" + evt.getTime() + "] Schoonmaker: noodsituatie! Kamer moet worden schoongemaakt");
        }
    }
}
