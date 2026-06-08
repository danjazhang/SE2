package Controller;

import Model.*;
import Model.layout.Layout;
import Model.layout.LayoutParser;
import Model.layout.ParseResultaat;
import Model.layout.Vakje;
import Model.persoon.Schoonmaker;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Ruimte;
import Model.ruimte.Trap;
import org.json.JSONObject;

// Verantwoordelijkheid: layouts laden en opslaan
public class LayoutController {

    // beheert alle geladen hotels
    private HotelManager hotelManager = new HotelManager();

    private ILogger logger;

    // laad een nieuw hotel vanuit een JSON bestand
    public int laadVanBestand(String bestandspad, String bestandsnaam) {
        //lees het JSON bestand via de parser
        ParseResultaat resultaat = new LayoutParser().laad(bestandspad);
        //als laden mislukt geef -1 terug, want id begint bij 1
        if (resultaat == null) return -1;

        //maak nieuwe hotel
        Hotel nieuwHotel = new Hotel();

        // grid groter maken:
        // +3 voor lift (links) en trap (rechts, 2 breed)
        // +2 voor lobby (y=1, onderaan) en extra ruimte bovenaan
        // JSON-ruimtes krijgen posY+1 zodat lobby op y=1 kan zitten
        int gridBreedte = resultaat.breedte + 3;
        int gridHoogte = resultaat.hoogte + 2; // +1 voor lobby op y=1, +1 voor originele marge

        nieuwHotel.breedte = gridBreedte;
        nieuwHotel.hoogte = gridHoogte;
        nieuwHotel.layout = new Layout(gridBreedte, gridHoogte);

        // zoek de hoogste kamer-y in de JSON (na +1 offset) voor kamernummering
        int ondersteKamerPosY = 1;
        for (JSONObject obj : resultaat.ruimteData) {
            if (obj.getString("AreaType").equals("Room")) {
                ondersteKamerPosY = Math.max(ondersteKamerPosY, obj.getInt("_posY") + 1);
            }
        }

        // maak ruimtes aan; posY krijgt +1 zodat ze boven de lobby (y=1) vallen
        RuimteFactory factory = new RuimteFactory(logger, ondersteKamerPosY);

        for (JSONObject obj : resultaat.ruimteData) {
            Ruimte r = factory.maakRuimte(obj.getString("AreaType"), obj);
            r.posX = obj.getInt("_posX") + 1; // ruimte voor lift
            r.posY = obj.getInt("_posY") + 1; // +1 zodat lobby op y=1 past
            r.breedte = obj.getInt("_breedte");
            r.hoogte = obj.getInt("_hoogte");
            nieuwHotel.ruimtes.add(r);
            nieuwHotel.layout.plaatsRuimte(r);
        }

        // lobby op y=1 — de onderste rij
        // lift en trap starten op y=2 (boven de lobby) en lopen door tot gridHoogte
        int lobbyPosY = 1;
        int kamersStartY = 2; // eerste y-rij met kamers, direct boven de lobby

        Lift lift = new Lift(nieuwHotel);
        lift.posX = 1;
        lift.posY = kamersStartY;        // begint bij de eerste kamerrij, niet bij de lobby
        lift.breedte = 1;
        lift.hoogte = gridHoogte - 1;    // van y=2 t/m y=gridHoogte
        lift.initWachtrijen(gridHoogte);
        lift.setLobbyVerdieping(lobbyPosY); // lift start bij de lobby (y=1)
        nieuwHotel.lift = lift;
        nieuwHotel.ruimtes.add(lift);
        nieuwHotel.layout.plaatsRuimte(lift);

        Trap trap = new Trap(3);
        trap.posX = gridBreedte - 1;
        trap.posY = kamersStartY;        // begint bij de eerste kamerrij, niet bij de lobby
        trap.breedte = 2;
        trap.hoogte = gridHoogte - 1;    // van y=2 t/m y=gridHoogte
        nieuwHotel.trap = trap;
        nieuwHotel.ruimtes.add(trap);
        nieuwHotel.layout.plaatsRuimte(trap);

        // lobby: posY=1, breedte van x=1 t/m trap (inclusief lift-kolom)
        Lobby lobby = new Lobby(1, lobbyPosY, gridBreedte - 2, 1, gridBreedte / 2, lobbyPosY, nieuwHotel, logger);
        nieuwHotel.lobby = lobby;
        nieuwHotel.ruimtes.add(lobby);
        nieuwHotel.layout.plaatsRuimte(lobby);

        nieuwHotel.pathfinder = new Pathfinder(nieuwHotel);

        // schoonmakers starten in de lobby (y=1)
        PersonenFactory personenFactory = new PersonenFactory();
        Vakje wachtVakjeLinks = nieuwHotel.layout.krijgVakje(Math.max(2, gridBreedte / 2 - 1), lobbyPosY);
        Schoonmaker schoonmakerCheckOut = personenFactory.maakSchoonmaker(nieuwHotel.pathfinder, wachtVakjeLinks);
        schoonmakerCheckOut.setWachtVakje(wachtVakjeLinks);
        nieuwHotel.voegPersoonToe(schoonmakerCheckOut);

        Vakje wachtVakjeRechts = nieuwHotel.layout.krijgVakje(Math.min(gridBreedte - 2, gridBreedte / 2 + 1), lobbyPosY);
        Schoonmaker schoonmakerNood = personenFactory.maakSchoonmaker(nieuwHotel.pathfinder, wachtVakjeRechts);
        schoonmakerNood.setWachtVakje(wachtVakjeRechts);
        schoonmakerNood.setNoodSchoonmaker(true);
        nieuwHotel.voegPersoonToe(schoonmakerNood);

        //sla de layout op in hotelmanager met bestandsnaam als naam
        int id = hotelManager.addLayout(bestandsnaam, nieuwHotel.layout);
        //sla het hele hotel object op in hotelmanager met zelfde id als sleutel
        hotelManager.loadHotel(id, nieuwHotel);
        return id;
    }

    // maak handmatig een lege layout aan
    public int maakHandmatigeLayout(String naam, int breedte, int hoogte) {
        //maak nieuw lege hotel
        Hotel nieuwHotel = new Hotel();
        //maak lege grid
        nieuwHotel.layout = new Layout(breedte, hoogte);
        //sla afmetingen op in hotel
        nieuwHotel.breedte = breedte;
        nieuwHotel.hoogte = hoogte;
        //sla layout op met opgegeven naam en krijg id terug
        int id = hotelManager.addLayout(naam, nieuwHotel.layout);
        //sla hotel op met zelfde id
        hotelManager.loadHotel(id, nieuwHotel);
        return id;
    }

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    // geef een hotel terug op basis van id
    public Hotel getHotel(int id) {
        return hotelManager.getHotel(id);
    }

    // geef de hotelmanager terug
    public HotelManager getHotelManager() {
        return hotelManager;
    }
}
