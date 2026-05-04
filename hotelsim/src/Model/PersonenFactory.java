package Model;

import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;

// Verantwoordelijkheid: personen aanmaken
public class PersonenFactory {

    // maak een nieuwe gast aan met pathfinder en startpositie
    public Gast maakGast(int gastId, int gewensteSterren, Pathfinder pathfinder, Vakje startVakje) {
        Gast gast = new Gast(gastId, gewensteSterren);
        gast.setPathfinder(pathfinder);
        if (startVakje != null) gast.zetStartPositie(startVakje);
        return gast;
    }

    // maak een nieuwe schoonmaker aan met pathfinder en startpositie
    public Schoonmaker maakSchoonmaker(Pathfinder pathfinder, Vakje startVakje) {
        Schoonmaker schoonmaker = new Schoonmaker();
        schoonmaker.setPathfinder(pathfinder);
        if (startVakje != null) schoonmaker.zetStartPositie(startVakje);
        return schoonmaker;
    }
}
