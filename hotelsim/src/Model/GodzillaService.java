package Model;

import Model.persoon.Gast;
import Model.persoon.Persoon;

// Verantwoordelijkheid: het Godzilla-event afhandelen.
// Elke keer dat behandel() wordt aangeroepen, brandt de volgende kolom.
public class GodzillaService {

    //atributen die objecten opslaat
    private Hotel hotel;
    private ILogger logger;
    private int volgendeKolom = 1;

    // Constructor: sla het hotel en de logger op.
    public GodzillaService(Hotel hotel, ILogger logger) {
        this.hotel = hotel;
        this.logger = logger;
    }

    // Start de volledige Godzilla-aanval.
    public void start(int tijd) {
        hotel.godzillaActief = true;
        brandKolom(tijd);
        if (logger != null) logger.log("[" + tijd + "] GODZILLA: aanval gestart! Vuur breidt zich uit van links naar rechts.");
    }

    // Breid het vuur één kolom verder uit.
    // Deze methode wordt daarna bij elke tick opnieuw aangeroepen zolang Godzilla actief blijft.
    public void behandel(int currentHTE) {
        // als vk groeter is dan b, zijn alle kolommen in brandt. stop.
        if (volgendeKolom > hotel.breedte) return;
        // anders wordt bk aangeroepen
        brandKolom(currentHTE);
    }

    // Zet precies één nieuwe kolom in brand en markeer direct alle slachtoffers op die kolom.
    private void brandKolom(int tijd) {
        //sla de huidge waarde van vk op in k
        int kolom = volgendeKolom;
        //verhoog vk met 1, zodat de volgende aanroep de volgende kolom pakt
        volgendeKolom++;
        // Voeg de kolom toe aan de set van brandende kolommen in het hotel.
        hotel.brandendeKolommen.add(kolom);
        // als de logger bestaat, log..
        if (logger != null) logger.log("[" + tijd + "] GODZILLA: kolom " + kolom + " staat in brand!");
        //methode opgeroepen
        markeerDodenOpKolom(kolom, tijd);
        // als lift bestaat en de xpos gelijk is aan brandende kolom
        if (hotel.lift != null && hotel.lift.posX == kolom) {
            markeerLiftPassagiers(tijd);
        }
    }

    // Loop door alle personen en markeer iedereen op de opgegeven kolom als gestorven.
    public void markeerDodenOpKolom(int kolom, int tijd) {
        //loop door alle p in h
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
    private void markeerLiftPassagiers(int tijd) {
        //loop door alle in de lift
        for (Gast g : hotel.lift.getPassagiers()) {
            // als de gast al gestorven is, sla over
            if (g.gestorven) continue;
            g.gestorven = true;
            if (logger != null) logger.log("[" + tijd + "] GODZILLA: liftpassagier omgekomen in brandende schacht.");
        }
    }
    //Als het hotel volledig afgebrand is, stopt de simulatie
    //Geef terug of vk groter is dan b. Als dat waar is geeft de methode true terug, anders false
    public boolean isKlaar() {
        return volgendeKolom > hotel.breedte;
    }
}
