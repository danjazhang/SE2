package Model;

import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;

// Verantwoordelijkheid: nieuwe personen aanmaken en direct klaarzettten met pathfinder en startpositie.
// De factory zorgt ervoor dat de aanroeper niet zelf hoeft te weten hoe een Gast of Schoonmaker opgezet wordt.
public class PersonenFactory {

    // Maak een nieuwe Gast aan: geef hem een id, sterren, pathfinder en startpositie.
    // Als startVakje niet leeg is (niet null), zet de gast dan op dat vakje.
    public Gast maakGast(int gastId, int gewensteSterren, Pathfinder pathfinder, Vakje startVakje) {
        Gast gast = new Gast(gastId, gewensteSterren);
        gast.setPathfinder(pathfinder);
        // Als startVakje niet leeg is (null), roep dan zetStartPositie() aan.
        if (startVakje != null) gast.zetStartPositie(startVakje);
        return gast;
    }

    // Maak een nieuwe Schoonmaker aan: geef hem een pathfinder en startpositie.
    // Als startVakje niet leeg is (niet null), zet de schoonmaker dan op dat vakje.
    public Schoonmaker maakSchoonmaker(Pathfinder pathfinder, Vakje startVakje) {
        Schoonmaker schoonmaker = new Schoonmaker();
        schoonmaker.setPathfinder(pathfinder);
        if (startVakje != null) schoonmaker.zetStartPositie(startVakje);
        return schoonmaker;
    }
}
