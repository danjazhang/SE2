package Model.service;

import Model.Hotel;
import Model.Pathfinder;
import Model.PersonenFactory;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.Kamer;
import Model.ruimte.Ruimte;

import java.util.List;

// Verantwoordelijkheid: alle businesslogica voor een check-in uitvoeren.
// De service gebruikt daarvoor andere klassen, zoals PersonenFactory en Pathfinder,
// maar bewaart zelf de volledige volgorde van de check-in use-case.
public class CheckInService {

    // Het hotelmodel is nodig om kamers, layout en personen te kunnen gebruiken.
    private final Hotel hotel;
    // De service gebruikt de baliepositie als vast startpunt voor nieuwe gasten.
    private final int balieX;

    public CheckInService(Hotel hotel, int balieX) {
        this.hotel = hotel;
        this.balieX = balieX;
    }

    // Voer een volledige check-in uit:
    // laat via PersonenFactory een gast aanmaken, voeg hem toe aan het hotel,
    // zoek een vrije kamer en zet daarna de route naar die kamer klaar.
    public CheckInResult checkInGast(int gastId) {
        Vakje startVakje = hotel.layout.krijgVakje(balieX, hotel.hoogte);
        PersonenFactory personenFactory = new PersonenFactory();
        Gast gast = personenFactory.maakGast(gastId, 1, hotel.pathfinder, startVakje);

        hotel.voegPersoonToe(gast);

        // Pas nadat we een vrije kamer gevonden hebben,
        // kunnen we de gast aan die kamer koppelen en via Pathfinder laten lopen.
        Kamer kamer = vindVrijeKamer();
        if (kamer != null) {
            kamer.koppelGast(gast);
            zetRouteNaarKamer(gast, startVakje, kamer);
        }

        return new CheckInResult(gast, kamer);
    }

    // Zoek de eerste kamer die nog niet bezet is en die al schoon is.
    // De service houdt deze keuze lokaal, zodat Lobby dat niet zelf meer hoeft te doen.
    private Kamer vindVrijeKamer() {
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Kamer) {
                Kamer k = (Kamer) r;
                if (!k.isBezet() && k.isSchoon()) return k;
            }
        }
        return null;
    }

    // Gebruik Pathfinder om de route van de balie naar de toegewezen kamer te berekenen.
    // Daarna krijgt de gast zijn eerste doel en alle tussenstappen mee.
    private void zetRouteNaarKamer(Gast gast, Vakje startVakje, Kamer kamer) {
        Vakje doel = hotel.layout.krijgVakje(kamer.posX, kamer.posY);
        if (startVakje == null || doel == null) return;

        Pathfinder pathfinder = new Pathfinder(hotel);
        List<Vakje> route = pathfinder.berekenRoute(startVakje, doel);
        if (route.isEmpty()) return;

        gast.zetDoel(route.get(0));
        for (int i = 1; i < route.size(); i++) {
            gast.voegTussendoelToe(route.get(i));
        }
    }

    // Klein resultaatobject zodat Lobby alleen nog hoeft te loggen en het model te verversen.
    public static class CheckInResult {
        // Bewaar zowel de gast als de gekozen kamer,
        // zodat de caller desnoods verder kan loggen of reageren.
        private final Gast gast;
        private final Kamer kamer;

        public CheckInResult(Gast gast, Kamer kamer) {
            this.gast = gast;
            this.kamer = kamer;
        }

        public Gast getGast() {
            return gast;
        }

        public Kamer getKamer() {
            return kamer;
        }
    }
}
