package Model;

import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventType;

public class Schoonmaker extends Persoon implements HotelEventListener {

    public boolean bezig;
    public Kamer kamer;

    public Schoonmaker() {
        this.bezig = false;
        this.kamer = null;
    }

    public void maakKamerSchoon(Kamer k) {}
    public void handelEmergency(Kamer k) {}
    public void gaNaarOptimalePositie() {}

    @Override
    public void notify(HotelEvent evt) {
        if (evt.getEventType() == HotelEventType.CLEANING_EMERGENCY) {
            System.out.println("[" + evt.getTime() + "] Schoonmaker: noodsituatie! Kamer moet worden schoongemaakt");
        }
    }
}
