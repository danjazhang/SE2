package Model.ruimte;

import Model.persoon.Persoon;

import java.util.ArrayList;
import java.util.List;


// Kamer, Restaurant, Bioscoop, Fitnesruimte en Lobby erven van deze klasse
public class Ruimte {

    // positie in het grid
    public int posX;
    public int posY;

    // afmetingen in vakjes
    public int breedte;
    public int hoogte;

    // positie van de ingang van de ruimte
    private int ingangX;
    private int ingangY;

    // lijst van personen die momenteel in de ruimte zijn
    private List<Persoon> aanwezigen;

    // constructor met positie en afmetingen
    public Ruimte(int posX, int posY, int breedte, int hoogte) {
        this.posX = posX;
        this.posY = posY;
        this.breedte = breedte;
        this.hoogte = hoogte;
        this.aanwezigen = new ArrayList<>();
    }

    // lege constructor voor subklassen
    public Ruimte() {
        this.aanwezigen = new ArrayList<>();
    }

    public void setPositie(int x, int y) { this.posX = x; this.posY = y; }
    public void setAfmetingen(int b, int h) { this.breedte = b; this.hoogte = h; }
    public int getX() { return posX; }
    public int getY() { return posY; }
    public int getBreedte() { return breedte; }
    public int getHoogte() { return hoogte; }

    // voeg een persoon toe aan de ruimte
    public void betreed(Persoon p) { aanwezigen.add(p); }

    // verwijder een persoon uit de ruimte
    public void verlaat(Persoon p) { aanwezigen.remove(p); }

    //geef alle aanwezigen terug
    public List<Persoon> getAanwezigen(){
        return new ArrayList<>(aanwezigen);
    }

    // geef deze ruimte terug als vrije kamer, standaard null
    // Kamer overschrijft dit als die vrij en schoon is
    public Kamer getVrijeKamer() { return null; }

    // geef de ingang positie terug als array [x, y]
    public int[] krijgIngang() { return new int[]{ingangX, ingangY}; }

    // sla de ingang positie op
    public void setIngang(int x, int y) { this.ingangX = x; this.ingangY = y; }

    // geef een statustekst terug voor het lobbyscherm
    // subklassen overschrijven dit om hun eigen status te tonen
    public String getStatusTekst() {
        return "";
    }

    // geeft true als deze ruimte een kamer is — Kamer overschrijft dit
    public boolean isKamer() {
        return false;
    }

    // geeft true als deze ruimte een faciliteit is — Restaurant/Fitness/Bioscoop overschrijven dit
    public boolean isFaciliteit() {
        return false;
    }

    // stel de filmduur in — Bioscoop overschrijft dit
    public void setFilmDuur(int duur) {}

    // stel de traptijd in — Trap overschrijft dit
    public void setTijdPerVerdieping(int duur) {}

    // geef een leesbare naam terug — subklassen overschrijven dit voor specifieke namen
    public String getNaam() {
        return getClass().getSimpleName().toLowerCase();
    }
}
