package Controller;

import Model.GodzillaService;
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
    private boolean eventManagerGestart = false;
    private int tikTeller = 0;
    private long startTijdMs = 0;

    // gast wordt gesummond na dit aantal stilstaande ticks — instelbaar via setMaxWachtTicks()
    private int maxWachtTicks = 5;
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
        eventManagerGestart = true;
    }

    public void pauzeer() { eventManager.pauze(); }
    public void stop() {
        if (!eventManagerGestart) return;
        eventManager.stop();
        eventManagerGestart = false;
    }

    // stel de maximale wachttijd in voordat een gast gesummoned wordt
    public void setMaxWachtTicks(int ticks) { this.maxWachtTicks = ticks; }

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

        // evacuatie-beheer
        if (hotel.brandalarmActief) verwerkEvacuatieLoop(hotel);

        // na alarm: gasten terug naar binnen via lobby
        verwerkTerugkerendeGastenNaAlarm(hotel);

        verwerkUitstappendeGasten(hotel);
        verwerkWachtendeGasten(hotel);
        verwerkWachttijden(hotel);
        verwerkRestaurantWachtrij(hotel);

        // Godzilla: breidt vuur uit en markeert doden
        GodzillaService godzilla = eventController.getGodzillaService();
        if (godzilla != null && hotel.godzillaActief) {
            godzilla.behandel(tikTeller);
        }

        List<Persoon> copy = new ArrayList<>(hotel.personen);
        for (Persoon p : copy) {
            if (p.gestorven) continue;
            p.beweeg();
        }

        // na beweging: controleer brandende kolommen opnieuw
        if (hotel.godzillaActief && godzilla != null) {
            for (int kolom : hotel.brandendeKolommen) {
                godzilla.markeerDodenOpKolom(kolom, tikTeller);
            }
        }

        // verwijder gestorven personen aan het einde van de tick
        if (hotel.godzillaActief) {
            hotel.personen.removeIf(p -> {
                if (p.gestorven) {
                    if (p.huidigVakje != null) {
                        p.huidigVakje.verwijderPersoon(p);
                        p.huidigVakje = null;
                    }
                    hotel.slachtoffers.add(p);
                    return true;
                }
                return false;
            });

            if (godzilla != null && godzilla.isKlaar()) {
                eventManager.stop();
            }
        }

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
            hotel.brandalarmActief = false;
            if (hotel.lift != null) {
                hotel.lift.zetUitBedrijf(false);
                hotel.lift.resetWachtrijen();
            }

            for (Persoon p : hotel.personen) {
                if (p.huidigVakje == null) continue;
                p.wisRoute();

                if (p instanceof Gast) {
                    Gast g = (Gast) p;
                    // gasten keren terug via lobby — vandaar normale routing naar kamer
                    g.keertTerugNaAlarm = true;
                    stuurGastNaarLobbyNaAlarm(hotel, g);
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

        // alarm actief: iedereen zonder evacuatieroute sturen
        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (p.huidigVakje == null) continue;
            if (p.huidigVakje.y == buitenY) continue;

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

    // Stuur gast eerst naar de lobby als tussenstop na evacuatie
    private void stuurGastNaarLobbyNaAlarm(Hotel hotel, Gast g) {
        g.wachtOpLift       = false;
        g.gebruiktLift      = false;
        g.inLift            = false;
        g.moetUitstappen    = false;
        g.wachtOpRestaurant = false;
        g.wachtRestaurant   = null;
        if (hotel.lobby != null) {
            Vakje lobbyVakje = hotel.layout.krijgVakje(
                    hotel.lobby.posX + hotel.lobby.breedte / 2, hotel.lobby.posY);
            if (lobbyVakje != null) {
                hotel.pathfinder.zetRouteTrap(g, lobbyVakje);
            }
        }
    }

    // Na alarm: gasten in lobby zonder doel sturen naar hun kamer via normale routing
    private void verwerkTerugkerendeGastenNaAlarm(Hotel hotel) {
        if (hotel.lobby == null || hotel.pathfinder == null) return;

        int buitenY = hotel.lobby.posY - 1;
        Vakje lobbyVakje = hotel.layout.krijgVakje(
                hotel.lobby.posX + hotel.lobby.breedte / 2, hotel.lobby.posY);

        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;
            if (!g.keertTerugNaAlarm || g.huidigVakje == null) continue;

            // nog buiten en geen doel: opnieuw naar lobby sturen
            if (g.huidigVakje.y == buitenY) {
                if (g.doelVakje == null && lobbyVakje != null) {
                    hotel.pathfinder.zetRouteTrap(g, lobbyVakje);
                }
                continue;
            }

            // nog onderweg of in lift: wachten
            if (g.doelVakje != null || g.inLift || g.wachtOpLift) continue;

            // in lobby of hoger: stuur naar kamer via normale routing (lift of trap)
            if (g.kamer != null) {
                hotel.pathfinder.zetRoute(g, g.kamer);
            }
            g.keertTerugNaAlarm = false;
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

    // Lift-afhandeling

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

    // Summoning

    private void verwerkWachttijden(Hotel hotel) {
        List<Persoon> teVerwijderen = new ArrayList<>();

        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;

            // uitcheckende gast op buiten-rij: direct verwijderen
            if (g.uitcheckend && g.huidigVakje != null && hotel.lobby != null
                    && g.huidigVakje.y == hotel.lobby.posY - 1) {
                g.huidigVakje.verwijderPersoon(g);
                g.huidigVakje = null;
                if (hotel.lift != null) hotel.lift.verwijderUitWachtrij(g);
                teVerwijderen.add(g);
                continue;
            }

            // summoning animatie loopt
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
                    && !inRuimte && !buiten && g.eindbestemming == null
                    && !g.keertTerugNaAlarm && g.huidigVakje != null;

            if (stilstaand) {
                g.wachtTicks++;
                if (g.wachtTicks >= maxWachtTicks) { g.summonTick = 0; g.wisRoute(); }
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
                g.wachtOpRestaurant = false;
                g.wachtRestaurant   = null;
                hotel.pathfinder.zetRoute(g, r);
            } else {
                Model.ruimte.Restaurant alt = vindNietVolRestaurant(hotel, g, r);
                if (alt != null) {
                    g.wachtOpRestaurant = false;
                    g.wachtRestaurant   = null;
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
