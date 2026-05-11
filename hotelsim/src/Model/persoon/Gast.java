package Model.persoon;

import Model.ruimte.Kamer;


// Erft van Persoon en heeft een gewenst aantal sterren en een kamer
public class Gast extends Persoon {

    //gastid die we van de library ophalen
    public int gastId;

    // het aantal sterren dat de gast wil in een kamer
    public int gewensteSterren;

    // de kamer waar de gast momenteel verblijft
    public Kamer kamer;

    // of de gast onderweg is naar de lobby om uit te checken
    public boolean uitcheckend = false;

    // constructor: maak een gast aan met een gewenst aantal sterren
    public Gast(int gastId, int gewensteSterren) {
        this.gastId = gastId;
        this.gewensteSterren = gewensteSterren;
        this.kamer = null;
    }

    // ga naar een activiteit in het hotel
    public void gaNaarActiviteit() {}

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