public class Lobby extends Ruimte {

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

    public void pauzeerSim() {
        System.out.println("Simulatie gepauzeerd.");
    }

    public int getBalieX() {
        return balieX;
    }

    public int getBalieY() {
        return balieY;
    }
}