package Model.ruimte;

import Model.persoon.Persoon;

public class Trap extends Ruimte {

    // tijd die het kost om één verdieping te lopen
    public int tijdperverdieping;

    // constructor: stel de tijd per verdieping in
    public Trap(int tijdperverdieping) {
        this.tijdperverdieping = tijdperverdieping;
    }

    @Override
    public void setTijdPerVerdieping(int duur) { this.tijdperverdieping = duur; }
}