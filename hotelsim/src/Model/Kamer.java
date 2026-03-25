package Model;

// Stelt een hotelkamer voor
// Erft van Ruimte en heeft een aantal sterren, een gast en een schoon-status
public class Kamer extends Ruimte {

    // het aantal sterren van de kamer (1 t/m 5)
    public int sterren;

    // de gast die momenteel in de kamer verblijft
    public Gast Gast;

    // of de kamer schoon is
    public boolean schoon;

    // constructor: kamer begint schoon en zonder gast
    public Kamer() {
        this.schoon = true;
        this.Gast = null;
    }

    // laat een gast inchecken in de kamer
    public void checkIn(Gast g) {}

    // laat de gast uitchecken
    public void checkOut() {}

    // maak de kamer schoon
    public void Schoonmaken() {}
}
