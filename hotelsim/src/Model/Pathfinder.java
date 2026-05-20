package Model;
import Model.layout.Layout;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.*;

public class Pathfinder {

    // Referentie naar layout
    private Layout layout;

    // Referentie naar hotel
    private Hotel hotel;

    public Pathfinder(Hotel hotel) {
        this.hotel = hotel;
        this.layout = hotel.layout;
    }

    // Bepaal volgende stap richting doel
    public Vakje volgendeStap(Vakje huidig, Vakje doel) {

        // Ongeldige input
        if (huidig == null || doel == null) {
            return null;
        }

        int x = huidig.x;
        int y = huidig.y;

        // Zelfde verdieping
        // Alleen horizontaal bewegen
        if (y == doel.y) {

            // Naar rechts
            if (x < doel.x) {
                x++;
            }

            // Naar links
            else if (x > doel.x) {
                x--;
            }

            return layout.krijgVakje(x, y);
        }

        // Persoon staat op trap
        // Verticale beweging mogelijk
        if (huidig.ruimte instanceof Trap) {

            // Omhoog
            if (y < doel.y) {
                y++;
            }

            // Omlaag
            else if (y > doel.y) {
                y--;
            }
            return layout.krijgVakje(x, y);
        }

        // Geen geldige stap
        return null;
    }

    // Zet route naar ruimte
    public void zetRoute(Persoon p, Ruimte bestemming) {

        Vakje start = p.huidigVakje;
        Vakje doel = layout.krijgVakje( bestemming.posX, bestemming.posY );

        // Ongeldige start/doel
        if (start == null || doel == null) {
            return;
        }

        // Sla eindbestemming op bij gast
        if (p instanceof Gast g) {
            g.eindbestemming = bestemming;
        }

        // Zelfde verdieping
        // Direct lopen
        if (start.y == doel.y) {
            p.zetDoel(doel);
            return;
        }

        // Schoonmakers gebruiken altijd trap
        if (p instanceof Schoonmaker) {
            routeViaTrap(p, start, doel);
            return;
        }

        // Bereken geschatte reistijden
        int trapTijd = Math.abs(start.y - doel.y) * hotel.trap.tijdperverdieping;
        int liftTijd = schatLiftTijd(start, doel);

        // Gebruik lift bij lagere tijd Of bij groot hoogteverschil
        if ( liftTijd < trapTijd || Math.abs(start.y - doel.y) > 4 ) {
            routeViaLift(p, start, doel);
        } else {
            routeViaTrap(p, start, doel);
        }
    }

    // Vind wachtplek naast lift
    private Vakje vindLiftWachtplek(int y) {

        for (Ruimte r : hotel.ruimtes) {

            if (r instanceof Lift) {

                // Vakje rechts van lift
                return layout.krijgVakje(
                        r.posX + 1,
                        y
                );
            }
        }
        return null;
    }

    // Route via lift
    private void routeViaLift( Persoon p, Vakje start, Vakje doel ) {

        Gast g = (Gast) p;

        // Zoek liftplek
        Vakje liftVakje =
                vindLiftWachtplek(start.y);

        // Geen lift gevonden
        if (liftVakje == null) {
            routeViaTrap(p, start, doel);
            return;
        }

        // Zet liftstatus
        g.gebruiktLift = true;
        g.gewensteVerdieping = doel.y;

        // Loop eerst naar lift
        p.zetDoel(liftVakje);

        // Roep lift op
        hotel.lift.roep(p, start.y);
    }

    // Route via trap
    private void routeViaTrap( Persoon p, Vakje start, Vakje doel ) {

        // Trap op huidige verdieping
        Vakje trap1 = vindTrap(start.y);

        // Trap op doelverdieping
        Vakje trap2 = vindTrap(doel.y);

        // Eerst naar eerste trap
        if (trap1 != null) {
            p.zetDoel(trap1);
        }

        // Daarna naar tweede trap
        if ( trap2 != null && !trap2.equals(trap1) ) {
            p.voegTussendoelToe(trap2);
        }

        // Uiteindelijk naar doel
        p.voegTussendoelToe(doel);
    }

    // Schatting lift reistijd
    private int schatLiftTijd( Vakje start, Vakje doel ) {

        Lift lift = hotel.lift;

        // Wachttijd tot lift aankomt
        int wacht = Math.abs( lift.getHuidigeVerdieping() - start.y );

        // Reistijd in lift
        int rit = Math.abs(start.y - doel.y);

        // Grootte wachtrij
        int queue = lift.aantalWachtend(start.y);

        return wacht + rit + queue;
    }

    // Vind liftvakje
    private Vakje vindLift(int y) {

        for (Ruimte r : hotel.ruimtes) {

            if (r instanceof Lift) {
                return layout.krijgVakje( r.posX, y );
            }
        }

        return null;
    }

    // Vind trapvakje
    private Vakje vindTrap(int y) {

        for (Ruimte r : hotel.ruimtes) {

            if (r instanceof Trap) {
                return layout.krijgVakje( r.posX, y );
            }
        }
        return null;
    }
}