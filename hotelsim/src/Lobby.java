public class Lobby extends Ruimte implements HotelEventListener {

    private int balieX;
    private int balieY;

    // constructor
    public Lobby() {
        //super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
    }

    public void toonStatusScherm() {
        System.out.println("Status van hotel wordt getoond...");
    }

    public void pauzeerSim() {
        System.out.println("Simulatie gepauzeerd.");
    }

    public int getBalieX() {
        return balieX;
    }

    public int getBalieY() {
        return balieY;
    }

    @Override
    public void notify(HotelEvent evt) {

        if (evt.getEventType() == HotelEventType.CHECK_IN) {
            System.out.println("Lobby: gast " + evt.getGuestId() + " checkt in");
        }

        if (evt.getEventType() == HotelEventType.CHECK_OUT) {
            System.out.println("Lobby: gast " + evt.getGuestId() + " checkt uit");
        }
    }
}