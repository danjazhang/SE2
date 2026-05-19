package Controller;

import Model.Hotel;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import hotelevents.HotelEventManager;

public class SimulatieController {

    // event manager voor start/stop/pauze
    private HotelEventManager eventManager;

    // event controller
    private EventController eventController;

    // hotel controller
    private HotelController hotelController;

    // simulatiesnelheid
    private int snelheid = 1;

    // teller voor langzame modus
    private int tikTeller = 0;

    // constructor
    public SimulatieController(
            HotelEventManager eventManager,
            EventController eventController,
            HotelController hotelController
    ) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;
    }

    // simulatie starten
    public void start() {
        eventManager.start(1);
    }

    // simulatie pauzeren
    public void pauzeer() {
        eventManager.pauze();
    }

    // simulatie stoppen
    public void stop() {
        eventManager.stop();
    }

    // snelheid direct instellen
    public void setSnelheid(int snelheid) {
        this.snelheid = snelheid;
    }

    // snelheid via dropdown
    public void pasSnelheidToe(String keuze) {

        switch (keuze) {

            case "Langzaam":
                snelheid = 0;
                break;

            case "Normaal":
                snelheid = 1;
                break;

            case "Snel":
                snelheid = 4;
                break;

            default:
                snelheid = 1;
                break;
        }
    }

    // simulatie tick
    public void tik() {

        Hotel hotel = hotelController.getHotel();

        if (hotel == null) return;

        tikTeller++;

        int stappenPerTik = 1;

        // langzaam
        if (snelheid <= 0) {

            if (tikTeller % 2 != 0) {

                hotelController.notifyListeners();
                return;
            }
        }

        // snel
        else if (snelheid >= 4) {

            stappenPerTik = snelheid;
        }

        // meerdere stappen uitvoeren
        for (int stap = 0; stap < stappenPerTik; stap++) {

            // lift verwerken
            if (hotel.lift != null) {

                // lift beweegt
                hotel.lift.tik();

                // passagiers markeren als in lift
                for (Persoon p : hotel.lift.getPassagiers()) {

                    if (p instanceof Gast g) {
                        g.inLift = true;
                    }
                }

                // gasten die net uit lift kwamen
                // terug op kaart zetten
                for (Persoon p : hotel.personen) {

                    if (p instanceof Gast g) {

                        if (!g.inLift && g.huidigVakje == null) {

                            var vakje = hotel.layout.krijgVakje(
                                    hotel.lift.posX,
                                    hotel.lift.getHuidigeVerdieping()
                            );

                            if (vakje != null) {

                                g.huidigVakje = vakje;

                                vakje.voegPersoonToe(g);

                                // BELANGRIJK:
                                // opnieuw doel zetten zodat gast verder loopt
                                if (g.kamer != null) {

                                    var kamerVakje = hotel.layout.krijgVakje(
                                            g.kamer.posX,
                                            g.kamer.posY
                                    );

                                    g.zetDoel(kamerVakje);
                                }
                            }
                        }
                    }
                }
            }

            // personen bewegen
            java.util.List<Persoon> personenKopie =
                    new java.util.ArrayList<>(hotel.personen);

            for (Persoon p : personenKopie) {

                p.beweeg();
            }
        }

        // scherm vernieuwen
        hotelController.notifyListeners();
    }
}