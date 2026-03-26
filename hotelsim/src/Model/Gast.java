package Model;

public class Gast extends Persoon {

    public int gewensteSterren;
    public Kamer kamer;

    //constructor
    public Gast(int gewensteSterren) {
        this.gewensteSterren = gewensteSterren;
        this.kamer = null;
    }

    public void checkIn(Kamer k) { this.kamer = k; }
    public void gaNaarActiviteit() {}
    public void checkOut() {}
}
