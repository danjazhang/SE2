package Model;

public class Gebeurtenis {

    public int tijd;
    public String type;

    public String checkin = "checkin";
    public String checkout = "checkout";
    public String schoonmaak = "schoonmaak";
    public String brandalarm = "brandalarm";
    public String drukte = "drukte";

    public Gebeurtenis(int tijd, String type) {
        this.tijd = tijd;
        this.type = type;
    }

    public void voerUit() {}
}
