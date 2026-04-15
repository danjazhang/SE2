package Model;

// Stelt de trap voor in het hotel
// Heeft een tijdsduur per verdieping die aangeeft hoe lang het duurt om een verdieping te lopen
public class Trap extends Ruimte{

    // tijd die het kost om één verdieping te lopen
    public int tijdperverdieping;

    // constructor: stel de tijd per verdieping in
    public Trap(int tijdperverdieping) {
        this.tijdperverdieping = tijdperverdieping;
    }

    // laat een persoon de trap gebruiken
    public void gebruikTrap(Persoon p) {}
}
