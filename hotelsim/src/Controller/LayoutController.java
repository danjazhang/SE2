package Controller;

import Model.*;
import Model.layout.Layout;
import Model.layout.LayoutParser;
import Model.layout.ParseResultaat;
import Model.layout.Vakje;
import Model.persoon.Schoonmaker;
import Model.ruimte.Gang;
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
        ParseResultaat resultaat = new LayoutParser().laad(bestandspad);
        if (resultaat == null) return -1;

        Hotel nieuwHotel = new Hotel();

        // Elke JSON-verdieping (y=1..hoogte-1) krijgt 2 grid-rijen:
        //   - ruimte-rij: de kamers/ruimtes staan hier
        //   - gang-rij:   de gang loopt hier volledig door (x=2..gridBreedte-2)
        // De begane grond (JSON-y=hoogte) wordt de lobby, geen gang.
        //
        // Multi-verdieping ruimtes (hoogte>=2) krijgen in het grid hun JSON-hoogte
        // als grid-hoogte. Ze bezetten ALLEEN de ruimte-rijen, NIET de gang-rijen.
        // De gang-rijen lopen gewoon door naast de ruimte.
        //
        // Mapping: jsonY -> gridY
        // Elke JSON-y-rij (behalve begane grond) krijgt een gang-rij eronder.
        // gridY[1]=1, gang=2, gridY[2]=3, gang=4, gridY[3]=5, gang=6, ...

        int jsonHoogte = resultaat.hoogte; // aantal JSON-rijen incl. begane grond
        int[] jsonYnaarGridY = new int[jsonHoogte + 2];
        int gridY = 1;
        for (int jsonY = 1; jsonY <= jsonHoogte; jsonY++) {
            jsonYnaarGridY[jsonY] = gridY;
            gridY++; // ruimte-rij
            if (jsonY < jsonHoogte) {
                gridY++; // gang-rij (niet voor begane grond)
            }
        }
        // gridY is nu de hoogte van het grid exclusief de lobby
        int gridHoogte = gridY; // lobby op gridY

        int gridBreedte = resultaat.breedte + 3; // +1 lift links, +2 trap rechts

        nieuwHotel.breedte = gridBreedte;
        nieuwHotel.hoogte = gridHoogte;
        nieuwHotel.layout = new Layout(gridBreedte, gridHoogte);

        // kamernummers: onderste kamerlaag in JSON
        int ondersteKamerPosY = 1;
        for (JSONObject obj : resultaat.ruimteData) {
            if (obj.getString("AreaType").equals("Room")) {
                ondersteKamerPosY = Math.max(ondersteKamerPosY, obj.getInt("_posY"));
            }
        }

        RuimteFactory factory = new RuimteFactory(logger, ondersteKamerPosY);

        for (JSONObject obj : resultaat.ruimteData) {
            Ruimte r = factory.maakRuimte(obj.getString("AreaType"), obj);
            int jsonX   = obj.getInt("_posX");
            int jsonY   = obj.getInt("_posY");
            int breedte = obj.getInt("_breedte");
            int hoogte  = obj.getInt("_hoogte");

            r.posX   = jsonX + 1;                  // +1 voor lift
            r.posY   = jsonYnaarGridY[jsonY];       // ruimte-rij in grid
            r.breedte = breedte;
            r.hoogte  = hoogte;                     // EXACT JSON-hoogte, geen gang-rijen ertussen

            // ingang = linksonder van de ruimte
            r.setIngang(r.posX, r.posY + r.hoogte - 1);

            nieuwHotel.ruimtes.add(r);
            nieuwHotel.layout.plaatsRuimte(r);
        }

        // lift (loopt door alle grid-rijen)
        Lift lift = new Lift(nieuwHotel);
        lift.posX = 1; lift.posY = 1;
        lift.breedte = 1; lift.hoogte = gridHoogte;
        nieuwHotel.lift = lift;
        nieuwHotel.ruimtes.add(lift);
        nieuwHotel.layout.plaatsRuimte(lift);

        // trap (loopt door alle grid-rijen)
        Trap trap = new Trap(3);
        trap.posX = gridBreedte - 1; trap.posY = 1;
        trap.breedte = 2; trap.hoogte = gridHoogte;
        nieuwHotel.trap = trap;
        nieuwHotel.ruimtes.add(trap);
        nieuwHotel.layout.plaatsRuimte(trap);

        // lobby (begane grond)
        Lobby lobby = new Lobby(2, gridHoogte, gridBreedte - 3, 1,
                gridBreedte / 2, gridHoogte, nieuwHotel, logger);
        nieuwHotel.lobby = lobby;
        nieuwHotel.ruimtes.add(lobby);
        nieuwHotel.layout.plaatsRuimte(lobby);

        // gangen: elke gang-rij (gridY van jsonY + 1) krijgt gang-vakjes op alle
        // lege x-posities (x=2..gridBreedte-2). Lift en trap worden niet overschreven.
        // Multi-verdieping ruimtes bezetten alleen hun ruimte-rijen, dus de gang-rij
        // eronder is altijd leeg en krijgt gewoon een gang-vakje.
        for (int jsonY = 1; jsonY < jsonHoogte; jsonY++) {
            int gangGridY = jsonYnaarGridY[jsonY] + 1;
            for (int x = 2; x <= gridBreedte - 2; x++) {
                Vakje vakje = nieuwHotel.layout.krijgVakje(x, gangGridY);
                if (vakje != null && vakje.ruimte == null) {
                    Gang gangVakje = new Gang(gangGridY);
                    gangVakje.posX = x;
                    gangVakje.posY = gangGridY;
                    gangVakje.breedte = 1;
                    gangVakje.hoogte = 1;
                    vakje.ruimte = gangVakje;
                    nieuwHotel.ruimtes.add(gangVakje);
                }
            }
        }

        nieuwHotel.pathfinder = new Pathfinder(nieuwHotel);

        // schoonmaker wacht bij de trap op de begane grond
        PersonenFactory personenFactory = new PersonenFactory();
        Vakje wachtVakje = nieuwHotel.layout.krijgVakje(gridBreedte - 1, gridHoogte);
        Schoonmaker schoonmaker = personenFactory.maakSchoonmaker(nieuwHotel.pathfinder, wachtVakje);
        schoonmaker.setWachtVakje(wachtVakje);
        nieuwHotel.voegPersoonToe(schoonmaker);

        int id = hotelManager.addLayout(bestandsnaam, nieuwHotel.layout);
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
