package Model;

import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;

// Verantwoordelijkheid: personen beheren, aanmaken en zoeken
public class PersonenService {

    private Hotel hotel;
    private PersonenFactory factory;

    public PersonenService(Hotel hotel) {
        this.hotel = hotel;
        this.factory = new PersonenFactory();
    }

    // maak een gast aan en voeg toe aan hotel
    public Gast maakGast(int gastId, Vakje startVakje) {
        Gast gast = factory.maakGast(gastId, 1, hotel.pathfinder, startVakje);
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
}
