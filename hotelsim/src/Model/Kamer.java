package Model;

// Stelt een hotelkamer voor
// Erft van Ruimte en heeft een aantal sterren, een gast en een schoon-status
public class Kamer extends Ruimte {

    // telt automatisch door zodat elke kamer een uniek nummer krijgt
    private static int volgendKamerNummer = 1;

    // uniek kamernummer
    public int kamerNummer;

    // het aantal sterren van de kamer (1 t/m 5)
    public int sterren;

    // de gast die momenteel in de kamer verblijft
    public Gast Gast;

    // of de kamer schoon is
    public boolean schoon;

    // constructor: kamer begint schoon en zonder gast
    public Kamer() {
        this.kamerNummer = volgendKamerNummer++;
        this.schoon = true;
        this.Gast = null;
    }

    // laat een gast inchecken in de kamer
    public void checkIn(Gast g) {
        this.Gast = g;
        this.schoon = false;
        if (g != null) {
            g.checkIn(this);
        }
    }

    // laat de gast uitchecken
    public void checkOut() {
        if (this.Gast != null) {
            Gast huidigeGast = this.Gast;
            this.Gast = null;
            huidigeGast.checkOut();
        }
    }

    // maak de kamer schoon
    public void Schoonmaken() {
        this.schoon = true;
    }

    public int getKamerNummer() {
        return kamerNummer;
    }
}
