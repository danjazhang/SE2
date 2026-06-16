package Controller;

import Model.GodzillaService;
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
    }

    public void pauzeer() { eventManager.pauze(); }
    public void stop() { eventManager.stop(); }

    // stel de maximale wachttijd in voordat een gast gesummoned wordt
    public void setMaxWachtTicks(int ticks) { this.maxWachtTicks = ticks; }

    // Snelheid wordt nu alleen nog via de library geregeld.
    // We passen dus de HTE van HotelEventManager aan in plaats van lokaal
    // extra sleeps of meerdere stappen per tick te gebruiken.
    public void pasSnelheidToe(String keuze) {
        switch (keuze) {
            case "Langzaam" -> {
                eventManager.setHte(1500);
            }
            case "Normaal"  -> {
                eventManager.setHte(1000);
            }
            case "Snel"     -> {
                eventManager.setHte(10);
            }
            default         -> {
                eventManager.setHte(1000);
            }
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

        // Deze methode verwerkt precies één lokale simulatietick.
        // De library bepaalt hoe snel tik() wordt aangeroepen via de ingestelde HTE.
        tikTeller++;

        if (hotel.lift != null) hotel.lift.tik();

        // Zolang het brandalarm actief is, blijft deze methode controleren of iedereen al buiten staat
        // en zo niet, of iedereen nog steeds een geldige evacuatieroute heeft.
        if (hotel.brandalarmActief) verwerkEvacuatieLoop(hotel);

        // Nadat het alarm voorbij is, komen gasten eerst gecontroleerd terug via de lobby.
        // Pas daarna mogen ze weer normaal naar hun kamer of naar nieuwe activiteiten gestuurd worden.
        verwerkTerugkerendeGastenNaAlarm(hotel);
        verwerkUitstappendeGasten(hotel);
        verwerkWachtendeGasten(hotel);
        verwerkWachttijden(hotel);
        verwerkRestaurantWachtrij(hotel);

        // Eerst breidt Godzilla het vuur uit.
        // Personen worden hier alleen als gestorven gemarkeerd; echte verwijdering gebeurt later in deze tick.
        GodzillaService godzilla = eventController.getGodzillaService();
        if (godzilla != null && hotel.godzillaActief) {
            godzilla.behandel(tikTeller);
        }

        List<Persoon> copy = new ArrayList<>(hotel.personen);
        for (Persoon p : copy) {
            if (p.gestorven) continue;
            p.beweeg();
        }

        // Na de beweging controleren we opnieuw alle brandende kolommen.
        // Zo sterft ook iemand die pas in deze tick een brandende kolom binnenloopt.
        if (hotel.godzillaActief && godzilla != null) {
            for (int kolom : hotel.brandendeKolommen) {
                godzilla.markeerDodenOpKolom(kolom, tikTeller);
            }
        }

        // Gestorven personen worden bewust pas op het einde van de tick verwijderd.
        // Daardoor blijft de volgorde van "markeren -> bewegen overslaan -> verwijderen" altijd stabiel.
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

            // Zodra alle kolommen gebrand hebben, stopt de simulatie volledig.
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
        int midX = hotel.lobby.posX + hotel.lobby.breedte / 2;
        Vakje uitgang = hotel.layout.krijgVakje(midX, buitenY);
        if (uitgang == null) return;

        boolean iederBuiten = !hotel.personen.isEmpty();
        for (Persoon p : hotel.personen) {
            if (p.huidigVakje == null || p.huidigVakje.y != buitenY) {
                iederBuiten = false;
                break;
            }
        }

        if (iederBuiten) {
            // Pas hier eindigt het alarm echt.
            // Niet na een vaste HTE, maar alleen wanneer niemand meer binnen zit.
            hotel.brandalarmActief = false;
            if (hotel.lift != null) hotel.lift.zetUitBedrijf(false);

            for (Persoon p : hotel.personen) {
                if (p.huidigVakje == null) continue;
                p.wisRoute();
                if (p instanceof Gast) {
                    Gast g = (Gast) p;
                    // Gasten keren na een evacuatie eerst terug naar de lobby.
                    // Vanuit daar krijgen ze later opnieuw hun normale route naar de kamer.
                    g.keertTerugNaAlarm = true;
                    stuurGastNaarLobbyNaAlarm(hotel, g);
                } else if (p instanceof Schoonmaker) {
                    Schoonmaker s = (Schoonmaker) p;
                    // Schoonmakers mogen hun oude taak hervatten of terug naar hun wachtvakje gaan.
                    if (s.bezig && s.kamer != null) {
                        hotel.pathfinder.zetRoute(s, s.kamer);
                    } else if (s.wachtVakje != null) {
                        hotel.pathfinder.zetRouteTrap(s, s.wachtVakje);
                    }
                }
            }
            return;
        }

        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (p.huidigVakje == null) continue;
            if (p.huidigVakje.y == buitenY) continue;

            if (p instanceof Gast) {
                Gast g = (Gast) p;
                // Gasten die nog in of bij de lift vastzitten, moeten eerst uit die lifttoestand gehaald worden.
                // Daarna krijgen ze alsnog een route via de trap naar buiten.
                if (g.wachtOpLift || g.inLift) {
                    g.wachtOpLift = false;
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

            // Iemand telt als "al aan het evacueren" zodra zijn doel al richting lobby/buiten ligt.
            boolean heeftEvacuatieRoute = p.doelVakje != null && p.doelVakje.y <= buitenY + 1;
            if (!heeftEvacuatieRoute) {
                p.evacueer(uitgang, hotel.pathfinder);
            }
        }
    }

    // Na het alarm sturen we een gast eerst terug naar een vakje in de lobby.
    // Van buiten rechtstreeks opnieuw naar een kamer/faciliteit sturen gaf eerder vastlopers,
    // daarom gebruiken we de lobby bewust als veilige tussenstap.
    private void stuurGastNaarLobbyNaAlarm(Hotel hotel, Gast g) {
        g.wachtOpLift = false;
        g.gebruiktLift = false;
        g.inLift = false;
        g.moetUitstappen = false;
        g.wachtOpRestaurant = false;
        g.wachtRestaurant = null;

        if (hotel.lobby != null) {
            Vakje lobbyVakje = hotel.layout.krijgVakje(hotel.lobby.posX + hotel.lobby.breedte / 2, hotel.lobby.posY);
            if (lobbyVakje != null) {
                hotel.pathfinder.zetRouteTrap(g, lobbyVakje);
            }
        }
    }

    private void verwerkTerugkerendeGastenNaAlarm(Hotel hotel) {
        if (hotel.lobby == null || hotel.pathfinder == null) return;

        int buitenY = hotel.lobby.posY - 1;
        Vakje lobbyVakje = hotel.layout.krijgVakje(hotel.lobby.posX + hotel.lobby.breedte / 2, hotel.lobby.posY);

        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;
            if (!g.keertTerugNaAlarm || g.huidigVakje == null) continue;

            // Als een gast buiten blijft hangen zonder doel, geven we opnieuw dezelfde route naar de lobby.
            if (g.huidigVakje.y == buitenY) {
                if (g.doelVakje == null && lobbyVakje != null) {
                    hotel.pathfinder.zetRouteTrap(g, lobbyVakje);
                }
                continue;
            }

            // Zolang de gast nog onderweg is of nog in een lifttoestand zit, doen we hier niets extra.
            if (g.doelVakje != null || g.inLift || g.wachtOpLift) continue;

            // Zodra de gast terug binnen is en geen tijdelijke route meer heeft,
            // mag hij opnieuw de gewone routing gebruiken, inclusief liftkeuze.
            if (g.kamer != null) {
                hotel.pathfinder.zetRoute(g, g.kamer);
            }
            g.keertTerugNaAlarm = false;
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
                if (g.wachtTicks >= maxWachtTicks) {
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
