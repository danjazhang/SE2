package Controller;

import Model.Hotel;
import Model.Scenario;
import Model.Klok;
import Model.Gebeurtenis;
import Model.ModelListener;
import View.EventLog;

import java.util.List;

// Controller klasse: beheert de simulatie
// Loopt door de tijd en verwerkt gebeurtenissen per tijdstap
// Implementeert ModelListener zodat het genotificeerd wordt als het Model verandert
public class Simulatie implements ModelListener {

    // de klok die de tijd bijhoudt
    public Klok klok;

    // het scenario met alle geplande gebeurtenissen
    public Scenario scenario;

    // het hotel model
    public Hotel hotel;

    // constructor: koppel hotel, scenario en klok
    // registreer de simulatie als observer bij het hotel
    public Simulatie(Hotel hotel, Scenario scenario, Klok klok) {
        this.hotel = hotel;
        this.scenario = scenario;
        this.klok = klok;
        hotel.voegListenerToe(this);
    }

    // wordt aangeroepen door Hotel als de data veranderd is (Observer pattern)
    @Override
    public void modelGewijzigd() {
        System.out.println("Controller: model is gewijzigd");
    }

    // start de simulatie: loop 100 tijdstappen door
    public void start() {
        for (int i = 0; i < 100; i++) {
            klok.tick();
            int tijd = klok.huidigeTijd;

            // haal alle gebeurtenissen op voor dit tijdstip
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
            EventLog.log("Gast checkt in");
        }
        if (g.type.equals("schoonmaak")) {
            System.out.println("Kamer wordt schoongemaakt");
        }
        if (g.type.equals("brandalarm")) {
            System.out.println("Brandalarm!");
        }
    }
}
