package Controller;

import Model.Hotel;
import Model.Scenario;
import Model.Klok;
import Model.Gebeurtenis;
import Model.ModelListener;

import java.util.List;

public class Simulatie implements ModelListener {

    public Klok klok;
    public Scenario scenario;
    public Hotel hotel;

    public Simulatie(Hotel hotel, Scenario scenario, Klok klok) {
        this.hotel = hotel;
        this.scenario = scenario;
        this.klok = klok;
        hotel.voegListenerToe(this);
    }

    @Override
    public void modelGewijzigd() {
        System.out.println("Controller: model is gewijzigd");
    }

    public void start() {
        for (int i = 0; i < 100; i++) {
            klok.tick();
            int tijd = klok.huidigeTijd;

            List<Gebeurtenis> events = scenario.krijgGebeurtenissen(tijd);

            if (events != null) {
                for (Gebeurtenis g : events) {
                    verwerkGebeurtenis(g);
                }
            }
        }
    }

    public void verwerkGebeurtenis(Gebeurtenis g) {
        if (g.type.equals("checkin")) {
            System.out.println("Gast checkt in");
        }
        if (g.type.equals("schoonmaak")) {
            System.out.println("Kamer wordt schoongemaakt");
        }
        if (g.type.equals("brandalarm")) {
            System.out.println("Brandalarm!");
        }
    }
}
