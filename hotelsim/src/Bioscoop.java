import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import hotelevents.HotelEventListener;

public class Bioscoop extends Ruimte implements HotelEventListener {
    boolean filmBezig;
    int filmDuur;
    //arraylist gasten;

    //constructor
    public Bioscoop(){
        this.filmBezig = false;
        this.filmDuur = 0;
    }

    public void startFilm(){}
    public void stopFilm(){}
    public void betreedBioscoop(){}

    @Override
    public void notify(HotelEvent evt) {

        switch (evt.getEventType()) {

            case GOTO_CINEMA:
                System.out.println("[" + evt.getTime() + "] Bioscoop: gast "
                        + evt.getGuestId() + " komt binnen");
                break;

            case START_CINEMA:
                System.out.println("[" + evt.getTime() + "] Bioscoop: film start");
                break;

            default:
                break;
        }
    }


}
