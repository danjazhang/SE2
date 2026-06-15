package Controller;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import Model.ruimte.Lift;
import hotelevents.HotelEventManager;

import java.util.ArrayList;
import java.util.List;

// Verantwoordelijkheid: simulatie starten, pauzeren, stoppen en ticks uitvoeren
public class SimulatieController {

    private HotelEventManager eventManager;
    private EventController eventController;
    private HotelController hotelController;
    private int tikTeller = 0;
    private long startTijdMs = 0;

    private static final int MAX_WACHT_TICKS = 5;
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

    public void pasSnelheidToe(String keuze) {
        switch (keuze) {
            case "Langzaam" -> eventManager.setHte(1500);
            case "Normaal"  -> eventManager.setHte(1000);
            case "Snel"     -> eventManager.setHte(10);
            default         -> eventManager.setHte(1000);
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

    public void tik() {
        Hotel hotel = hotelController.getHotel();
        if (hotel == null) return;
        tikTeller++;

        if (hotel.lift != null) hotel.lift.tik();

        if (hotel.brandalarmActief) {
            verwerkEvacuatieLoop(hotel);
        } else {
            verwerkNaEvacuatieRoute(hotel);
        }

        verwerkUitstappendeGasten(hotel);
        verwerkWachtendeGasten(hotel);
        verwerkWachttijden(hotel);
        verwerkRestaurantWachtrij(hotel);

        List<Persoon> copy = new ArrayList<>(hotel.personen);
        for (Persoon p : copy) p.beweeg();

        hotelController.notifyListeners();
    }

    // -----------------------------------------------------------------------
    // Evacuatie
    // -----------------------------------------------------------------------

    private void verwerkEvacuatieLoop(Hotel hotel) {
        if (hotel.pathfinder == null || hotel.lobby == null) return;

        int buitenY = hotel.lobby.posY - 1;
        int midX    = hotel.lobby.posX + hotel.lobby.breedte / 2;
        Vakje uitgang = hotel.layout.krijgVakje(midX, buitenY);
        if (uitgang == null) return;

        // verwijder uitcheckende gasten die al buiten staan
        List<Persoon> teVerwijderen = new ArrayList<>();
        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;
            if (g.uitcheckend && g.huidigVakje != null && g.huidigVakje.y == buitenY) {
                g.huidigVakje.verwijderPersoon(g);
                g.huidigVakje = null;
                if (hotel.lift != null) hotel.lift.verwijderUitWachtrij(g);
                teVerwijderen.add(g);
            }
        }
        hotel.personen.removeAll(teVerwijderen);

        // check of iedereen (niet-uitcheckend) buiten staat
        boolean iederBuiten = !hotel.personen.isEmpty();
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).uitcheckend) continue;
            if (p.huidigVakje == null || p.huidigVakje.y != buitenY) {
                iederBuiten = false;
                break;
            }
        }

        if (iederBuiten) {
            // alarm uit, lift terug aan
            hotel.brandalarmActief = false;
            if (hotel.lift != null) {
                hotel.lift.zetUitBedrijf(false);
                hotel.lift.resetWachtrijen();
            }

            Vakje lobbyVakje = hotel.layout.krijgVakje(midX, hotel.lobby.posY);

            for (Persoon p : hotel.personen) {
                if (p.huidigVakje == null) continue;
                p.wisRoute();

                if (p instanceof Gast) {
                    Gast g = (Gast) p;
                    Model.ruimte.Ruimte doel = (g.eindbestemming != null) ? g.eindbestemming : g.kamer;
                    if (doel != null) {
                        g.eindbestemming = doel;
                        if (lobbyVakje != null) g.zetDoel(lobbyVakje);
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
            verwerkWachtendeSchoonmaakTaken(hotel);
            return;
        }

        // alarm nog actief: iedereen die nog niet buiten is krijgt evacuatieroute
        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (p.huidigVakje == null) continue;
            if (p.huidigVakje.y == buitenY) continue;

            // gast in of wachtend op lift: reset en via trap
            if (p instanceof Gast) {
                Gast g = (Gast) p;
                if (g.wachtOpLift || g.inLift) {
                    g.wachtOpLift  = false;
                    g.gebruiktLift = false;
                    if (g.inLift && hotel.lift != null) {
                        Vakje uv = hotel.layout.krijgVakje(hotel.lift.posX + 1, hotel.lift.getHuidigeVerdieping());
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

            boolean heeftEvacuatieRoute = p.doelVakje != null && p.doelVakje.y <= buitenY + 1;
            if (!heeftEvacuatieRoute) {
                p.evacueer(uitgang, hotel.pathfinder);
            }
        }
    }

    // Na evacuatie: gasten in lobby zonder doel sturen naar hun kamer
    private void verwerkNaEvacuatieRoute(Hotel hotel) {
        if (hotel.pathfinder == null || hotel.lobby == null) return;

        for (Persoon p : hotel.personen) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;
            if (g.huidigVakje == null || g.doelVakje != null) continue;

            Model.ruimte.Ruimte bestemming = (g.eindbestemming != null) ? g.eindbestemming : g.kamer;
            if (bestemming == null) continue;

            // lobby of hoger bereikt: stuur naar kamer via normale routing
            if (g.huidigVakje.y >= hotel.lobby.posY) {
                g.eindbestemming = null;
                hotel.pathfinder.zetRoute(g, bestemming);
            }
        }
    }

    private void verwerkWachtendeSchoonmaakTaken(Hotel hotel) {
        if (hotel.wachtendeSchoonmaakKamers == null || hotel.wachtendeSchoonmaakKamers.isEmpty()) return;
        List<Kamer> afgehandeld = new ArrayList<>();
        for (Kamer kamer : new ArrayList<>(hotel.wachtendeSchoonmaakKamers)) {
            Schoonmaker vrij = vindVrijeSchoonmaker(hotel);
            if (vrij == null) break;
            vrij.maakKamerSchoon(kamer);
            hotel.pathfinder.zetRoute(vrij, kamer);
            afgehandeld.add(kamer);
        }
        hotel.wachtendeSchoonmaakKamers.removeAll(afgehandeld);
    }

    private Schoonmaker vindVrijeSchoonmaker(Hotel hotel) {
        for (Persoon p : hotel.personen) {
            if (p instanceof Schoonmaker && !((Schoonmaker) p).bezig) return (Schoonmaker) p;
        }
        return null;
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

            Vakje uitstapVakje = hotel.layout.krijgVakje(hotel.lift.posX + 1, hotel.lift.getHuidigeVerdieping());
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
                Vakje doel = hotel.layout.krijgVakje(ingang[0], ingang[1]);
                if (doel == null) doel = hotel.layout.krijgVakje(bestemming.posX, bestemming.posY);
                if (doel != null) hotel.pathfinder.zetRouteTrap(g, doel);
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
            // wachtplek is posX+1 — maar alleen als die y ook binnen de lift-range valt
            if (g.huidigVakje.x == lift.posX + 1 && g.huidigVakje.y >= lift.posY) {
                g.wachtOpLift = lift.getHuidigeVerdieping() != g.huidigVakje.y;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Summoning
    // -----------------------------------------------------------------------

    private void verwerkWachttijden(Hotel hotel) {
        List<Persoon> teVerwijderen = new ArrayList<>();

        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;

            // uitcheckende gast buiten: direct verwijderen
            if (g.uitcheckend && g.huidigVakje != null && hotel.lobby != null
                    && g.huidigVakje.y == hotel.lobby.posY - 1) {
                g.huidigVakje.verwijderPersoon(g);
                g.huidigVakje = null;
                if (hotel.lift != null) hotel.lift.verwijderUitWachtrij(g);
                teVerwijderen.add(g);
                continue;
            }

            // summoning animatie
            if (g.summonTick >= 0) {
                g.summonTick++;
                if (g.summonTick >= SUMMON_DUUR) {
                    if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
                    g.huidigVakje = null;
                    if (hotel.lift != null) hotel.lift.verwijderUitWachtrij(g);
                    teVerwijderen.add(g);
                }
                continue;
            }

            boolean inRuimte = g.huidigVakje != null && g.huidigVakje.ruimte != null
                    && !(g.huidigVakje.ruimte instanceof Model.ruimte.Lift)
                    && !(g.huidigVakje.ruimte instanceof Model.ruimte.Trap)
                    && !(g.huidigVakje.ruimte instanceof Model.ruimte.Lobby);

            boolean buiten = hotel.lobby != null && g.huidigVakje != null
                    && g.huidigVakje.y == hotel.lobby.posY - 1;

            boolean stilstaand = g.doelVakje == null && !g.inLift && !g.uitcheckend
                    && !inRuimte && !buiten && g.eindbestemming == null && g.huidigVakje != null;

            if (stilstaand) {
                g.wachtTicks++;
                if (g.wachtTicks >= MAX_WACHT_TICKS) { g.summonTick = 0; g.wisRoute(); }
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
            if (!g.wachtOpRestaurant || g.wachtRestaurant == null || g.doelVakje != null) continue;

            Model.ruimte.Restaurant r = g.wachtRestaurant;
            if (!r.isVol()) {
                g.wachtOpRestaurant = false; g.wachtRestaurant = null;
                hotel.pathfinder.zetRoute(g, r);
            } else {
                Model.ruimte.Restaurant alt = vindNietVolRestaurant(hotel, g, r);
                if (alt != null) {
                    g.wachtOpRestaurant = false; g.wachtRestaurant = null;
                    hotel.pathfinder.zetRoute(g, alt);
                }
            }
        }
    }

    private Model.ruimte.Restaurant vindNietVolRestaurant(Hotel hotel, Gast gast, Model.ruimte.Restaurant uitgesloten) {
        Model.ruimte.Restaurant beste = null;
        int minAfstand = Integer.MAX_VALUE;
        for (Model.ruimte.Ruimte r : hotel.ruimtes) {
            if (!(r instanceof Model.ruimte.Restaurant) || r == uitgesloten) continue;
            Model.ruimte.Restaurant rest = (Model.ruimte.Restaurant) r;
            if (rest.isVol()) continue;
            int afstand = Math.abs(r.posX - gast.huidigVakje.x) + Math.abs(r.posY - gast.huidigVakje.y);
            if (afstand < minAfstand) { minAfstand = afstand; beste = rest; }
        }
        return beste;
    }
}
