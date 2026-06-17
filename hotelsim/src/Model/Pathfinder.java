package Model;

import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.*;

public class Pathfinder {

    //atributen opgeslagen als variabelen
    private Layout layout;
    private Hotel hotel;

    //constructor
    public Pathfinder(Hotel hotel) {
        //haal hotel en layout uit hotel op en sla op in deze objecten
        this.hotel = hotel;
        this.layout = hotel.layout;
    }

    // bepaal volgende stap richting doel
    public Vakje volgendeStap(Vakje huidig, Vakje doel) {
        // als h en d niet bestaan, geef null terug
        if (huidig == null || doel == null) return null;
        // sla x en y van huidige vakje op
        int x = huidig.x;
        int y = huidig.y;

        // als huidige ruimte onderdeel is van een trap
        if (huidig.ruimte instanceof Trap) {
            //als huidige y lager is dan, ga 1 vakje omhoog
            if (y < doel.y) return layout.krijgVakje(x, y + 1);
            //als huidige y hoger is dan doel y, 1 vakje omlaag
            if (y > doel.y) return layout.krijgVakje(x, y - 1);
            //als x kleiner is dan doel x, 1 vakje naar recht
            if (x < doel.x) return layout.krijgVakje(x + 1, y);
            //als x groter is dan doel x, 1 vakje naar links
            if (x > doel.x) return layout.krijgVakje(x - 1, y);
            return null;
        }

        // als huidige y gelijk is aan doel y
        if (y == doel.y) {
            //als x kleiner is dan doel x, verhoog met 1
            if (x < doel.x) x++;
            // anders als x groter is dan doel x, verlaag met 1
            else if (x > doel.x) x--;
            //geef vakje op nieuwe positie terug
            return layout.krijgVakje(x, y);
        }

        // zelfde x als doel en doel is direct erboven of eronder: stap verticaal
        if (x == doel.x) {
            if (y < doel.y) return layout.krijgVakje(x, y + 1);
            if (y > doel.y) return layout.krijgVakje(x, y - 1);
        }

        // als x niet gelijk is aan doel x
        if (x != doel.x) {
            // als x kleiner is dan doel x, ga stap naar links
            if (x < doel.x) return layout.krijgVakje(x + 1, y);
            return layout.krijgVakje(x - 1, y);
        }

        return null;
    }

    // zet route naar ruimte — kiest automatisch lift of trap
    public void zetRoute(Persoon p, Ruimte bestemming) {
        Vakje start = p.huidigVakje;
        Vakje doel = layout.krijgVakje(bestemming.posX, bestemming.posY);
        if (start == null || doel == null) return;
        if (p instanceof Gast g) g.eindbestemming = bestemming;
        // als de gast al in de lift-wachtrij stond: verwijder hem eerst
        resetLiftStatusAlsNodig(p);
        // zelfde verdieping: direct lopen
        if (start.y == doel.y) { p.zetDoel(doel); return; }
        if (p instanceof Schoonmaker) { routeViaTrap(p, start, doel); return; }
        int trapTijd = Math.abs(start.y - doel.y) * hotel.trap.tijdperverdieping;
        int liftTijd = schatLiftTijd(start, doel);
        if (liftTijd < trapTijd || Math.abs(start.y - doel.y) > 4) {
            routeViaLift(p, start, doel);
        } else {
            routeViaTrap(p, start, doel);
        }
    }

    // zet route altijd via trap — gebruikt tijdens brandalarm zodat lift volledig uitgesloten is
    public void zetRouteTrap(Persoon p, Vakje doel) {
        Vakje start = p.huidigVakje;
        if (start == null || doel == null) return;
        // als de gast al in de lift-wachtrij stond: verwijder hem eerst
        resetLiftStatusAlsNodig(p);
        // zelfde verdieping: direct lopen zonder trap
        if (start.y == doel.y) { p.zetDoel(doel); return; }
        // altijd via trap, lift wordt nooit overwogen
        routeViaTrap(p, start, doel);
    }

