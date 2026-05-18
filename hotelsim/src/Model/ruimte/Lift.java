package Model.ruimte;

import Model.persoon.Persoon;

import java.util.ArrayList;
import java.util.List;

public class Lift extends Ruimte {

    // de verdieping waar de lift zich momenteel bevindt
    private int huidigeVerdieping;

    // de personen die momenteel in de lift zitten
    private List<Persoon> passagiers;

    // de verzoeken voor verdiepingen waar de lift naartoe moet
    private List<Integer> verzoeken;

    // of de deur open is
    private boolean deurOpen;

    // constructor: lift begint op verdieping 1 met lege lijsten
    public Lift() {
        this.huidigeVerdieping = 1;
        this.passagiers = new ArrayList<>();
        this.verzoeken = new ArrayList<>();
        this.deurOpen = false;
    }

    // ga één verdieping omhoog
    public void gaOmhoog() { huidigeVerdieping++; }

    // ga één verdieping omlaag
    public void gaOmlaag() { huidigeVerdieping--; }

    // open de deur
    public void openDeur() { deurOpen = true; }

    // sluit de deur
    public void sluitDeur() { deurOpen = false; }

    // voeg een verzoek toe voor een verdieping (alleen als het er nog niet in zit)
    public void voegVerzoekToe(int verdieping) {
        if (!verzoeken.contains(verdieping)) verzoeken.add(verdieping);
    }

    public int getHuidigeVerdieping() { return huidigeVerdieping; }
    public List<Integer> getVerzoeken() { return verzoeken; }
}
