package Model;

// Stelt een hotelgast voor
// Erft van Persoon en heeft een gewenst aantal sterren en een kamer
public class Gast extends Persoon {

    //gastid die we van de library ophalen
    public int gastId;

    // het aantal sterren dat de gast wil in een kamer
    public int gewensteSterren;

    // de kamer waar de gast momenteel verblijft
    public Kamer kamer;

    // constructor: maak een gast aan met een gewenst aantal sterren
    public Gast(int gastId, int gewensteSterren) {
        this.gastId = gastId;
        this.gewensteSterren = gewensteSterren;
        this.kamer = null;
    }

    // koppel de gast aan een kamer
    public void checkIn(Kamer k) { 
        k.koppelGast(this);
        }

    // ga naar een activiteit in het hotel
    public void gaNaarActiviteit() {}

    // ontkoppel gast van kamer
    public void checkOut() {
        if (kamer != null) {
            kamer.ontkoppelGast(this);
        }
    }

    // ga fysiek de kamer binnen
    public void gaNaarkamer(){
        if (kamer != null) {
            kamer.gastKomtBinnen(this);
        }
    }

    //ga fysiek de kamer uit
    public void verlaatKamer(){
        if (kamer != null){
            kamer.gastVerlaatKamer(this);
        }
    }
}
