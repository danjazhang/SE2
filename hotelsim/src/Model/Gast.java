package Model;

// Stelt een hotelgast voor
// Erft van Persoon en heeft een gewenst aantal sterren en een kamer
public class Gast extends Persoon {

    // het aantal sterren dat de gast wil in een kamer
    public int gewensteSterren;

    // de kamer waar de gast momenteel verblijft
    public Kamer kamer;

    // constructor: maak een gast aan met een gewenst aantal sterren
    public Gast(int gewensteSterren) {
        this.gewensteSterren = gewensteSterren;
        this.kamer = null;
    }

    // koppel de gast aan een kamer
    public void checkIn(Kamer k) { this.kamer = k; }

    // ga naar een activiteit in het hotel
    public void gaNaarActiviteit() {}

    // verlaat de kamer
    public void checkOut() {}
}
