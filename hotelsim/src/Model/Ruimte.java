package Model;

import java.util.ArrayList;
import java.util.List;

// Basisklasse voor alle ruimtes in het hotel
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
    public Ruimte() {}

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

    // sla de ingang positie op
    public void setIngang(int x, int y) { this.ingangX = x; this.ingangY = y; }

    // geef de ingang positie terug als array [x, y]
    public int[] krijgIngang() { return new int[]{ingangX, ingangY}; }
}
