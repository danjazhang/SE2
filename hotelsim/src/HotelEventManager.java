
import java.util.ArrayList;
import java.util.List;

public class HotelEventManager {

    private List<HotelEventListener> listeners = new ArrayList<>();
    private int hte = 100; // standaard minimaal 100 ms
    private boolean running = false;

    // registreert een listener
    public void register(HotelEventListener listener) {
        listeners.add(listener);
    }

    // verwijdert een listener
    public void deregister(HotelEventListener listener) {
        listeners.remove(listener);
    }

    // stelt de snelheid in
    public void setHte(int hte) {
        if (hte < 100) {
            this.hte = 100;
        } else {
            this.hte = hte;
        }
    }

    // start simulatie
    public void start(String scenario) {
        running = true;
        System.out.println("Simulatie gestart met scenario: " + scenario);
    }

    // pauze / hervat
    public void pauze() {
        System.out.println( "Gepauzeerd");
    }

    // stop simulatie
    public void stop() {
        running = false;
        System.out.println("Simulatie gestopt");
    }

    // observer pattern core
    private void notifyListeners(HotelEvent evt) {
        for (HotelEventListener listener : listeners) {
            listener.notify(evt);
        }
    }

    // event trigger
    public void triggerEvent(HotelEvent evt) {
        notifyListeners(evt);
    }
}