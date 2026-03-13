public class Gast extends Persoon {

    // het gewenste aantal sterren voor de kamer
    int gewensteSterren;

    // kamer waar de gast verblijft
    Kamer kamer;

    // Geen Activiteit-klasse in het klassediagram daarom wordt dit hier niet gebruikt.
    //Activiteit activiteit;

    // constructor
    public Gast(){}

    // gast checkt in bij een kamer
    public void checkIn(Kamer k){
        this.kamer = k;
    }

    // laat de gast naar een ruimte gaan bijv. restaurant of fitness
    public void gaNaarActiviteit(){}

    // gast checkt uit
    public void checkOut(){}
}