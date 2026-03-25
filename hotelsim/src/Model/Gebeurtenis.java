package Model;

// Stelt een gebeurtenis voor in de simulatie
// Heeft een tijdstip en een type (bijv. "checkin", "schoonmaak", "brandalarm")
public class Gebeurtenis {

    // het tijdstip waarop de gebeurtenis plaatsvindt
    public int tijd;

    // het type van de gebeurtenis
    public String type;

    // vaste strings voor de mogelijke types
    public String checkin = "checkin";
    public String checkout = "checkout";
    public String schoonmaak = "schoonmaak";
    public String brandalarm = "brandalarm";
    public String drukte = "drukte";

    // constructor: maak een gebeurtenis aan met een tijdstip en type
    public Gebeurtenis(int tijd, String type) {
        this.tijd = tijd;
        this.type = type;
    }

    // voer de gebeurtenis uit
    public void voerUit() {}
}
