public class Persoon {
    // het vakje waar de persoon nu staat
    Vakje huidigVakje;

    // het vakje waar de persoon naartoe wil gaan
    Vakje doelVakje;

    //constructor
    public Persoon(){
        this.huidigVakje = null;
        this.doelVakje = null;
    }

    public void beweeg(){

    }

    //zet het doel vakje
    public void zetDoel(Vakje v){
        this.doelVakje = v;
    }
    public void voerTaakUit(){

    }
}
