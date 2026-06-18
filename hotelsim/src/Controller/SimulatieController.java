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

    private HotelEventManager eventManager; // regelt events zoals scenario’s en timing van de simulatie
    private EventController eventController; // beheert speciale events zoals Godzilla
    private HotelController hotelController; // geeft toegang tot het hotelmodel en updates

    private boolean eventManagerGestart = false; // houdt bij of de simulatie actief is gestart
    private int tikTeller = 0; // telt het aantal “ticks” (simulatiestappen)
    private long startTijdMs = 0; // starttijd in milliseconden voor realtime weergave

    // gast wordt gesummond na dit aantal stilstaande ticks — instelbaar via setMaxWachtTicks()
    private int maxWachtTicks = 5;

    // summoning animatie duurt dit aantal ticks
    private static final int SUMMON_DUUR = 8;

    public SimulatieController(HotelEventManager eventManager, EventController eventController, HotelController hotelController) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;
    }

    // start de simulatie met een bepaald scenario
    public void start(int scenario) {
        startTijdMs = System.currentTimeMillis(); // onthoud starttijd
        tikTeller = 0; // reset tick teller
        eventManager.start(scenario); // start event systeem met gekozen scenario
        eventManagerGestart = true; // markeer dat simulatie draait
    }

    // pauzeert de simulatie (tijdelijk stoppen)
    public void pauzeer() {
        eventManager.pauze();
    }

    // stopt de simulatie volledig
    public void stop() {
        if (!eventManagerGestart) return; // voorkom stoppen als het niet gestart is
        eventManager.stop(); // stop event manager
        eventManagerGestart = false; // reset status
    }

    // stel de maximale wachttijd in voordat een gast gesummoned wordt
    public void setMaxWachtTicks(int ticks) {
        this.maxWachtTicks = ticks;
    }

    // past de snelheid van de simulatie aan
    public void pasSnelheidToe(String keuze) {
        switch (keuze) {
            case "Langzaam" -> eventManager.setHte(1500); // trage tick snelheid
            case "Normaal" -> eventManager.setHte(1000); // normale snelheid
            case "Snel" -> eventManager.setHte(10);   // zeer snelle simulatie
            default -> eventManager.setHte(1000);  // fallback naar normaal
        }
    }

    // geeft huidige tick teller terug
    public int getTikTeller() {
        return tikTeller;
    }

    // berekent hoe lang de simulatie al draait in hh:mm:ss formaat
    public String getRealTijd() {
        if (startTijdMs == 0) return "00:00:00"; // nog niet gestart
        long verstreken = System.currentTimeMillis() - startTijdMs; // tijd in ms
        long seconden = verstreken / 1000; // omrekenen naar seconden
        long uren = seconden / 3600; // uren berekenen
        long minuten = (seconden % 3600) / 60; // minuten binnen uur
        long sec = seconden % 60; // resterende seconden
        return String.format("%02d:%02d:%02d", uren, minuten, sec); // formatteren
    }

    // hoofd tick methode: wordt elke simulatiestap aangeroepen
    public void tik() {
        Hotel hotel = hotelController.getHotel(); // haal huidig hotel op
        if (hotel == null) return; // als er geen hotel is, stop

        tikTeller++; // verhoog tick teller

        if (hotel.lift != null) hotel.lift.tik(); // update lift logica

        // evacuatie-beheer bij brandalarm
        if (hotel.brandalarmActief) verwerkEvacuatieLoop(hotel);

        // gasten laten terugkeren na alarm
        verwerkTerugkerendeGastenNaAlarm(hotel);

        // verwerk uitstappen van gasten uit lift
        verwerkUitstappendeGasten(hotel);

        // controleer wachtposities van gasten bij lift
        verwerkWachtendeGasten(hotel);

        // verwerk wachttijden van gasten (stilstand -> summoning)
        verwerkWachttijden(hotel);

        // restaurant wachtrijen beheren
        verwerkRestaurantWachtrij(hotel);

        // Godzilla event logica uitvoeren
        GodzillaService godzilla = eventController.getGodzillaService();
        if (godzilla != null && hotel.godzillaActief) {
            godzilla.behandel(tikTeller); // laat Godzilla schade en vuur verspreiden
        }

        // kopie van personen om veilig te kunnen itereren
        List<Persoon> copy = new ArrayList<>(hotel.personen);
        for (Persoon p : copy) {
            if (p.gestorven) continue; // dode personen bewegen niet meer
            p.beweeg(); // laat persoon bewegen
        }

        // na beweging: controleer opnieuw brandende kolommen
        if (hotel.godzillaActief && godzilla != null) {
            for (int kolom : hotel.brandendeKolommen) {
                godzilla.markeerDodenOpKolom(kolom, tikTeller);
            }
        }

        // verwijder alle gestorven personen uit hotel
        if (hotel.godzillaActief) {
            hotel.personen.removeIf(p -> {
                if (p.gestorven) {
                    if (p.huidigVakje != null) {
                        p.huidigVakje.verwijderPersoon(p); // verwijder uit vakje
                        p.huidigVakje = null;
                    }
                    hotel.slachtoffers.add(p); // voeg toe aan slachtofferslijst
                    return true; // verwijderen uit lijst
                }
                return false;
            });

            // stop simulatie als Godzilla klaar is
            if (godzilla != null && godzilla.isKlaar()) {
                eventManager.stop();
            }
        }
        hotelController.notifyListeners(); // update UI of observers
    }

    // -----------------------------------------------------------------------
    // Evacuatie logica
    // -----------------------------------------------------------------------

    // behandelt evacuatie tijdens brandalarm
    private void verwerkEvacuatieLoop(Hotel hotel) {
        if (hotel.pathfinder == null || hotel.lobby == null) return;

        // bepaal buitenrij (boven lobby)
        int buitenY = hotel.lobby.posY - 1;

        // midden van lobby als uitgangspunt
        int midX = hotel.lobby.posX + hotel.lobby.breedte / 2;

        // uitgangsvakje buiten hotel
        Vakje uitgang = hotel.layout.krijgVakje(midX, buitenY);
        if (uitgang == null) return;

        // verwijder uitcheckende gasten die al buiten staan
        List<Persoon> teVerwijderen = new ArrayList<>();
        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;

            // als gast buiten staat en uitcheckt
            if (g.uitcheckend && g.huidigVakje != null && g.huidigVakje.y == buitenY) {
                g.huidigVakje.verwijderPersoon(g);
                g.huidigVakje = null;
                if (hotel.lift != null) hotel.lift.verwijderUitWachtrij(g);
                teVerwijderen.add(g);
            }
        }
        hotel.personen.removeAll(teVerwijderen);

        // check of iedereen buiten staat
        boolean iederBuiten = !hotel.personen.isEmpty();
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).uitcheckend) continue;
            if (p.huidigVakje == null || p.huidigVakje.y != buitenY) {
                iederBuiten = false;
                break;
            }
        }

        // Als iedereen buiten het gebouw staat, kan het brandalarm veilig uitgezet worden
        if (iederBuiten) {
            hotel.brandalarmActief = false;

            // Controleer of er een lift aanwezig is
            if (hotel.lift != null) {
                // Zet de lift weer aan
                hotel.lift.zetUitBedrijf(false);
                // Reset alle wachtrijen van de lift
                hotel.lift.resetWachtrijen();
            }

            // Doorloop alle personen in het hotel
            for (Persoon p : hotel.personen) {

                // Als iemand niet in de wereld staat, sla deze over
                if (p.huidigVakje == null) continue;
                p.wisRoute();

                // BEHANDEL GASTEN APART
                if (p instanceof Gast) {

                    // Cast algemene Persoon naar Gast zodat we gast-specifieke variabelen kunnen gebruiken
                    Gast g = (Gast) p;
                    // Markeer dat deze gast terugkeert naar normale situatie na het alarm
                    g.keertTerugNaAlarm = true;
                    // Stuur de gast eerst naar de lobby als tussenstap voordat hij terug naar kamer gaat
                    stuurGastNaarLobbyNaAlarm(hotel, g);

                    // BEHANDEL SCHOONMAKERS APART
                } else if (p instanceof Schoonmaker) {
                    // Cast naar Schoonmaker zodat we schoonmaker-specifieke data kunnen gebruiken
                    Schoonmaker s = (Schoonmaker) p;

                    if (s.bezig && s.kamer != null) {

                        // Plan opnieuw een route naar de kamer waar hij mee bezig was
                        hotel.pathfinder.zetRoute(s, s.kamer);

                        // Als de schoonmaker stond te wachten op een plek (bijv. trap of gang)
                    } else if (s.wachtVakje != null) {

                        // Plan een route naar die wachtruimte via trappen (geen lift)
                        hotel.pathfinder.zetRouteTrap(s, s.wachtVakje);
                    }
                }
            }

            verwerkWachtendeSchoonmaakTaken(hotel);
            return;
        }

        // zolang alarm actief is: iedereen naar uitgang sturen
        for (Persoon p : new ArrayList<>(hotel.personen)) {

            // als persoon geen positie heeft, overslaan
            if (p.huidigVakje == null) continue;

            // als persoon al buiten staat, overslaan
            if (p.huidigVakje.y == buitenY) continue;

            // alleen speciale logica voor gasten (niet voor schoonmakers of andere personen)
            if (p instanceof Gast) {
                Gast g = (Gast) p;
                // LIFT SITUATIE BIJ EVACUATIE
                // als gast bezig is met lift of in de lift zit
                if (g.wachtOpLift || g.inLift) {
                    // gast hoeft niet meer te wachten op de lift
                    g.wachtOpLift = false;
                    // gast gebruikt de lift niet meer tijdens evacuatie
                    g.gebruiktLift = false;
                    // ALS GAST ÉCHT IN DE LIFT ZIT → ERUIT HALEN
                    if (g.inLift && hotel.lift != null) {

                        // bereken vakje direct naast de lift op huidige verdieping
                        Vakje uv = hotel.layout.krijgVakje(
                                hotel.lift.posX + 1,
                                hotel.lift.getHuidigeVerdieping()
                        );

                        // alleen verplaatsen als dat vakje bestaat
                        if (uv != null) {

                            // verwijder gast uit huidige positie (lift vakje)
                            g.huidigVakje.verwijderPersoon(g);

                            // update referentie naar nieuwe locatie
                            g.huidigVakje = uv;

                            // voeg gast toe aan nieuw vakje in het hotel
                            uv.voegPersoonToe(g);
                        }

                        // gast zit niet meer in de lift
                        g.inLift = false;
                    }
                    p.evacueer(uitgang, hotel.pathfinder);
                    continue;
                }
            }

            // als geen route bestaat: evacueer
            boolean heeftEvacuatieRoute = p.doelVakje != null && p.doelVakje.y <= buitenY + 1;
            if (!heeftEvacuatieRoute) {
                p.evacueer(uitgang, hotel.pathfinder);
            }
        }
    }

    // stuurt gast eerst naar lobby na evacuatie
    private void stuurGastNaarLobbyNaAlarm(Hotel hotel, Gast g) {
        g.wachtOpLift = false;
        g.gebruiktLift = false;
        g.inLift = false;
        g.moetUitstappen = false;
        g.wachtOpRestaurant = false;
        g.wachtRestaurant = null;

        //lobby moet bestaan: hier is de uitgang(het midden van de lobby)
        if (hotel.lobby != null) {
            Vakje lobbyVakje = hotel.layout.krijgVakje(
                    hotel.lobby.posX + hotel.lobby.breedte / 2, hotel.lobby.posY);

            if (lobbyVakje != null) {
                hotel.pathfinder.zetRouteTrap(g, lobbyVakje);
            }
        }
    }

    // laat gasten terugkeren naar normale routes na alarm
    private void verwerkTerugkerendeGastenNaAlarm(Hotel hotel) {
        if (hotel.lobby == null || hotel.pathfinder == null) return;

        int buitenY = hotel.lobby.posY - 1;

        Vakje lobbyVakje = hotel.layout.krijgVakje(
                hotel.lobby.posX + hotel.lobby.breedte / 2, hotel.lobby.posY);

        // copy van lijst zodat we veilig kunnen itereren terwijl lijst verandert
        for (Persoon p : new ArrayList<>(hotel.personen)) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;

            if (!g.keertTerugNaAlarm || g.huidigVakje == null) continue;

            // nog buiten
            if (g.huidigVakje.y == buitenY) {
                if (g.doelVakje == null && lobbyVakje != null) {
                    hotel.pathfinder.zetRouteTrap(g, lobbyVakje);
                }
                continue;
            }

            // onderweg of bezig
            if (g.doelVakje != null || g.inLift || g.wachtOpLift) continue;

            // terug naar kamer
            if (g.kamer != null) {
                hotel.pathfinder.zetRoute(g, g.kamer);
            }
            // gast is nu volledig terug in normale simulatie
            g.keertTerugNaAlarm = false;
        }
    }

    // schoonmakers toewijzen aan wachtende kamers
    private void verwerkWachtendeSchoonmaakTaken(Hotel hotel) {
        if (hotel.wachtendeSchoonmaakKamers == null || hotel.wachtendeSchoonmaakKamers.isEmpty()) return;
        //lijst om bij te houden welke kamers zijn toegewezen
        List<Kamer> afgehandeld = new ArrayList<>();
        //copy van lijst voor betere verwerking
        for (Kamer kamer : new ArrayList<>(hotel.wachtendeSchoonmaakKamers)) {
            Schoonmaker vrij = vindVrijeSchoonmaker(hotel); //zoek schoonmaker
            if (vrij == null) break;

            vrij.maakKamerSchoon(kamer); //wijs taak toe aan schoonmaker
            hotel.pathfinder.zetRoute(vrij, kamer); //route van schoonmaker
            afgehandeld.add(kamer);
        }
        //kamer die door een schoonmaker wordt schoongemaakt
        hotel.wachtendeSchoonmaakKamers.removeAll(afgehandeld);
    }

    // zoekt een vrije schoonmaker
    private Schoonmaker vindVrijeSchoonmaker(Hotel hotel) {
        for (Persoon p : hotel.personen) {
            if (p instanceof Schoonmaker && !((Schoonmaker) p).bezig) {
                return (Schoonmaker) p;
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Lift logica
    // -----------------------------------------------------------------------

    // verwerkt gasten die uit de lift moeten stappen
    private void verwerkUitstappendeGasten(Hotel hotel) {
        for (Persoon p : hotel.personen) {
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;

            if (!g.moetUitstappen) continue;
            g.moetUitstappen = false;

            //vakje naast de lift waar gasten wachten
            Vakje uitstapVakje = hotel.layout.krijgVakje(
                    hotel.lift.posX + 1,
                    hotel.lift.getHuidigeVerdieping()
            );

            //gast gaat naar uitstapvakje
            if (uitstapVakje != null) {
                if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
                g.huidigVakje = uitstapVakje;
                uitstapVakje.voegPersoonToe(g);
            }

            g.gebruiktLift = false;
            g.wachtOpLift = false;

            // na lift: naar bestemming sturen
            if (g.eindbestemming != null && hotel.pathfinder != null) {
                Model.ruimte.Ruimte bestemming = g.eindbestemming; //bestemming wordt opgeslagen
                g.eindbestemming = null; //reset zodat het niet dubbel wordt opgeslagen

                // pak ingang van bestemming (meestal deur van kamer/ruimte)
                int[] ingang = bestemming.krijgIngang();

                // probeer eerst via ingang naar bestemming te gaan
                Vakje doel = hotel.layout.krijgVakje(ingang[0], ingang[1]);

                // fallback: als ingang niet bestaat → gebruik positie van ruimte zelf
                if (doel == null) {
                    doel = hotel.layout.krijgVakje(bestemming.posX, bestemming.posY);
                }

                // plan route naar bestemming via trap/logische route
                if (doel != null) {
                    hotel.pathfinder.zetRouteTrap(g, doel);
                }
            }
        }
    }

    // controleert of gasten moeten wachten op lift
    // controleert of gasten moeten wachten op lift
    private void verwerkWachtendeGasten(Hotel hotel) {

        // haal lift op uit hotel
        Lift lift = hotel.lift;

        // als er geen lift is niks doen
        if (lift == null) return;

        // loop door alle personen in het hotel
        for (Persoon p : hotel.personen) {
            // alleen gasten hebben liftgedrag
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;

            // alleen verwerken als gast: lift gebruikt, niet al in lift zit, een positie heeft
            if (!g.gebruiktLift || g.inLift || g.huidigVakje == null) continue;


            // CHECK OF GAST BIJ DE LIFT WACHT
            // X check: moet naast lift staan
            // Y check: moet op of boven begane grond zone zitten
            if (g.huidigVakje.x == lift.posX + 1 && g.huidigVakje.y >= lift.posY) {
                // gast wacht op lift als hij niet op dezelfde verdieping staat
                g.wachtOpLift = lift.getHuidigeVerdieping() != g.huidigVakje.y;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Wachttijden / summoning
    // -----------------------------------------------------------------------

    // controleert stilstaande gasten en start summoning indien nodig
    private void verwerkWachttijden(Hotel hotel) {

        // lijst met gasten die volledig verwijderd moeten worden uit de simulatie
        List<Persoon> teVerwijderen = new ArrayList<>();

        // loop door kopie van de personenlijst (veilig bij verwijderen tijdens iteratie)
        for (Persoon p : new ArrayList<>(hotel.personen)) {

            // alleen gasten worden verwerkt in dit systeem
            if (!(p instanceof Gast)) continue;

            Gast g = (Gast) p;

            // ======================================================
            // DIRECT VERWIJDEREN: UITCHECKEN + BUITEN
            // ======================================================
            // als gast aan het uitchecken is én al buiten staat
            // → dan hoeft hij niet meer in de simulatie te bestaan
            if (g.uitcheckend && g.huidigVakje != null && hotel.lobby != null
                    && g.huidigVakje.y == hotel.lobby.posY - 1) {

                // verwijder gast uit huidige tile
                g.huidigVakje.verwijderPersoon(g);

                // reset positie
                g.huidigVakje = null;

                // verwijder gast uit lift wachtrij (als hij daar nog in zat)
                if (hotel.lift != null) hotel.lift.verwijderUitWachtrij(g);

                // markeer gast voor volledige verwijdering uit lijst
                teVerwijderen.add(g);

                continue;
            }

            // ======================================================
            // SUMMONING ANIMATIE (VISUELE VERWIJDERING)
            // ======================================================
            // summonTick >= 0 betekent: gast zit in “verdwijn animatie”
            if (g.summonTick >= 0) {

                // verhoog animatie timer per tick
                g.summonTick++;

                // als animatie klaar is → gast echt verwijderen
                if (g.summonTick >= SUMMON_DUUR) {

                    // verwijder gast uit huidige vakje
                    if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);

                    // reset positie
                    g.huidigVakje = null;

                    // verwijder uit lift wachtrij indien nodig
                    if (hotel.lift != null) hotel.lift.verwijderUitWachtrij(g);

                    // markeer voor definitieve verwijdering
                    teVerwijderen.add(g);
                }

                // ga naar volgende gast (summoning heeft prioriteit)
                continue;
            }

            // ======================================================
            // CHECK: IS DE GAST STILSTAAND?
            // ======================================================
            // we bepalen hier of de gast “vast” staat zonder actie

            boolean inRuimte =
                    g.huidigVakje != null &&
                            g.huidigVakje.ruimte != null &&
                            !(g.huidigVakje.ruimte instanceof Model.ruimte.Lift) &&
                            !(g.huidigVakje.ruimte instanceof Model.ruimte.Trap) &&
                            !(g.huidigVakje.ruimte instanceof Model.ruimte.Lobby);

            // check of gast buiten het hotel staat
            boolean buiten =
                    hotel.lobby != null &&
                            g.huidigVakje != null &&
                            g.huidigVakje.y == hotel.lobby.posY - 1;

            // definitie van “stilstaand”:
            // gast doet niks, heeft geen route en zit niet in speciale toestand
            boolean stilstaand =
                    g.doelVakje == null &&
                            !g.inLift &&
                            !g.uitcheckend &&
                            !inRuimte &&
                            !buiten &&
                            g.eindbestemming == null &&
                            !g.keertTerugNaAlarm &&
                            g.huidigVakje != null;

            // ======================================================
            // STILSTAANDE GAST → WACHTTELLER OPBOUWEN
            // ======================================================
            if (stilstaand) {

                // verhoog aantal ticks dat gast stilstaat
                g.wachtTicks++;

                // als te lang stil → summon starten
                if (g.wachtTicks >= maxWachtTicks) {

                    // start summoning animatie
                    g.summonTick = 0;

                    // wis huidige route zodat gast niet meer beweegt
                    g.wisRoute();
                }

            } else {
                // als gast wel beweegt of bezig is → reset wachtteller
                g.wachtTicks = 0;
            }
        }

        // verwijder alle gemarkeerde gasten uit het hotelsysteem
        hotel.personen.removeAll(teVerwijderen);
    }


// ==========================================================
// RESTAURANT WACHTRIJ SYSTEEM
// ==========================================================

    // verwerkt wachtrijen voor restaurants
    private void verwerkRestaurantWachtrij(Hotel hotel) {

        // als pathfinder ontbreekt → kan geen routes plannen
        if (hotel.pathfinder == null) return;

        // loop door alle personen (kopie voor veiligheid)
        for (Persoon p : new ArrayList<>(hotel.personen)) {

            // alleen gasten hebben restaurant gedrag
            if (!(p instanceof Gast)) continue;

            Gast g = (Gast) p;

            // gast moet wachten + restaurant moet bestaan + geen actieve route
            if (!g.wachtOpRestaurant || g.wachtRestaurant == null || g.doelVakje != null) continue;

            Model.ruimte.Restaurant r = g.wachtRestaurant;

            // ======================================================
            // RESTAURANT IS NIET VOL
            // ======================================================
            if (!r.isVol()) {

                // stop met wachten
                g.wachtOpRestaurant = false;
                g.wachtRestaurant = null;

                // plan route naar restaurant
                hotel.pathfinder.zetRoute(g, r);

            } else {

                // ======================================================
                // RESTAURANT IS VOL → ZOEK ALTERNATIEF
                // ======================================================
                Model.ruimte.Restaurant alt = vindNietVolRestaurant(hotel, g, r);

                // als alternatief bestaat → gebruik dat restaurant
                if (alt != null) {

                    g.wachtOpRestaurant = false;
                    g.wachtRestaurant = null;

                    hotel.pathfinder.zetRoute(g, alt);
                }
            }
        }
    }


// ==========================================================
// ZOEK ALTERNATIEF RESTAURANT
// ==========================================================

    private Model.ruimte.Restaurant vindNietVolRestaurant(
            Hotel hotel, Gast gast, Model.ruimte.Restaurant uitgesloten) {

        // beste keuze tot nu toe
        Model.ruimte.Restaurant beste = null;

        // afstand die we willen minimaliseren
        int minAfstand = Integer.MAX_VALUE;

        // loop door alle ruimtes in het hotel
        for (Model.ruimte.Ruimte r : hotel.ruimtes) {

            // alleen restaurants, en niet het uitgesloten restaurant
            if (!(r instanceof Model.ruimte.Restaurant) || r == uitgesloten) continue;

            Model.ruimte.Restaurant rest = (Model.ruimte.Restaurant) r;

            // sla volle restaurants over
            if (rest.isVol()) continue;

            // bereken Manhattan afstand (x + y verschil)
            int afstand =
                    Math.abs(r.posX - gast.huidigVakje.x) +
                            Math.abs(r.posY - gast.huidigVakje.y);

            // kies het dichtstbijzijnde niet-volle restaurant
            if (afstand < minAfstand) {
                minAfstand = afstand;
                beste = rest;
            }
        }

        // geef beste optie terug (of null als geen beschikbaar)
        return beste;
    }
}