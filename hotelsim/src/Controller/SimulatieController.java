package Controller;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.Lift;
import hotelevents.HotelEventManager;

import java.util.ArrayList;
import java.util.List;

// Verantwoordelijkheid: simulatie starten, pauzeren, stoppen en ticks uitvoeren
public class SimulatieController {

    private HotelEventManager eventManager;
    private EventController eventController;
    private HotelController hotelController;
    private int snelheid = 1;
    private int tikTeller = 0;
    private long startTijdMs = 0;

    // gast wordt gesummond na dit aantal stilstaande ticks
    private static final int MAX_WACHT_TICKS = 15;
    // summoning animatie duurt dit aantal ticks
    private static final int SUMMON_DUUR = 8;

    public SimulatieController(HotelEventManager eventManager, EventController eventController, HotelController hotelController) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;
    }

    public void start(int scenario) {
        startTijdMs = System.currentTimeMillis();
        tikTeller = 0;
        eventManager.start(scenario);
    }

    public void pauzeer() { eventManager.pauze(); }
    public void stop()    { eventManager.stop(); }
    public void setSnelheid(int snelheid) { this.snelheid = snelheid; }

    public void pasSnelheidToe(String keuze) {
        switch (keuze) {
            case "Langzaam" -> snelheid = 0;
            case "Normaal"  -> snelheid = 1;
            case "Snel"     -> snelheid = 50;
            default         -> snelheid = 1;
        }
    }

    public int getTikTeller() { return tikTeller; }

    public String getRealTijd() {
        if (startTijdMs == 0) return "00:00:00";
        long verstreken = System.currentTimeMillis() - startTijdMs;
        long seconden   = verstreken / 1000;
        long uren       = seconden / 3600;
        long minuten    = (seconden % 3600) / 60;
        long sec        = seconden % 60;
        return String.format("%02d:%02d:%02d", uren, minuten, sec);
    }

    // wordt elke simulatie-tick uitgevoerd
    public void tik() {
        Hotel hotel = hotelController.getHotel();
        if (hotel == null) return;

        tikTeller++;

        int stappen = 1;
        if (snelheid <= 0) {
            if (tikTeller % 2 != 0) { hotelController.notifyListeners(); return; }
        }

        for (int i = 0; i < stappen; i++) {
            if (hotel.lift != null) hotel.lift.tik();

            // brandalarm: evacueer iedereen; zodra iedereen buiten is, zet alarm uit
            if (hotel.brandalarmActief) verwerkEvacuatieLoop(hotel);

            verwerkUitstappendeGasten(hotel);
            verwerkWachtendeGasten(hotel);
            verwerkWachttijden(hotel);
            verwerkRestaurantWachtrij(hotel);

            List<Persoon> copy = new ArrayList<>(hotel.personen);
            for (Persoon p : copy) p.beweeg();

            hotelController.notifyListeners();
        }
        try { Thread.sleep(225 / Math.max(1, snelheid)); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    // -----------------------------------------------------------------------
    // Evacuatie
    // -----------------------------------------------------------------------

    /**
     * Elke tick tijdens het alarm:
     * 1. Controleer of iedereen al buiten staat (y == buitenY).
     *    Zo ja: alarm uit, lift terug aan, iedereen terug naar hun taak.
     * 2. Zo nee: zorg dat iedereen een evacuatieroute heeft.
     *    Lift-wachters en lift-passagiers worden direct via de trap omgeleid.
     */
    private void verwerkEvacuatieLoop(Hotel hotel) {
        if (hotel.pathfinder == null || hotel.lobby == null) return;

        int buitenY = hotel.lobby.posY - 1;
        int midX    = hotel.lobby.posX + hotel.lobby.breedte / 2;
        Vakje uitgang = hotel.layout.krijgVakje(midX, buitenY);
        if (uitgang == null) return;

        // --- check of iedereen buiten staat ---
        boolean iederBuiten = !hotel.personen.isEmpty();
        for (Persoon p : hotel.personen) {
            if (p.huidigVakje == null || p.huidigVakje.y != buitenY) {
                iederBuiten = false;
                break;
            }
        }

        if (iederBuiten) {
            // alarm uitzetten en lift terug in gebruik
            hotel.brandalarmActief = false;
            if (hotel.lift != null) hotel.lift.zetUitBedrijf(false);

            // iedereen terug naar hun taak
            for (Persoon p : hotel.personen) {
                if (p.huidigVakje == null) continue;
                p.wisRoute(); // wis evacuatieroute eerst
                if (p instanceof Gast) {
                    Gast g = (Gast) p;
                    if (g.kamer != null) {
                        hotel.pathfinder.zetRoute(g, g.kamer);
                    }
                } else if (p instanceof Schoonmaker) {
                    Schoonmaker s = (Schoonmaker) p;
                    if (s.bezig && s.kamer != null) {
                        hotel.pathfinder.zetRoute(s, s.kamer);
                    } else if (s.wachtVakje != null) {
                        hotel.pathfinder.zetRouteTrap(s, s.wachtVakje);
                    }
                }
            }
            return;
        }

        // --- alarm nog actief: stuur iedereen die nog niet evacuert ---
        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (p.huidigVakje == null) continue;
            // al buiten: overslaan
            if (p.huidigVakje.y == buitenY) continue;

            // gast in lift of wachtend op lift: reset en via trap
            if (p instanceof Gast) {
                Gast g = (Gast) p;
                if (g.wachtOpLift || g.inLift) {
                    g.wachtOpLift  = false;
                    g.gebruiktLift = false;
                    if (g.inLift && hotel.lift != null) {
                        int ux = hotel.lift.posX + 1;
                        int uy = hotel.lift.getHuidigeVerdieping();
                        Vakje uv = hotel.layout.krijgVakje(ux, uy);
                        if (uv != null) {
                            g.huidigVakje.verwijderPersoon(g);
                            g.huidigVakje = uv;
                            uv.voegPersoonToe(g);
                        }
                        g.inLift = false;
                    }
                    p.evacueer(uitgang, hotel.pathfinder);
                    continue;
                }
            }

            // persoon heeft nog geen route richting buiten: geef evacuatieroute
            // een persoon evacueert al als zijn uiteindelijke doel de buiten-rij is,
            // of als hij onderweg is naar beneden (doel.y <= lobby.posY)
            boolean heeftEvacuatieRoute = p.doelVakje != null
                    && p.doelVakje.y <= buitenY + 1; // richting buiten (lobby of lager)
            if (!heeftEvacuatieRoute) {
                p.evacueer(uitgang, hotel.pathfinder);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Lift-afhandeling
    // -----------------------------------------------------------------------

    private void verwerkUitstappendeGasten(Hotel hotel) {
        for (Persoon p : hotel.personen) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;
            if (!g.moetUitstappen) continue;
            g.moetUitstappen = false;

            int uitstapX = hotel.lift.posX + 1;
            int uitstapY = hotel.lift.getHuidigeVerdieping();
            Vakje uitstapVakje = hotel.layout.krijgVakje(uitstapX, uitstapY);
            if (uitstapVakje != null) {
                if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
                g.huidigVakje = uitstapVakje;
                uitstapVakje.voegPersoonToe(g);
            }

            g.gebruiktLift = false;
            g.wachtOpLift  = false;

            if (g.eindbestemming != null && hotel.pathfinder != null) {
                Model.ruimte.Ruimte bestemming = g.eindbestemming;
                g.eindbestemming = null;
                int[] ingang = bestemming.krijgIngang();
                Vakje doelVakje = hotel.layout.krijgVakje(ingang[0], ingang[1]);
                if (doelVakje == null) doelVakje = hotel.layout.krijgVakje(bestemming.posX, bestemming.posY);
                if (doelVakje != null) hotel.pathfinder.zetRouteTrap(g, doelVakje);
            }
        }
    }

    private void verwerkWachtendeGasten(Hotel hotel) {
        Lift lift = hotel.lift;
        if (lift == null) return;
        for (Persoon p : hotel.personen) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;
            if (!g.gebruiktLift || g.inLift || g.huidigVakje == null) continue;
            if (g.huidigVakje.x == lift.posX + 1) {
                g.wachtOpLift = lift.getHuidigeVerdieping() != g.huidigVakje.y;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Summoning: gast verdwijnt na te lang stilstaan
    // -----------------------------------------------------------------------

    private void verwerkWachttijden(Hotel hotel) {
        List<Persoon> teVerwijderen = new ArrayList<>();

        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;

            // summoning animatie loopt
            if (g.summonTick >= 0) {
                g.summonTick++;
                if (g.summonTick >= SUMMON_DUUR) {
                    if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
                    g.huidigVakje = null;
                    teVerwijderen.add(g);
                }
                continue;
            }

            // gast in een echte ruimte: niet summonen
            boolean inRuimte = g.huidigVakje != null
                    && g.huidigVakje.ruimte != null
                    && !(g.huidigVakje.ruimte instanceof Model.ruimte.Lift)
                    && !(g.huidigVakje.ruimte instanceof Model.ruimte.Trap)
                    && !(g.huidigVakje.ruimte instanceof Model.ruimte.Lobby);

            // buiten (evacuatie-rij): ook niet summonen
            boolean buiten = hotel.lobby != null
                    && g.huidigVakje != null
                    && g.huidigVakje.y == hotel.lobby.posY - 1;

            boolean stilstaand = g.doelVakje == null
                    && !g.inLift
                    && !g.uitcheckend
                    && !inRuimte
                    && !buiten
                    && g.huidigVakje != null;

            if (stilstaand) {
                g.wachtTicks++;
                if (g.wachtTicks >= MAX_WACHT_TICKS) {
                    g.summonTick = 0;
                    g.wisRoute();
                }
            } else {
                g.wachtTicks = 0;
            }
        }

        hotel.personen.removeAll(teVerwijderen);
    }

    // -----------------------------------------------------------------------
    // Restaurant wachtrij
    // -----------------------------------------------------------------------

    private void verwerkRestaurantWachtrij(Hotel hotel) {
        if (hotel.pathfinder == null) return;
        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;
            if (!g.wachtOpRestaurant || g.wachtRestaurant == null) continue;
            if (g.doelVakje != null) continue;

            Model.ruimte.Restaurant r = g.wachtRestaurant;

            if (!r.isVol()) {
                g.wachtOpRestaurant = false;
                g.wachtRestaurant   = null;
                hotel.pathfinder.zetRoute(g, r);
                continue;
            }

            Model.ruimte.Restaurant alternatief = vindNietVolRestaurant(hotel, g, r);
            if (alternatief != null) {
                g.wachtOpRestaurant = false;
                g.wachtRestaurant   = null;
                hotel.pathfinder.zetRoute(g, alternatief);
            }
        }
    }

    private Model.ruimte.Restaurant vindNietVolRestaurant(
            Hotel hotel, Gast gast, Model.ruimte.Restaurant uitgesloten) {
        Model.ruimte.Restaurant beste = null;
        int minAfstand = Integer.MAX_VALUE;
        for (Model.ruimte.Ruimte r : hotel.ruimtes) {
            if (!(r instanceof Model.ruimte.Restaurant)) continue;
            if (r == uitgesloten) continue;
            Model.ruimte.Restaurant rest = (Model.ruimte.Restaurant) r;
            if (rest.isVol()) continue;
            int afstand = Math.abs(r.posX - gast.huidigVakje.x)
                        + Math.abs(r.posY - gast.huidigVakje.y);
            if (afstand < minAfstand) {
                minAfstand = afstand;
                beste = rest;
            }
        }
        return beste;
    }
}
