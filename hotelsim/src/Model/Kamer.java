package Model;

public class Kamer extends Ruimte {
    public int sterren;
    public Gast Gast;
    public boolean schoon;

    public Kamer() {
        this.schoon = true;
        this.Gast = null;
    }

    public void checkIn(Gast g) {}
    public void checkOut() {}
    public void Schoonmaken() {}
}
