package Model.layout;

import Model.ruimte.Ruimte;

// Stelt de plattegrond van het hotel voor als een 2D grid van vakjes
// Elke positie in het grid bevat een Vakje met een verwijzing naar een Ruimte
public class Layout {

    // uniek id van de layout
    public int id;

    // naam van de layout (bijv. het bestandspad)
    public String naam;

    // breedte en hoogte van het grid
    public int breedte;
    public int hoogte;

    // het 2D grid van vakjes, geindexeerd op [x-1][y-1]
    public Vakje[][] vakjes;

    // constructor: maak een leeg grid aan van de opgegeven grootte
    public Layout(int breedte, int hoogte) {
        this.breedte = breedte;
        this.hoogte = hoogte;
        this.vakjes = new Vakje[breedte][hoogte];
        // vul het grid met lege vakjes
        for (int x = 0; x < breedte; x++) {
            for (int y = 0; y < hoogte; y++) {
                vakjes[x][y] = new Vakje();
                // vakjes zijn 1 geindexeerd
                //slaat json positie op in het vakje zodat het zijn eigen locatie kent
                vakjes[x][y].x = x + 1;
                vakjes[x][y].y = y + 1;
            }
        }
    }

    // plaats een ruimte in het grid op basis van de positie en afmetingen van de ruimte
    //ruimte moet al gemaakt zijn in ruimtemaker
    public void plaatsRuimte(Ruimte ruimte) {
        for (int x = ruimte.posX; x < ruimte.posX + ruimte.breedte; x++) {
            for (int y = ruimte.posY; y < ruimte.posY + ruimte.hoogte; y++) {
                if (x <= breedte && y <= hoogte) {
                    //-1 omdat 1,1 in json bestand gelijk moet zijn aan 0,0 in array
                    vakjes[x - 1][y - 1].ruimte = ruimte;
                }
            }
        }
    }

    // geef het vakje op positie (x, y) terug
    // geeft null terug als de positie buiten het grid valt
    public Vakje krijgVakje(int x, int y) {
        if (x >= 1 && x <= breedte && y >= 1 && y <= hoogte) {
            //-1 omdat 1,1 in json bestand gelijk moet zijn aan 0,0 in array
            return vakjes[x - 1][y - 1];
        }
        return null;
    }
}
