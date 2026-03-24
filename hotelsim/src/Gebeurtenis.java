public class Gebeurtenis {

    // tijd waarop de gebeurtenis plaatsvindt
    int tijd;

    // type van de gebeurtenis
    String type;

    // mogelijke types van gebeurtenissen
    String checkin = "checkin";
    String checkout = "checkout";
    String schoonmaak = "schoonmaak";
    String brandalarm = "brandalarm";
    String drukte = "drukte";

    // constructor
    public Gebeurtenis(int tijd, String type){
        this.tijd = tijd;
        this.type = type;
    }

    // voert de gebeurtenis uit
    public void voerUit(){}

}
