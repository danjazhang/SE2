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

    // starttijd in milliseconden voor de realtime klok
    private long startTijdMs = 0;

    public SimulatieController(HotelEventManager eventManager, EventController eventController, HotelController hotelController) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;
    }

    // start de simulatie met het opgegeven scenario en sla de starttijd op
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
            case "Snel"     -> snelheid = 4;
            default         -> snelheid = 1;
        }
    }

    // geef het huidige ticknummer terug voor de HTE weergave
    public int getTikTeller() { return tikTeller; }

    // geef de verstreken realtime terug als HH:mm:ss, onafhankelijk van de ticks
    public String getRealTijd() {
        if (startTijdMs == 0) return "00:00:00";
        long verstreken = System.currentTimeMillis() - startTijdMs;
        long seconden = verstreken / 1000;
        long uren = seconden / 3600;
        long minuten = (seconden % 3600) / 60;
        long sec = seconden % 60;
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
        } else if (snelheid >= 4) {
            stappen = snelheid;
        }

        for (int i = 0; i < stappen; i++) {
            if (hotel.lift != null) hotel.lift.tik();
            verwerkUitstappendeGasten(hotel);
            verwerkWachtendeGasten(hotel);
            List<Persoon> copy = new ArrayList<>(hotel.personen);
            for (Persoon p : copy) p.beweeg();
            hotelController.notifyListeners();
            try { Thread.sleep(225); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    private void verwerkUitstappendeGasten(Hotel hotel) {
        for (Persoon p : hotel.personen) {
            if (!(p instanceof Gast g)) continue;
            if (!g.moetUitstappen) continue;
            g.moetUitstappen = false;

            // Zet gast op de gang naast de lift (x=lift+1) op de huidige verdieping.
            // Niet op de lift-kolom zelf, want dan raakt de gast vast in de lift-ruimte.
            int gangX = hotel.lift.posX + 1;
            int gangY = hotel.lift.getHuidigeVerdieping();
            Vakje gangVakje = hotel.layout.krijgVakje(gangX, gangY);
            if (gangVakje != null) {
                if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
                g.huidigVakje = gangVakje;
                gangVakje.voegPersoonToe(g);
            }

            // stuur gast naar zijn eindbestemming
            if (g.eindbestemming != null && hotel.pathfinder != null) {
                hotel.pathfinder.zetRoute(g, g.eindbestemming);
            }
        }
    }

    private void verwerkWachtendeGasten(Hotel hotel) {
        Lift lift = hotel.lift;
        if (lift == null) return;
        for (Persoon p : hotel.personen) {
            if (!(p instanceof Gast g)) continue;
            if (!g.gebruiktLift || g.inLift || g.huidigVakje == null) continue;
            // gast staat bij de lift als hij op x=lift.posX+1 staat op de gang-rij van zijn verdieping
            boolean bijLift = g.huidigVakje.x == hotel.lift.posX + 1;
            if (bijLift) g.wachtOpLift = lift.getHuidigeVerdieping() != g.huidigVakje.y;
        }
    }
}
