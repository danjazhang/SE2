package Controller;
import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Lift;
import hotelevents.HotelEventManager;
import java.util.ArrayList;
import java.util.List;

// Verantwoordelijkheid: simulatie starten, pauzeren en stoppen
public class SimulatieController {

    // Verantwoordelijk voor het starten/pauzeren/stoppen van events
    private HotelEventManager eventManager;

    // Controller voor events
    private EventController eventController;

    // Controller die het hotel beheert
    private HotelController hotelController;

    // Simulatiesnelheid
    private int snelheid = 1;

    // Teller voor ticks
    private int tikTeller = 0;

    public SimulatieController(HotelEventManager eventManager, EventController eventController,HotelController hotelController
    ) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;
    }

    // start de simulatie met het opgegeven scenario (1 t/m 4)
    public void start(int scenario) {
        eventManager.start(scenario);
    }

    // Pauzeer de simulatie
    public void pauzeer() {
        eventManager.pauze();
    }

    // Stop de simulatie
    public void stop() {
        eventManager.stop();
    }

    // Zet snelheid direct
    public void setSnelheid(int snelheid) {
        this.snelheid = snelheid;
    }

    // Zet snelheid op basis van tekst
    public void pasSnelheidToe(String keuze) {

        switch (keuze) {

            case "Langzaam" -> snelheid = 0;
            case "Normaal" -> snelheid = 1;
            case "Snel" -> snelheid = 4;
            default -> snelheid = 1;
        }
    }

    // Wordt elke simulatie-tick uitgevoerd
    public void tik() {

        // Haal hotel op
        Hotel hotel = hotelController.getHotel();

        // Stop als hotel niet bestaat
        if (hotel == null) {
            return;
        }

        // Verhoog teller
        tikTeller++;

        // Aantal simulatiestappen per tick
        int stappen = 1;

        // Langzame modus
        // Alleen elke 2 ticks uitvoeren
        if (snelheid <= 0) {

            if (tikTeller % 2 != 0) {
                hotelController.notifyListeners();
                return;
            }

            // Snelle modus
        } else if (snelheid >= 4) {

            stappen = snelheid;
        }

        // Voer stappen uit
        for (int i = 0; i < stappen; i++) {

            // Laat lift bewegen
            if (hotel.lift != null) {
                hotel.lift.tik();
            }

            // Verwerk gasten die uit de lift stappen
            verwerkUitstappendeGasten(hotel);

            // Verwerk gasten die op de lift wachten
            verwerkWachtendeGasten(hotel);

            // Maak kopie om concurrent modification te voorkomen
            List<Persoon> copy = new ArrayList<>(hotel.personen);

            // Laat alle personen bewegen
            for (Persoon p : copy) {
                p.beweeg();
            }

            // Update listeners / UI na elke stap
            hotelController.notifyListeners();

            //kleine pauze zodat repaint zichtbaar wordt
            try {
                Thread.sleep(225);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Verwerk gasten die net uit de lift zijn gekomen
    private void verwerkUitstappendeGasten(Hotel hotel) {

        for (Persoon p : hotel.personen) {

            // Alleen gasten
            if (!(p instanceof Gast g)) {
                continue;
            }

            // Alleen gasten die moeten uitstappen
            if (g.moetUitstappen) {

                // Reset flag
                g.moetUitstappen = false;

                // Pak vakje naast lift
                Vakje liftVakje = hotel.layout.krijgVakje(
                        hotel.lift.posX,
                        hotel.lift.getHuidigeVerdieping()
                );

                // Zet gast op kaart
                if (liftVakje != null) {

                    g.huidigVakje = liftVakje;
                    liftVakje.voegPersoonToe(g);
                }

                // Zet route naar eindbestemming
                if (g.eindbestemming != null && g.getPathfinder() != null) {

                    Vakje doel = hotel.layout.krijgVakje(
                            g.eindbestemming.posX,
                            g.eindbestemming.posY
                    );

                    if (doel != null) {
                        g.zetDoel(doel);
                    }
                }
            }
        }
    }

    // Verwerk gasten die op de lift wachten
    private void verwerkWachtendeGasten(Hotel hotel) {

        Lift lift = hotel.lift;

        // Stop als lift niet bestaat
        if (lift == null) {
            return;
        }

        for (Persoon p : hotel.personen) {

            // Alleen gasten
            if (!(p instanceof Gast g)) {
                continue;
            }

            // Gast gebruikt geen lift
            if (!g.gebruiktLift) {
                continue;
            }

            // Gast zit al in lift
            if (g.inLift) {
                continue;
            }

            // Geen positie
            if (g.huidigVakje == null) {
                continue;
            }

            // Controleer of gast naast de lift staat
            boolean bijLift =
                    g.huidigVakje.x == hotel.lift.posX + 1 &&
                            g.huidigVakje.y == hotel.lift.getHuidigeVerdieping();

            if (bijLift) {

                // Lift staat op dezelfde verdieping
                if (lift.getHuidigeVerdieping() == g.huidigVakje.y) {

                    // Gast hoeft niet meer te wachten
                    g.wachtOpLift = false;

                } else {

                    // Gast wacht op lift
                    g.wachtOpLift = true;
                }
            }
        }
    }
}