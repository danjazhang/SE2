package Controller;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
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
    private static final int MAX_WACHT_TICKS = 5;
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
    public void stop() { eventManager.stop(); }
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
        long seconden = verstreken / 1000;
        long uren = seconden / 3600;
        long minuten = (seconden % 3600) / 60;
        long sec = seconden % 60;
        return String.format("%02d:%02d:%02d", uren, minuten, sec);
    }

    public void tik() {
        Hotel hotel = hotelController.getHotel();
        if (hotel == null) return;

        tikTeller++;

        int stappen = 1;
        if (snelheid <= 0) {
            if (tikTeller % 2 != 0) { hotelController.notifyListeners(); return; }
        }
        //else if (snelheid >= 4) {
        //    stappen = snelheid;
        //}

        for (int i = 0; i < stappen; i++) {
            if (hotel.lift != null) hotel.lift.tik();
            verwerkUitstappendeGasten(hotel);
            verwerkWachtendeGasten(hotel);
            verwerkWachttijden(hotel);
            verwerkRestaurantWachtrij(hotel);
            List<Persoon> copy = new ArrayList<>(hotel.personen);
            for (Persoon p : copy) p.beweeg();
            hotelController.notifyListeners();

        }
        try { Thread.sleep(225 / Math.max(1,snelheid)); } catch (InterruptedException e) { e.printStackTrace(); }
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
            g.wachtOpLift = false;

            if (g.eindbestemming != null && hotel.pathfinder != null) {
                Model.ruimte.Ruimte bestemming = g.eindbestemming;
                g.eindbestemming = null;
                int[] ingang = bestemming.krijgIngang();
                Vakje doelVakje = hotel.layout.krijgVakje(ingang[0], ingang[1]);
                if (doelVakje == null) {
                    doelVakje = hotel.layout.krijgVakje(bestemming.posX, bestemming.posY);
                }
                if (doelVakje != null) {
                    hotel.pathfinder.zetRouteTrap(g, doelVakje);
                }
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
            boolean opWachtplek = g.huidigVakje.x == lift.posX + 1;
            if (opWachtplek) {
                g.wachtOpLift = lift.getHuidigeVerdieping() != g.huidigVakje.y;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Summoning: gast verdwijnt na te lang stilstaan
    // Gasten in een ruimte (kamer/restaurant/etc) tellen niet mee.
    // Gasten die wachten op het restaurant tellen ook niet mee —
    // als ze te lang wachten activeren ze summoning zodra wachtOpRestaurant
    // niet meer actief is EN ze buiten staan.
    // -----------------------------------------------------------------------

    private void verwerkWachttijden(Hotel hotel) {
        List<Persoon> teVerwijderen = new ArrayList<>();

        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;

            // summoning animatie loopt: tel op en verwijder als klaar
            if (g.summonTick >= 0) {
                g.summonTick++;
                if (g.summonTick >= SUMMON_DUUR) {
                    if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
                    g.huidigVakje = null;
                    teVerwijderen.add(g);
                }
                continue;
            }

            // gast in een echte ruimte (kamer, restaurant, etc.): niet summonen
            boolean inRuimte = g.huidigVakje != null
                    && g.huidigVakje.ruimte != null
                    && !(g.huidigVakje.ruimte instanceof Model.ruimte.Lift)
                    && !(g.huidigVakje.ruimte instanceof Model.ruimte.Trap)
                    && !(g.huidigVakje.ruimte instanceof Model.ruimte.Lobby);

            // gast wacht op restaurant telt ook mee voor summoning
            boolean stilstaand = g.doelVakje == null
                    && !g.inLift
                    && !g.uitcheckend
                    && !inRuimte
                    && g.huidigVakje != null
                    && g.huidigVakje.y != hotel.lobby.posY - 1;

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
    // Restaurant wachtrij: stuur wachtende gasten naar binnen zodra er plek is
    // -----------------------------------------------------------------------

    private void verwerkRestaurantWachtrij(Hotel hotel) {
        if (hotel.pathfinder == null) return;
        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;
            if (!g.wachtOpRestaurant || g.wachtRestaurant == null) continue;
            if (g.doelVakje != null) continue; // nog onderweg naar wachtplek

            Model.ruimte.Restaurant r = g.wachtRestaurant;

            // wachtrestaurant heeft nu plek
            if (!r.isVol()) {
                g.wachtOpRestaurant = false;
                g.wachtRestaurant = null;
                hotel.pathfinder.zetRoute(g, r);
                continue;
            }

            // zoek een alternatief niet-vol restaurant
            Model.ruimte.Restaurant alternatief = vindNietVolRestaurant(hotel, g, r);
            if (alternatief != null) {
                g.wachtOpRestaurant = false;
                g.wachtRestaurant = null;
                hotel.pathfinder.zetRoute(g, alternatief);
            }
            // anders: blijf wachten — wachtTicks tellen niet want wachtOpRestaurant=true
        }
    }

    private Model.ruimte.Restaurant vindNietVolRestaurant(Hotel hotel, Gast gast, Model.ruimte.Restaurant uitgesloten) {
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
