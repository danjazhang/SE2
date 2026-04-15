package Model;

import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;

// Verantwoordelijkheid: personen aanmaken
public class PersonenFactory {

    // maak een nieuwe gast aan met layout en startpositie
    public Gast maakGast(int gastId, int gewensteSterren, Layout layout, Vakje startVakje) {
        Gast gast = new Gast(gastId, gewensteSterren);
        gast.layout = layout;
        if (startVakje != null) gast.zetStartPositie(startVakje);
        return gast;
    }

    // maak een nieuwe schoonmaker aan met layout en startpositie
    public Schoonmaker maakSchoonmaker(Layout layout, Vakje startVakje) {
        Schoonmaker schoonmaker = new Schoonmaker();
        schoonmaker.layout = layout;
        if (startVakje != null) schoonmaker.zetStartPositie(startVakje);
        return schoonmaker;
    }
}
