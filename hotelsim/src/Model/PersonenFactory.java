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

    // maak de standaardset schoonmakers voor een hotel en voeg ze direct toe
    public void maakStandaardSchoonmakers(Hotel hotel, int gridBreedte, int gridHoogte, int lobbyPosY) {
        if (hotel == null || hotel.layout == null || hotel.pathfinder == null) return;

        // schoonmaker 1: wacht in de lobby
        Vakje wachtVakjeLinks = hotel.layout.krijgVakje(Math.max(2, gridBreedte / 2 - 1), lobbyPosY);
        Schoonmaker schoonmakerCheckOut = maakSchoonmaker(hotel.pathfinder, wachtVakjeLinks);
        schoonmakerCheckOut.setWachtVakje(wachtVakjeLinks);
        hotel.voegPersoonToe(schoonmakerCheckOut);

        // schoonmaker 2: start rechtsboven bij de trap
        Vakje wachtVakjeRechts = hotel.layout.krijgVakje(gridBreedte - 1, gridHoogte - 1);
        Schoonmaker schoonmakerNood = maakSchoonmaker(hotel.pathfinder, wachtVakjeRechts);
        schoonmakerNood.setWachtVakje(wachtVakjeRechts);
        schoonmakerNood.setNoodSchoonmaker(true);
        hotel.voegPersoonToe(schoonmakerNood);
    }
}
