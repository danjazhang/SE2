package Model;

import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;

// Verantwoordelijkheid: eenvoudige persoon-gerichte opzoeklogica.
// Deze service houdt zich bewust alleen bezig met het vinden of maken van personen.
// Keuzes zoals "welke schoonmaker moet welke kamer krijgen" horen niet hier,
// maar in SchoonmaakService waar de taaklogica centraal zit.
public class PersonenService {

    // Hotelreferentie zodat we toegang hebben tot de huidige personenlijst.
    private Hotel hotel;

    // Factory die nieuwe persoonsobjecten kan opbouwen.
    private PersonenFactory factory;

    // Constructor: bewaar het hotel en maak meteen een factory aan.
    public PersonenService(Hotel hotel) {
        // Sla het hotel op zodat andere methoden door hotel.personen kunnen lopen.
        this.hotel = hotel;
        // Maak de factory waarmee we later gasten kunnen opbouwen.
        this.factory = new PersonenFactory();
    }

    // Maak een gast aan met een bepaald id, sterrenwens en startpositie.
    public Gast maakGast(int gastId, int gewensteSterren, Vakje startVakje) {
        // Laat de factory de echte gast construeren met pathfinder en startvakje.
        Gast gast = factory.maakGast(gastId, gewensteSterren, hotel.pathfinder, startVakje);
        // Voeg de nieuwe gast direct toe aan het hotelmodel.
        hotel.voegPersoonToe(gast);
        // Geef de nieuwe gast terug zodat de aanroeper er verder mee kan werken.
        return gast;
    }

    // Zoek een gast puur op guest id.
    public Gast vindGast(int gastId) {
        // Doorloop alle personen die op dit moment in het hotel zitten.
        for (Persoon p : hotel.personen) {
            // Controleer of deze persoon een gast is en of het id overeenkomt.
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                // Juiste gast gevonden: geef die meteen terug.
                return (Gast) p;
            }
        }
        // Geen gast met dit id gevonden.
        return null;
    }

    // Zoek gewoon de eerste vrije schoonmaker zonder verdere taakverdeling.
    public Schoonmaker vindVrijeSchoonmaker() {
        // Doorloop opnieuw alle personen van het hotel.
        for (Persoon p : hotel.personen) {
            // We zoeken alleen een schoonmaker die op dit moment niet bezig is.
            if (p instanceof Schoonmaker && !((Schoonmaker) p).bezig) {
                // Eerste vrije kandidaat gevonden: direct teruggeven.
                return (Schoonmaker) p;
            }
        }
        // Er is nu geen vrije schoonmaker beschikbaar.
        return null;
    }
}
