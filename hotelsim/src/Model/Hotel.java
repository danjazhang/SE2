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

// Model klasse: bevat alle data van het hotel
public class Hotel {

    // breedte en hoogte van het hotel grid
    public int breedte;
    public int hoogte;

    // de huidige layout van het hotel
    public Layout layout;

    // pathfinder voor het berekenen van routes
    public Pathfinder pathfinder;

    // lijst van alle ruimtes in het hotel
    public List<Ruimte> ruimtes;

    // lijst van alle personen in het hotel
    public List<Persoon> personen;

    public Lift lift;
    public Trap trap;
    public Lobby lobby;

    public BrandalarmService brandalarmService;

    // of het brandalarm momenteel actief is
    public boolean brandalarmActief = false;

    // constructor: maak lege lijsten aan
    public Hotel() {
        ruimtes = new ArrayList<>();
        personen = new ArrayList<>();
    }

    // voeg een persoon toe aan het hotel
    public void voegPersoonToe(Persoon p) {
        personen.add(p);
        //als brandalarm actief is: direct evacueren
        if (brandalarmService != null && brandalarmActief) {
            brandalarmService.evacueerNieuwePersoon(p);
        }
    }

    // geef de ruimte op positie (x, y) terug
    public Ruimte krijgRuimteOp(int x, int y) {
        if (layout== null) return null;
        Vakje vakje = layout.krijgVakje(x, y);
        if (vakje != null) return vakje.ruimte;
        return null;
    }
}