    // reset lift-gerelateerde status als de gast wacht op de lift maar een nieuwe route krijgt
// dit voorkomt dat een gast "vast blijft hangen" in een oude lift-aanvraag
    private void resetLiftStatusAlsNodig(Persoon p) {

        // alleen gasten gebruiken de lift-logica
        if (!(p instanceof Gast)) return;

        Gast g = (Gast) p;

        // als de gast wel een lift heeft aangevraagd maar nog niet in de lift zit
        if (g.gebruiktLift && !g.inLift) {

            // reset alle lift-statussen zodat hij opnieuw kan plannen
            g.gebruiktLift = false;
            g.wachtOpLift  = false;

            // haal de gast ook uit de lift-wachtrij (anders blijft hij daar hangen)
            if (hotel.lift != null) hotel.lift.verwijderUitWachtrij(g);
        }
    }

    // route berekenen via de lift
// wordt gebruikt als de lift de snelste of gewenste optie is
    private void routeViaLift(Persoon p, Vakje start, Vakje doel) {

        Gast g = (Gast) p;

        // zoek de wachtruimte (vakje naast de lift waar gasten moeten wachten)
        Vakje liftVakje = vindLiftWachtplek(start.y);

        // als er geen lift beschikbaar is → fallback naar trap
        if (liftVakje == null) {
            routeViaTrap(p, start, doel);
            return;
        }

        // gast gebruikt nu de lift
        g.gebruiktLift = true;

        // onthoud naar welke verdieping de gast wil
        g.gewensteVerdieping = doel.y;

        // eerste doel: wachten bij de lift
        p.zetDoel(liftVakje);

        // activeer de lift en roep hem naar de startverdieping
        hotel.lift.roep(g, start.y);
    }

    // route berekenen via trappen in plaats van lift
// kan meerdere stappen bevatten als meerdere verdiepingen nodig zijn
    private void routeViaTrap(Persoon p, Vakje start, Vakje doel) {

        // eerste trap op huidige verdieping zoeken
        Vakje trap1 = vindTrap(start.y);

        // tweede trap op doelverdieping zoeken
        Vakje trap2 = vindTrap(doel.y);

        // als er een trap op startverdieping is → eerst daarheen
        if (trap1 != null) p.zetDoel(trap1);

        // als er een andere trap op doelverdieping is → als tussenstap toevoegen
        if (trap2 != null && !trap2.equals(trap1)) p.voegTussendoelToe(trap2);

        // uiteindelijke bestemming blijft altijd het einddoel
        p.voegTussendoelToe(doel);
    }

    // zoekt de wachtruimte naast de lift op een bepaalde verdieping
    private Vakje vindLiftWachtplek(int y) {

        // door alle ruimtes in het hotel lopen
        for (Ruimte r : hotel.ruimtes) {

            // we zoeken de lift-ruimte zelf
            if (r instanceof Lift) {

                // wachtplek zit naast de lift (posX + 1) op de juiste verdieping (y)
                return layout.krijgVakje(r.posX + 1, y);
            }
        }

        // geen lift gevonden → geen wachtplek beschikbaar
        return null;
    }

    // schat hoe lang het duurt voordat een gast met de lift op bestemming is
    private int schatLiftTijd(Vakje start, Vakje doel) {

        Lift lift = hotel.lift;

        // tijd tot de lift naar de startverdieping komt
        int wacht = Math.abs(lift.getHuidigeVerdieping() - start.y);

        // tijd om van startverdieping naar doelverdieping te reizen
        int rit   = Math.abs(start.y - doel.y);

        // extra wachttijd door andere mensen in de lift-queue
        int queue = lift.aantalWachtend(start.y);

        // +2 ticks voor instappen en uitstappen (animatie / statusovergangen)
        return wacht + rit + queue + 2;
    }

    private Vakje vindTrap(int y) {
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Trap) {
                // clip y naar de grenzen van de trap (posY t/m posY+hoogte-1)
                int trapY = Math.max(r.posY, Math.min(y, r.posY + r.hoogte - 1));
                return layout.krijgVakje(r.posX, trapY);
            }
        }
        return null;
    }
}
