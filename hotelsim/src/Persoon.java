public class Persoon {
    // het vakje waar de persoon nu staat
    Vakje huidigVakje;

    // het vakje waar de persoon naartoe wil gaan
    Vakje doelVakje;

    /* In het huidige ontwerp is er geen aparte Taak-klasse.
 Gedrag van personen (bijv. eten, schoonmaken) wordt
direct geïmplementeerd in de klassen Gast en Schoonmaker.*/
    //taak huidigetaak ?

    //constructor
    public Persoon(){
        this.huidigVakje = null;
        this.doelVakje = null;
    }

    public void beweeg(){

    }

    //zet het doel vakje
    public void zetDoel(Vakje v){

    }
    public void voerTaakUit(){

    }
}
