package Model;

// Observer interface voor het MVC Observer pattern
// Klassen die willen weten wanneer het Model verandert implementeren deze interface
// Hotel roept modelGewijzigd() aan op alle geregistreerde listeners
// HotelPanel (View) en Simulatie (Controller) implementeren deze interface
public interface ModelListener {
    // wordt aangeroepen door Hotel als de data veranderd is
    void modelGewijzigd();
}