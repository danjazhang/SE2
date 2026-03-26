package Model;

// Basisklasse voor alle personen in het hotel
// Gast en Schoonmaker erven van deze klasse
public class Persoon {

    // het vakje waar de persoon zich momenteel bevindt
    public Vakje huidigVakje;

    // het vakje waar de persoon naartoe wil
    public Vakje doelVakje;

    // constructor: persoon begint zonder positie
    public Persoon() {
        this.huidigVakje = null;
        this.doelVakje = null;
    }

    // beweeg de persoon naar het doelVakje
    public void beweeg() {}

    // stel het doelVakje in
    public void zetDoel(Vakje v) { this.doelVakje = v; }

    // voer de taak van de persoon uit
    public void voerTaakUit() {}
}
