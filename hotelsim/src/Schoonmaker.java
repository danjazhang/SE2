import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import hotelevents.HotelEventListener;

public class Schoonmaker extends Persoon implements HotelEventListener {

    boolean bezig;
    Kamer kamer;

    public Schoonmaker(){
        this.bezig = false;
        this.kamer = null;
    }

    public void maakKamerSchoon(Kamer k){}

    public void handelEmergency(Kamer k){}

    public void gaNaarOptimalePositie(){}

    @Override
    public void notify(HotelEvent evt) {

        if (evt.getEventType() == HotelEventType.CLEANING_EMERGENCY) {
            System.out.println("[" + evt.getTime() + "] Schoonmaker: noodsituatie! Kamer moet worden schoongemaakt");
        }
    }
}