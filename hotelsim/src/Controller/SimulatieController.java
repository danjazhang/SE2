package Controller;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Lift;
import hotelevents.HotelEventManager;

import java.util.ArrayList;
import java.util.List;

// Verantwoordelijkheid: de simulatie starten, pauzeren, stoppen en elke tick de beweging uitvoeren.
public class SimulatieController {

    // De library-eventmanager om de simulatie te starten, te pauzeren of te stoppen.
    private HotelEventManager eventManager;

    // De EventController voor de verbinding met de library.
    private EventController eventController;

    // De HotelController voor toegang tot het hotel.
    private HotelController hotelController;

    // De snelheid van de simulatie: 0 = langzaam, 1 = normaal, 4 = snel.
    private int snelheid = 1;

    // Het aantal ticks dat er verstreken is sinds het begin van de simulatie.
    private int tikTeller = 0;

    // Het tijdstip in milliseconden waarop de simulatie gestart is, voor de realtime klok.
    private long startTijdMs = 0;

    // Constructor: sla de drie controllers op.
    public SimulatieController(HotelEventManager eventManager, EventController eventController, HotelController hotelController) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;
    }

    // Start de simulatie met het opgegeven scenario en sla de starttijd op.
    // 'System.currentTimeMillis()' geeft de huidige tijd in milliseconden terug.
    public void start(int scenario) {
        startTijdMs = System.currentTimeMillis();
        tikTeller = 0;
        eventManager.start(scenario);
    }

    // Pauzeer de simulatie.
    public void pauzeer() { eventManager.pauze(); }

    // Stop de simulatie.
    public void stop() { eventManager.stop(); }

    // Stel de snelheid direct in op de opgegeven waarde.
    public void setSnelheid(int snelheid) { this.snelheid = snelheid; }

    // Vertaal een snelheidskeuze als tekst naar een getal en sla op.
    // 'Langzaam' wordt 0, 'Normaal' wordt 1, 'Snel' wordt 4.
    public void pasSnelheidToe(String keuze) {
        switch (keuze) {
            case "Langzaam" -> snelheid = 0;
            case "Normaal"  -> snelheid = 1;
            case "Snel"     -> snelheid = 4;
            default         -> snelheid = 1;
        }
    }

    // Geef de huidige tikTeller terug.
    public int getTikTeller() { return tikTeller; }

    // Geef de verstreken realtime terug als tekst in het formaat HH:mm:ss.
    // 'System.currentTimeMillis() - startTijdMs' is het verschil in milliseconden.
    // Gedeeld door 1000 is het het aantal verstreken seconden.
    // 'String.format("%02d:%02d:%02d", ...)' formatteert de getallen altijd met minimaal 2 cijfers.
    public String getRealTijd() {
        if (startTijdMs == 0) return "00:00:00";
        long verstreken = System.currentTimeMillis() - startTijdMs;
        long seconden = verstreken / 1000;
        long uren = seconden / 3600;
        long minuten = (seconden % 3600) / 60;
        long sec = seconden % 60;
        return String.format("%02d:%02d:%02d", uren, minuten, sec);
    }

    // Voer één simulatietick uit: beweeg de lift, verwerk uitstappende en wachtende gasten,
    // laat alle personen één stap bewegen, en notificeer de views.
    public void tik() {
        Hotel hotel = hotelController.getHotel();
        // Als er geen hotel is (null), stop dan.
        if (hotel == null) return;

        tikTeller++;

        // Bereken hoeveel stappen er per tick uitgevoerd worden op basis van de snelheid.
        int stappen = 1;
        if (snelheid <= 0) {
            // Langzaam: voer alleen elke tweede tick een stap uit.
            if (tikTeller % 2 != 0) { hotelController.notifyListeners(); return; }
        } else if (snelheid >= 4) {
            // Snel: voer meerdere stappen per tick uit.
            stappen = snelheid;
        }

        // Voer het opgegeven aantal stappen uit.
        for (int i = 0; i < stappen; i++) {
            // Laat de lift één tick bewegen als die bestaat (niet null).
            if (hotel.lift != null) hotel.lift.tik();
            // Verwerk gasten die uit de lift moeten stappen.
            verwerkUitstappendeGasten(hotel);
            // Verwerk gasten die bij de liftdeur wachten.
            verwerkWachtendeGasten(hotel);
            // Maak een kopie van de personenlijst zodat we veilig kunnen itereren
            // ook als de lijst tijdens het bewegen wijzigt (gast wordt verwijderd bij checkout).
            List<Persoon> copy = new ArrayList<>(hotel.personen);
            for (Persoon p : copy) p.beweeg();
            // Notificeer de views zodat ze de nieuwe posities tekenen.
            hotelController.notifyListeners();
            // Wacht 225 milliseconden voor de animatiesnelheid.
            try { Thread.sleep(225); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    // Zet uitstappende gasten op het juiste vakje en stuur ze naar hun eindbestemming.
    private void verwerkUitstappendeGasten(Hotel hotel) {
        for (Persoon p : hotel.personen) {
            // Als p geen Gast is, sla hem dan over.
            if (!(p instanceof Gast g)) continue;
            // Als moetUitstappen niet gelijk is aan true, sla hem dan over.
            if (!g.moetUitstappen) continue;
            // Reset moetUitstappen zodat dit maar één keer verwerkt wordt.
            g.moetUitstappen = false;
            // Zet de gast op het liftuitstappuntvakje van de huidige verdieping.
            Vakje liftVakje = hotel.layout.krijgVakje(hotel.lift.posX, hotel.lift.getHuidigeVerdieping());
            if (liftVakje != null) { g.huidigVakje = liftVakje; liftVakje.voegPersoonToe(g); }
            // Als de gast een eindbestemming heeft, zet hem dan op weg naar die bestemming.
            if (g.eindbestemming != null && g.getPathfinder() != null) {
                Vakje doel = hotel.layout.krijgVakje(g.eindbestemming.posX, g.eindbestemming.posY);
                if (doel != null) g.zetDoel(doel);
            }
        }
    }

    // Controleer voor elke wachtende gast of de lift op zijn verdieping is aangekomen.
    // Als de lift er nog niet is, zet wachtOpLift op true zodat de gast stilstaat.
    private void verwerkWachtendeGasten(Hotel hotel) {
        Lift lift = hotel.lift;
        // Als er geen lift is (null), stop dan.
        if (lift == null) return;
        for (Persoon p : hotel.personen) {
            if (!(p instanceof Gast g)) continue;
            // Sla gasten over die de lift niet gebruiken, al in de lift zitten of geen positie hebben.
            if (!g.gebruiktLift || g.inLift || g.huidigVakje == null) continue;
            // 'bijLift' is true als de gast naast de lift staat op de juiste verdieping.
            boolean bijLift = g.huidigVakje.x == hotel.lift.posX + 1 &&
                    g.huidigVakje.y == hotel.lift.getHuidigeVerdieping();
            // Als de gast bij de lift staat maar de lift nog niet op zijn verdieping is, laat hem dan wachten.
            if (bijLift) g.wachtOpLift = lift.getHuidigeVerdieping() != g.huidigVakje.y;
        }
    }
}
