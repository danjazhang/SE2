package Model.ruimte;

import Model.persoon.Persoon;

// Verantwoordelijkheid: de trap in het hotel voorstellen.
// De trap is de enige manier om van verdieping te wisselen tijdens een brandalarm.
// Trap erft van Ruimte via 'extends Ruimte'.
public class Trap extends Ruimte {

    // De tijd in ticks die het kost om één verdieping de trap op of af te lopen.
    public int tijdperverdieping;

    // Constructor: sla tijdperverdieping op.
    public Trap(int tijdperverdieping) {
        this.tijdperverdieping = tijdperverdieping;
    }

    // Lege methode als placeholder voor het gebruiken van de trap.
    public void gebruikTrap(Persoon p) {}
}
