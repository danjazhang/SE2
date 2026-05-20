package Controller;

import Model.Hotel;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.ruimte.Lift;
import hotelevents.HotelEventManager;

import java.util.ArrayList;
import java.util.List;

public class SimulatieController {

    private HotelEventManager eventManager;
    private EventController eventController;
    private HotelController hotelController;

    private int snelheid = 1;
    private int tikTeller = 0;

    public SimulatieController(
            HotelEventManager eventManager,
            EventController eventController,
            HotelController hotelController
    ) {
        this.eventManager = eventManager;
        this.eventController = eventController;
        this.hotelController = hotelController;
    }

    public void start() {
        eventManager.start(1);
    }

    public void pauzeer() {
        eventManager.pauze();
    }

    public void stop() {
        eventManager.stop();
    }

    public void setSnelheid(int snelheid) {
        this.snelheid = snelheid;
    }

    public void pasSnelheidToe(String keuze) {
        switch (keuze) {
            case "Langzaam" -> snelheid = 0;
            case "Normaal" -> snelheid = 1;
            case "Snel" -> snelheid = 4;
            default -> snelheid = 1;
        }
    }

    public void tik() {

        Hotel hotel = hotelController.getHotel();
        if (hotel == null) return;

        tikTeller++;

        int stappen = 1;

        if (snelheid <= 0) {
            if (tikTeller % 2 != 0) {
                hotelController.notifyListeners();
                return;
            }
        } else if (snelheid >= 4) {
            stappen = snelheid;
        }

        for (int i = 0; i < stappen; i++) {

            // Lift tick
            if (hotel.lift != null) {
                hotel.lift.tik();
            }

            // Verwerk gasten die uit lift stappen
            verwerkUitstappendeGasten(hotel);

            // Verwerk gasten die wachten op lift
            verwerkWachtendeGasten(hotel);

            // Normale beweging
            List<Persoon> copy = new ArrayList<>(hotel.personen);
            for (Persoon p : copy) {
                p.beweeg();
            }
        }

        hotelController.notifyListeners();
    }

    private void verwerkUitstappendeGasten(Hotel hotel) {

        for (Persoon p : hotel.personen) {

            if (!(p instanceof Gast g)) continue;

            if (g.moetUitstappen) {

                g.moetUitstappen = false;

                // Zet gast terug op kaart bij lift
                Vakje liftVakje = hotel.layout.krijgVakje(
                        hotel.lift.posX,
                        hotel.lift.getHuidigeVerdieping()
                );

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

    private void verwerkWachtendeGasten(Hotel hotel) {

        Lift lift = hotel.lift;
        if (lift == null) return;

        for (Persoon p : hotel.personen) {

            if (!(p instanceof Gast g)) continue;

            if (!g.gebruiktLift) continue;
            if (g.inLift) continue;
            if (g.huidigVakje == null) continue;

            // Check of gast bij lift staat
            boolean bijLift = g.huidigVakje.ruimte instanceof Lift;

            if (bijLift) {

                // Lift is op deze verdieping
                if (lift.getHuidigeVerdieping() == g.huidigVakje.y) {

                    g.wachtOpLift = false;
                    // Lift laadt automatisch in via inladen()

                } else {

                    // Wacht op lift
                    g.wachtOpLift = true;
                }
            }
        }
    }
}
