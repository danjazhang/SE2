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
            System.out.println("Schoonmaker: noodsituatie! Naar kamer.");
        }
    }
}