package Model;

public class Persoon {
    public Vakje huidigVakje;
    public Vakje doelVakje;

    public Persoon() {
        this.huidigVakje = null;
        this.doelVakje = null;
    }

    public void beweeg() {}
    public void zetDoel(Vakje v) { this.doelVakje = v; }
    public void voerTaakUit() {}
}
