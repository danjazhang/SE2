package Model;

// Stelt een schoonmaker voor in het hotel
// Erft van Persoon en reageert op schoonmaak events
public class Schoonmaker extends Persoon {

    // of de schoonmaker momenteel bezig is
    public boolean bezig;

    // de kamer die de schoonmaker momenteel schoonmaakt
    public Kamer kamer;

    // constructor: schoonmaker begint niet bezig en zonder kamer
    public Schoonmaker() {
        this.bezig = false;
        this.kamer = null;
    }

    // maak een kamer schoon
    public void maakKamerSchoon(Kamer k) {
        this.kamer = k;
        this.bezig = true;
        k.schoonmaken();
        this.bezig = false;
        this.kamer = null;
    }

    // handel een noodsituatie af
    public void handelEmergency(Kamer k) {
        maakKamerSchoon(k);
    }

    // ga naar de optimale positie in het hotel
    public void gaNaarOptimalePositie() {}
}
