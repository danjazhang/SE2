package Model;

import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;

// Verantwoordelijkheid: personen beheren
public class PersonenService {

    private Hotel hotel;
    private PersonenFactory factory;

    public PersonenService(Hotel hotel) {
        this.hotel = hotel;
        this.factory = new PersonenFactory();
    }

    // maak een gast aan met gewenste sterren en voeg toe aan hotel
    public Gast maakGast(int gastId, int gewensteSterren, Vakje startVakje) {
        Gast gast = factory.maakGast(gastId, gewensteSterren, hotel.pathfinder, startVakje);
        hotel.voegPersoonToe(gast);
        return gast;
    }

    // zoek een gast op id
    public Gast vindGast(int gastId) {
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                return (Gast) p;
            }
        }
        return null;
    }

    // zoek een vrije schoonmaker
    public Schoonmaker vindVrijeSchoonmaker() {
        for (Persoon p : hotel.personen) {
            if (p instanceof Schoonmaker && !((Schoonmaker) p).bezig) {
                return (Schoonmaker) p;
            }
        }
        return null;
    }

    // geef bij check-out de voorkeur aan de gewone schoonmaker,
    // maar val terug op eender welke vrije schoonmaker als dat nodig is
    public Schoonmaker vindVrijeSchoonmakerVoorCheckOut() {
        Schoonmaker fallback = null;
        for (Persoon p : hotel.personen) {
            if (!(p instanceof Schoonmaker schoonmaker) || schoonmaker.bezig) continue;
            if (!schoonmaker.isNoodSchoonmaker()) return schoonmaker;
            if (fallback == null) fallback = schoonmaker;
        }
        return fallback;
    }

    // geef bij een noodgeval de voorkeur aan de noodschoonmaker,
    // maar val terug op een andere vrije schoonmaker als die al bezig is
    public Schoonmaker vindVrijeSchoonmakerVoorNoodsituatie() {
        Schoonmaker fallback = null;
        for (Persoon p : hotel.personen) {
            if (!(p instanceof Schoonmaker schoonmaker) || schoonmaker.bezig) continue;
            if (schoonmaker.isNoodSchoonmaker()) return schoonmaker;
            if (fallback == null) fallback = schoonmaker;
        }
        return fallback;
    }
}
