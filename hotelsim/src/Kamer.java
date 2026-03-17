public class Kamer extends Ruimte {
    int sterren;
    Gast Gast;
    boolean schoon;

    //constructor
    //int sterren in parameters geeft error in hotel.java
    public Kamer(){
        this.schoon = true;
        this.Gast = null;
    }

    public void checkIn(Gast g){}
    public void checkOut(){}
    public void Schoonmaken(){}
}
