import java.util.List;
public class Hotel {
    //breedte en hoogte van het hotel
    int breedte;
    int hoogte;

    Layout layout;

    // lijst van alle ruimtes binnen het hotel
    List <Ruimte> ruimtes;

    //lijst van alle personnen gast en schoonmakers
    List <Persoon> personen;

// lift en trap in het hotel
    Lift lift;
    Trap trap;
    //constructor
    public Hotel(){}
     // laadt het layout van het hotel uit een bestand
    public void laadLayoutBestand(){

    }
    // voegt een persoon toe aan het hotel
    public void voegPersoonToe(Persoon p){

    }
    // geeft de ruimte terug op positie
    public Ruimte krijgRuimteOp(int x, int y) {
        return null; //TODO: implementatie later toevoegen
    }
}

