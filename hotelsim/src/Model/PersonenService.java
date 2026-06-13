package Model;

import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;

// Verantwoordelijkheid: personen aanmaken en opzoeken in het hotel.
// PersonenService is de tussenlaag tussen de controllers en de PersonenFactory.
public class PersonenService {

    // Het hotel waar de personen in staan.
    private Hotel hotel;

    // De factory die nieuwe personen aanmaakt.
    private PersonenFactory factory;

    // Constructor: sla het hotel op en maak een nieuwe factory aan.
    public PersonenService(Hotel hotel) {
        this.hotel = hotel;
        this.factory = new PersonenFactory();
    }

    // Maak een nieuwe gast aan via de factory, voeg hem toe aan het hotel en geef hem terug.
    public Gast maakGast(int gastId, int gewensteSterren, Vakje startVakje) {
        Gast gast = factory.maakGast(gastId, gewensteSterren, hotel.pathfinder, startVakje);
        hotel.voegPersoonToe(gast);
        return gast;
    }

    // Zoek een gast op via zijn gastId door de personenlijst door te lopen.
    // 'p instanceof Gast' betekent: als de persoon een Gast is.
    // '((Gast) p).gastId == gastId' betekent: het gastId is gelijk aan het gezochte id.
    // Als de gast niet gevonden wordt, geef null terug.
    public Gast vindGast(int gastId) {
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                return (Gast) p;
            }
        }
        return null;
    }

    // Zoek een vrije schoonmaker: loop door de personenlijst en geef de eerste schoonmaker terug
    // die een Schoonmaker is én niet bezig is (bezig is gelijk aan false).
    // Als er geen vrije schoonmaker is, geef null terug.
    public Schoonmaker vindVrijeSchoonmaker() {
        for (Persoon p : hotel.personen) {
            if (p instanceof Schoonmaker && !((Schoonmaker) p).bezig) {
                return (Schoonmaker) p;
            }
        }
        return null;
    }

    // Zoek een vrije schoonmaker voor check-outtaken.
    // Geeft de voorkeur aan de gewone schoonmaker (isNoodSchoonmaker is false).
    // Als alleen de noodschoonmaker vrij is, wordt die als fallback gebruikt.
    public Schoonmaker vindVrijeSchoonmakerVoorCheckOut() {
        Schoonmaker fallback = null;
        for (Persoon p : hotel.personen) {
            // 'continue' betekent: sla deze iteratie over en ga verder met de volgende.
            // Als p geen Schoonmaker is of als hij bezig is, sla hem dan over.
            if (!(p instanceof Schoonmaker schoonmaker) || schoonmaker.bezig) continue;
            // Als hij geen noodschoonmaker is, geef hem dan meteen terug.
            if (!schoonmaker.isNoodSchoonmaker()) return schoonmaker;
            // Anders bewaar hem als fallback als er nog geen fallback is.
            if (fallback == null) fallback = schoonmaker;
        }
        return fallback;
    }

    // Zoek een vrije schoonmaker voor noodsituaties.
    // Geeft de voorkeur aan de noodschoonmaker (isNoodSchoonmaker is true).
    // Als die bezig is, wordt de gewone schoonmaker als fallback gebruikt.
    public Schoonmaker vindVrijeSchoonmakerVoorNoodsituatie() {
        Schoonmaker fallback = null;
        for (Persoon p : hotel.personen) {
            if (!(p instanceof Schoonmaker schoonmaker) || schoonmaker.bezig) continue;
            // Als hij een noodschoonmaker is, geef hem dan meteen terug.
            if (schoonmaker.isNoodSchoonmaker()) return schoonmaker;
            if (fallback == null) fallback = schoonmaker;
        }
        return fallback;
    }
}
