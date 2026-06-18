package Model;

// Verantwoordelijkheid: interface voor het observer-patroon in MVC.
// HotelController roept modelGewijzigd() aan op alle geregistreerde listeners als het model verandert.
// De Views (HotelView, SimulatieView) implementeren deze interface zodat ze zichzelf kunnen hertekenen.
public interface ModelListener {
    void modelGewijzigd();
}
