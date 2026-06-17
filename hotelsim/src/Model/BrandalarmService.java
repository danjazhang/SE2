package Model;

import Model.layout.Vakje;
import Model.persoon.Persoon;

// Verantwoordelijkheid: brandalarm activeren en alle personen naar de uitgang sturen
public class BrandalarmService {

// atributen opgeslagen als variabele 
    private Hotel hotel;
    private ILogger logger;
    private Vakje uitgang;

// constructor die h en l meekrijgt
    public BrandalarmService(Hotel hotel, ILogger logger) {
        // h opgeslagen in de atribuut van dit obj
        this.hotel = hotel;
        this.logger = logger;
    }

    public void activeer(int tijd) {
        // markeer het alarm als actief in het hotel
        hotel.brandalarmActief = true;

        // als de l bestaat, buitbedrijf
        if (hotel.lift != null) {
            hotel.lift.zetUitBedrijf(true);
        }

        // atr u van dit object krijgt de waaarde die de methode vu teruggeeft
        this.uitgang = vindUitgang();

 // loop door alle p in lijst van hotel
        for (Persoon p : hotel.personen) {
            // als p een gast is 
            if (p instanceof Model.persoon.Gast) {
                //dan p gecast naar gast en opgeslagen als g voor atrib
                Model.persoon.Gast g = (Model.persoon.Gast) p;
                //als de gast wol of inl
                if (g.wachtOpLift || g.inLift) {
                    //atributen op false
                    g.wachtOpLift = false;
                    g.gebruiktLift = false;
                    // als gast in lift en bestaat
                    if (g.inLift && hotel.lift != null) {
                        //coordinaten berekend vakje naast de lift
                        int uitstapX = hotel.lift.posX + 1;
                        int uitstapY = hotel.lift.getHuidigeVerdieping();
                        // haalt vakje op met coordinaten 
                        Model.layout.Vakje uitstapVakje = hotel.layout.krijgVakje(uitstapX, uitstapY);
                        //als v bestata
                        if (uitstapVakje != null) {
                            //als g een hv heeft, verwijder hem daar
                            if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
                            g.huidigVakje = uitstapVakje;
                            uitstapVakje.voegPersoonToe(g);
                        }
                        //gast zit niet meer in de lift
                        g.inLift = false;
                    }
                }
            }
            // evacuuer opgeroept
            p.evacueer(uitgang, hotel.pathfinder);
        }
// als logger bestaat, log 
        if (logger != null) logger.log("[" + tijd + "] BRANDALARM: iedereen evacueren via de trap!");
    }

    public void evacueerNieuwePersoon(Persoon p) {
        //als ba actief is & uitgang bestaat
        if (hotel.brandalarmActief && uitgang != null) {
            //stuur persoon naar de uitgang via ev
            p.evacueer(uitgang, hotel.pathfinder);
        }
    }

// zoek het vakje dat gebruikt wordt als uitgang
    public Vakje vindUitgang() {
        // als lobby niet bestaat geef null terug
        if (hotel.lobby == null) return null;
        // y berkeenn door 1 onder lobby te pakken
        int buitenY = hotel.lobby.posY - 1;
        // midden van de breedte van de lobby
        int midX = hotel.lobby.posX + hotel.lobby.breedte / 2;
        //geeft het vakje op die positie terug
        return hotel.layout.krijgVakje(midX, buitenY);
    }



}
