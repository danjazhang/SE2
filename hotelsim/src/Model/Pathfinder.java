package Model;

import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.ruimte.Lift;
import Model.ruimte.Ruimte;
import Model.ruimte.Trap;

import java.util.ArrayList;
import java.util.List;

// Verantwoordelijkheid: routes berekenen tussen twee vakjes via lift of trap
public class Pathfinder {

    private Layout layout;
    private Hotel hotel;

    public Pathfinder(Hotel hotel) {
        this.hotel = hotel;
        this.layout = hotel.layout;
    }

    public Vakje volgendeStap(Vakje huidig, Vakje doel) {
        //haal x en y coordinaten om mee te rekenen
        int huidigX = huidig.x;
        int huidigY = huidig.y;
        int doelX = doel.x;
        int doelY = doel.y;

    
        //beginnen met nieuwe positie gelijk aan huidige en dan aanpassen
        int nieuweX = huidigX;
        int nieuweY = huidigY;

        //beweeg 1 stap in x of y richting
        if (huidigX < doelX) nieuweX++;
        else if (huidigX > doelX) nieuweX--;
        else if (huidigY < doelY) nieuweY++;
        else if (huidigY > doelY) nieuweY--;

        //zoek vakje op nieuwe positie via layout
        return layout.krijgVakje(nieuweX, nieuweY);
    }

    // bereken route van gast naar ruimte en zet die op de gast
    public void zetRoute(Gast gast, Ruimte ruimte) {
        Vakje start = gast.huidigVakje;
        Vakje doel = layout.krijgVakje(ruimte.posX, ruimte.posY);
        if (start == null || doel == null) return;
        List<Vakje> route = berekenRoute(start, doel);
        gast.zetDoel(route.get(0));
        for (int i = 1; i < route.size(); i++) {
            gast.voegTussendoelToe(route.get(i));
        }
    }

    // zoek het trap vakje op een bepaalde verdieping op
    private Vakje vindTrapVakje(int verdieping) {
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Trap) {
                return layout.krijgVakje(r.posX, verdieping);
            }
        }
        return null;
    }

    // zoek het lift vakje op een bepaalde verdieping op
    private Vakje vindLiftVakje(int verdieping) {
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Lift) {
                return layout.krijgVakje(r.posX, verdieping);
            }
        }
        return null;
    }

    // kies willekeurig tussen lift en trap
    public List<Vakje> berekenRoute(Vakje start, Vakje doel) {
         List<Vakje> route = new ArrayList<>();

        boolean gebruikLift = Math.random() < 0.5;

        // stap 1: ga naar lift of trap op huidige verdieping
        Vakje transport;
        if (gebruikLift) {
            transport = vindLiftVakje(start.y);
        } else {
            transport = vindTrapVakje(start.y);
        }
        if (transport != null) route.add(transport);

        // stap 2: ga naar de doelverdieping
        if (start.y != doel.y) {
            Vakje transportOpDoel;
            if (gebruikLift) {
                transportOpDoel = vindLiftVakje(doel.y);
            } else {
                transportOpDoel = vindTrapVakje(doel.y);
            }
            if (transportOpDoel != null) route.add(transportOpDoel);
        }

        // stap 3: einddoel
        route.add(doel);
        return route;
    }
}
