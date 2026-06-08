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
            if (!(p instanceof Gast)) continue;
            Gast g = (Gast) p;
            if (!g.moetUitstappen) continue;
            g.moetUitstappen = false;

            // zet gast op het lege vakje naast de lift op de huidige verdieping
            int uitstapX = hotel.lift.posX + 1;
            int uitstapY = hotel.lift.getHuidigeVerdieping();
            Vakje uitstapVakje = hotel.layout.krijgVakje(uitstapX, uitstapY);
            if (uitstapVakje != null) {
                if (g.huidigVakje != null) g.huidigVakje.verwijderPersoon(g);
                g.huidigVakje = uitstapVakje;
                uitstapVakje.voegPersoonToe(g);
            }

            // reset lift-status zodat zetRoute niet opnieuw via lift probeert te routeren
            g.gebruiktLift = false;
            g.wachtOpLift = false;

            // stuur gast direct naar zijn eindbestemming — altijd lopen, nooit opnieuw lift
            if (g.eindbestemming != null && hotel.pathfinder != null) {
                Model.ruimte.Ruimte bestemming = g.eindbestemming;
                g.eindbestemming = null;
                // gebruik doelvakje van de bestemming en loop er direct naartoe
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
            // gast staat op de wachtplek als hij op x=posX+1 staat
            boolean opWachtplek = g.huidigVakje.x == lift.posX + 1;
            if (opWachtplek) {
                // wacht als lift nog niet op dezelfde y-rij is
                g.wachtOpLift = lift.getHuidigeVerdieping() != g.huidigVakje.y;
            }
        }
    }
}
