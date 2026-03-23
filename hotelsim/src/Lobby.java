import hotelevents.HotelEvent;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;
import hotelevents.HotelEventListener;

public class Lobby extends Ruimte implements HotelEventListener {

    private int balieX;
    private int balieY;

    // constructor
    public Lobby(int posX, int posY, int breedte, int hoogte, int balieX, int balieY) {
        super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
    }

    public void toonStatusScherm() {
        System.out.println("Status van hotel wordt getoond...");
    }

    public int getBalieX() {
        return balieX;
    }

    public int getBalieY() {
        return balieY;
    }

    @Override
    public void notify(HotelEvent evt) {

        switch (evt.getEventType()) {

            case CHECK_IN:
                System.out.println("[" + evt.getTime() + "] Lobby: gast "
                        + evt.getGuestId() + " checkt in");
                break;

            case CHECK_OUT:
                System.out.println("[" + evt.getTime() + "] Lobby: gast "
                        + evt.getGuestId() + " checkt uit");
                break;

            default:
                break;
        }
    }
}