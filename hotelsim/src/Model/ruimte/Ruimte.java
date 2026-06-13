package Model.ruimte;

import Model.persoon.Persoon;

import java.util.ArrayList;
import java.util.List;

// Verantwoordelijkheid: basisklasse voor alle ruimtes in het hotel.
// Kamer, Restaurant, Bioscoop, Fitnessruimte en Lobby erven van deze klasse via 'extends Ruimte'.
// Ruimte slaat positie, afmetingen en aanwezige personen op.
public class Ruimte {

    // De x-positie van deze ruimte op het grid (kolom).
    public int posX;

    // De y-positie van deze ruimte op het grid (rij).
    public int posY;

    // Het aantal vakjes breed dat deze ruimte is.
    public int breedte;

    // Het aantal vakjes hoog dat deze ruimte is.
    public int hoogte;

    // De x-positie van de ingang van de ruimte op het grid.
    private int ingangX;

    // De y-positie van de ingang van de ruimte op het grid.
    private int ingangY;

    // De lijst van personen die momenteel in de ruimte zijn.
    // 'List<Persoon>' betekent: een lijst die alleen Persoon-objecten mag bevatten.
    private List<Persoon> aanwezigen;

    // Constructor met positie en afmetingen: sla alle waarden op en maak een lege aanwezigenlijst.
    public Ruimte(int posX, int posY, int breedte, int hoogte) {
        this.posX = posX;
        this.posY = posY;
        this.breedte = breedte;
        this.hoogte = hoogte;
        this.aanwezigen = new ArrayList<>();
    }

    // Lege constructor voor subklassen die hun eigen constructor willen gebruiken.
    // Maakt wel alvast een lege aanwezigenlijst aan.
    public Ruimte() {
        this.aanwezigen = new ArrayList<>();
    }

    // Setters en getters: methoden om private variabelen van buitenaf in te stellen of op te vragen.
    public void setPositie(int x, int y) { this.posX = x; this.posY = y; }
    public void setAfmetingen(int b, int h) { this.breedte = b; this.hoogte = h; }
    public int getX() { return posX; }
    public int getY() { return posY; }
    public int getBreedte() { return breedte; }
    public int getHoogte() { return hoogte; }

    // Voeg persoon p toe aan de aanwezigenlijst. Wordt aangeroepen als iemand de ruimte betreedt.
    public void betreed(Persoon p) { aanwezigen.add(p); }

    // Verwijder persoon p uit de aanwezigenlijst. Wordt aangeroepen als iemand de ruimte verlaat.
    public void verlaat(Persoon p) { aanwezigen.remove(p); }

    // Geef een kopie van de aanwezigenlijst terug.
    // 'new ArrayList<>(aanwezigen)' maakt een nieuwe lijst met dezelfde inhoud,
    // zodat de aanroeper de originele lijst niet per ongeluk kan aanpassen.
    public List<Persoon> getAanwezigen() {
        return new ArrayList<>(aanwezigen);
    }

    // Sla de ingangspositie op in ingangX en ingangY.
    public void setIngang(int x, int y) { this.ingangX = x; this.ingangY = y; }

    // Geef de ingangspositie terug als een array met twee getallen: [x, y].
    // 'new int[]{ingangX, ingangY}' maakt een nieuwe int-array van twee elementen.
    public int[] krijgIngang() { return new int[]{ingangX, ingangY}; }

    // Standaard geeft een ruimte geen vrije kamer terug: return null.
    // Kamer overschrijft deze methode via '@Override' zodat hij zichzelf teruggeeft als hij vrij en schoon is.
    public Kamer getVrijeKamer() { return null; }
}
