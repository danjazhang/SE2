package Model;

import Model.persoon.Persoon;

// Verantwoordelijkheid: het Godzilla-event afhandelen.
// Het hotel brandt kolom voor kolom van links naar rechts.
// Elke keer dat behandel() wordt aangeroepen, brandt de volgende kolom.
// Personen op een brandende kolom worden gemarkeerd als gestorven (gestorven = true).
// De echte verwijdering gebeurt pas aan het einde van de tick in SimulatieController - nooit hier.
//
// Bewuste keuze: pathfinding houdt geen rekening met brand.
// Personen lopen gewoon door brandende kolommen en sterven dan meteen.
//
// Liftpassagiers worden op drie manieren gecontroleerd:
//   1. IN_SHAFT: de liftschachtkolom brandt - iedereen in de schacht sterft.
//   2. IN_LIFT: passagiers in de cabine worden altijd via hotel.lift.getPassagiers() gecontroleerd.
//   3. ENTER/EXIT: personen die net instappen of uitstappen staan op hun huidigVakje - ook gecontroleerd.
public class GodzillaService {

    // Het hotel dat aangevallen wordt.
    private Hotel hotel;

    // Logger voor het sturen van berichten naar de GUI.
    private ILogger logger;

    // De volgende kolom die in brand gezet wordt bij de volgende aanroep van behandel().
    // Begint bij kolom 1 (de meest linkse kolom).
    private int volgendeKolom = 1;

    // Constructor: sla het hotel en de logger op.
    public GodzillaService(Hotel hotel, ILogger logger) {
        this.hotel = hotel;
        this.logger = logger;
    }

    // Start de volledige Godzilla-aanval.
    // Deze methode wordt exact één keer aangeroepen op het moment dat het GODZILLA-event binnenkomt.
    public void start(int tijd) {
        // Zet godzillaActief op true zodat routing-events worden genegeerd.
        hotel.godzillaActief = true;
        // Zet de eerste kolom al in brand zodat er meteen iets te zien is.
        brandKolom(tijd);
        if (logger != null) logger.log("[" + tijd + "] GODZILLA: aanval gestart! Vuur breidt zich uit van links naar rechts.");
    }

    // Breid het vuur één kolom verder uit.
    // Deze methode wordt daarna bij elke tick opnieuw aangeroepen zolang Godzilla actief blijft.
    public void behandel(int currentHTE) {
        // Als alle kolommen al branden, stop dan: het hotel is volledig afgebrand.
        if (volgendeKolom > hotel.breedte) return;

        brandKolom(currentHTE);
    }

    // Zet precies één nieuwe kolom in brand en markeer direct alle slachtoffers op die kolom.
    private void brandKolom(int tijd) {
        int kolom = volgendeKolom;
        volgendeKolom++;

        // Voeg de kolom toe aan de set van brandende kolommen in het hotel.
        hotel.brandendeKolommen.add(kolom);

        if (logger != null) logger.log("[" + tijd + "] GODZILLA: kolom " + kolom + " staat in brand!");

        // Markeer alle personen op deze kolom als gestorven.
        markeerDodenOpKolom(kolom, tijd);

        // Controleer ook de liftpassagiers apart als de liftschacht op deze kolom staat.
        // hotel.lift.posX wordt dynamisch opgehaald - nooit hardcoded.
        if (hotel.lift != null && hotel.lift.posX == kolom) {
            markeerLiftPassagiers(tijd);
        }
    }

    // Loop door alle personen en markeer iedereen op de opgegeven kolom als gestorven.
    // Gestorven personen worden niet opnieuw gemarkeerd (gestorven is al true).
    // De persoon wordt hier NIET verwijderd - dat gebeurt pas aan het einde van de tick.
    public void markeerDodenOpKolom(int kolom, int tijd) {
        for (Persoon p : hotel.personen) {
            // Als de persoon al gestorven is, sla hem dan over.
            if (p.gestorven) continue;
            // Als de persoon geen positie heeft, sla hem dan over.
            if (p.huidigVakje == null) continue;
            // Als de x-positie van het huidige vakje gelijk is aan de brandende kolom, markeer dan als gestorven.
            if (p.huidigVakje.x == kolom) {
                p.gestorven = true;
                if (logger != null) logger.log("[" + tijd + "] GODZILLA: persoon op kolom " + kolom + " is omgekomen.");
            }
        }
    }

    // Controleer alle passagiers die momenteel in de liftcabine zitten.
    // De cabine beweegt door de schacht - als de schachtkolom brandt, sterven zij ook.
    // hotel.lift.getPassagiers() geeft altijd de actuele lijst terug.
    private void markeerLiftPassagiers(int tijd) {
        for (Model.persoon.Gast g : hotel.lift.getPassagiers()) {
            if (g.gestorven) continue;
            g.gestorven = true;
            if (logger != null) logger.log("[" + tijd + "] GODZILLA: liftpassagier omgekomen in brandende schacht.");
        }
    }

    // Geef terug of het hotel al volledig afgebrand is: alle kolommen staan in brand.
    public boolean isKlaar() {
        return volgendeKolom > hotel.breedte;
    }
}
