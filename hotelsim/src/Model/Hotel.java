package Model;

import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Persoon;
import Model.ruimte.Lift;
import Model.ruimte.Lobby;
import Model.ruimte.Ruimte;
import Model.ruimte.Trap;

import java.util.ArrayList;
import java.util.List;

// Verantwoordelijkheid: de dataklas van het hotel.
// Hotel slaat alle ruimtes, personen en de layout op.
// Dit is een pure dataklas: hij slaat dingen op maar berekent niks zelf.
public class Hotel {

    // Het aantal kolommen (vakjes breed) van het hotel grid.
    public int breedte;

    // Het aantal rijen (vakjes hoog) van het hotel grid.
    public int hoogte;

    // De layout bevat het volledige grid van vakjes.
    public Layout layout;

    // De pathfinder berekent routes van A naar B in het grid.
    public Pathfinder pathfinder;

    // De lijst van alle ruimtes in het hotel (kamers, restaurant, lobby, lift, trap, etc.).
    public List<Ruimte> ruimtes;

    // De lijst van alle personen in het hotel (gasten en schoonmakers).
    public List<Persoon> personen;

    // Directe referentie naar de lift zodat we hem snel kunnen bereiken.
    public Lift lift;

    // Directe referentie naar de trap zodat we hem snel kunnen bereiken.
    public Trap trap;

    // Directe referentie naar de lobby zodat we hem snel kunnen bereiken.
    public Lobby lobby;

    // Sla op of het brandalarm momenteel actief is: true = actief, false = niet actief.
    // Als dit true is, worden geen nieuwe activiteiten naar gasten gestuurd.
    public boolean brandalarmActief = false;

    // Constructor: maak lege lijsten aan voor ruimtes en personen.
    public Hotel() {
        ruimtes = new ArrayList<>();
        personen = new ArrayList<>();
    }

    // Voeg persoon p toe aan de personenlijst van het hotel.
    public void voegPersoonToe(Persoon p) {
        personen.add(p);
    }

    // Geef de ruimte terug die op positie (x, y) in het grid staat.
    // Als de layout leeg is (null), geef dan null terug.
    // 'layout.krijgVakje(x, y)' geeft het vakje op die positie terug.
    // Als het vakje niet leeg is (niet null), geef dan de ruimte van dat vakje terug.
    public Ruimte krijgRuimteOp(int x, int y) {
        if (layout == null) return null;
        Vakje vakje = layout.krijgVakje(x, y);
        if (vakje != null) return vakje.ruimte;
        return null;
    }
}
