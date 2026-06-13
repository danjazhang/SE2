package Model;

// Verantwoordelijkheid: interface voor het observer-patroon in MVC.
// Een interface is een contract: elke klasse die ModelListener implementeert moet modelGewijzigd() hebben.
// HotelController roept modelGewijzigd() aan op alle geregistreerde listeners als het model verandert.
// De Views (HotelView, SimulatieView) implementeren deze interface zodat ze zichzelf kunnen hertekenen.
public interface ModelListener {

    // Wordt aangeroepen door HotelController als de data in het model veranderd is.
    // De View weet hierdoor dat hij zichzelf opnieuw moet tekenen.
    void modelGewijzigd();
}
