package Model;

import Model.layout.Vakje;
import Model.persoon.Persoon;

// Verantwoordelijkheid: brandalarm activeren en alle personen naar de uitgang sturen
// Gebruikt polymorfisme via evacueer() zodat elke persoon zijn eigen gedrag bepaalt
// Gast wist zijn route en loopt naar buiten
// Schoonmaker onthoudt zijn kamer en loopt ook naar buiten
public class BrandalarmService {

    private Hotel hotel;
    private ILogger logger;

    // bewaar uitgang zodat nieuwe personen hem ook gebruiken
    private Vakje uitgang;

    public BrandalarmService(Hotel hotel, ILogger logger) {
        this.hotel = hotel;
        this.logger = logger;
    }

    // Activeer het brandalarm onmiddellijk.
    // Dit is de eerste omschakeling: lift uit, uitgang bepalen, iedereen naar buiten sturen.
    public void activeer(int tijd) {
        // markeer het alarm als actief in het hotel
        hotel.brandalarmActief = true;

        // zet de lift buiten gebruik zodat niemand er meer in kan
        if (hotel.lift != null) {
            hotel.lift.zetUitBedrijf(true);
        }

        // zoek de uitgang eenmalig op zodat we hem niet per persoon opnieuw hoeven te zoeken
        this.uitgang = vindUitgang();

        // roep evacueer() aan op elke persoon
        // elke subklasse beslist zelf wat er extra gebeurt via @Override
        for (Persoon p : hotel.personen) {
            // gasten die wachten op de lift of in de lift zitten: reset lift-status eerst
            if (p instanceof Model.persoon.Gast) {
                Model.persoon.Gast g = (Model.persoon.Gast) p;
                if (g.wachtOpLift || g.inLift) {
                    g.wachtOpLift = false;
                    g.gebruiktLift = false;
                    // als in de lift: zet op het lift-vakje zodat evacueer() werkt
                    if (g.inLift && hotel.lift != null) {

                        // bepaal het vakje direct naast de lift waar de gast moet uitstappen
                        int uitstapX = hotel.lift.posX + 1;
                        int uitstapY = hotel.lift.getHuidigeVerdieping();
                        // haal het vakje uit het layout-systeem
                        Model.layout.Vakje uitstapVakje = hotel.layout.krijgVakje(uitstapX, uitstapY);
                        // alleen verdergaan als het uitstapvakje bestaat
                        if (uitstapVakje != null) {
                            // verwijder gast eerst uit het huidige vakje (oude positie opruimen)
                            if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
                            // update de nieuwe positie van de gast
                            g.huidigVakje = uitstapVakje;
                            // plaats de gast op het nieuwe vakje
                            uitstapVakje.voegPersoonToe(g);
                        }

                        // gast zit niet meer in de lift
                        g.inLift = false;
                    }
                }
            }
            p.evacueer(uitgang, hotel.pathfinder);
        }

        if (logger != null) logger.log("[" + tijd + "] BRANDALARM: iedereen evacueren via de trap!");
    }

    // Als tijdens een actief alarm nog een persoon aan het hotel toegevoegd wordt,
    // krijgt die hier meteen dezelfde evacuatieroute als iedereen anders.
    public void evacueerNieuwePersoon(Persoon p) {
        if (hotel.brandalarmActief && uitgang != null) {
            p.evacueer(uitgang, hotel.pathfinder);
        }
    }

    // zoek het uitgang vakje: de "buiten"-rij direct onder de lobby (y = lobby.posY - 1)
    // personen wachten hier tijdens brandalarm, buiten het gebouw
    public Vakje vindUitgang() {
        if (hotel.lobby == null) return null;
        //vanuit de lobby -1 geeft de rij eronder voor evacuatie
        int buitenY = hotel.lobby.posY - 1;
        // midden van de breedte van de lobby
        int midX = hotel.lobby.posX + hotel.lobby.breedte / 2;
        return hotel.layout.krijgVakje(midX, buitenY);
    }



}
