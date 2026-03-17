import java.util.List;

public class Simulatie {

    Klok klok;
    Scenario scenario;
    Hotel hotel;

    // constructor
    public Simulatie(Hotel hotel, Scenario scenario, Klok klok){
        this.hotel = hotel;
        this.scenario = scenario;
        this.klok = klok;
    }

    // start de simulatie
    public void start(){

        // simpele loop
        for(int i = 0; i < 100; i++){

            // tijd vooruit
            klok.tick();

            int tijd = klok.huidigeTijd;

            // krijg gebeurtenissen op dit moment
            List<Gebeurtenis> events = scenario.krijgGebeurtenissen(tijd);

            if(events != null){
                for(Gebeurtenis g : events){

                    // hier komt later de echte logica
                    verwerkGebeurtenis(g);
                }
            }
        }
    }

    // verwerkt één gebeurtenis
    public void verwerkGebeurtenis(Gebeurtenis g){

        // voorbeeld logica (later uitbreiden)

        if(g.type.equals("checkin")){
            System.out.println("Gast checkt in");
        }

        if(g.type.equals("schoonmaak")){
            System.out.println("Kamer wordt schoongemaakt");
        }

        if(g.type.equals("brandalarm")){
            System.out.println("Brandalarm!");
        }
    }

}
