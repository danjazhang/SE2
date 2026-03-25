package Model;

import java.util.ArrayList;
import java.util.List;

public class HotelEventManager {
    private List<HotelEventListener> listeners = new ArrayList<>();

    public void register(HotelEventListener listener) {
        listeners.add(listener);
    }

    public void fire(HotelEvent event) {
        for (HotelEventListener l : listeners) {
            l.notify(event);
        }
    }

    public void start(int guestId) {
        fire(new HotelEvent(HotelEventType.CHECK_IN,           10, guestId));
        fire(new HotelEvent(HotelEventType.NEED_FOOD,          20, guestId));
        fire(new HotelEvent(HotelEventType.GOTO_FITNESS,       30, guestId));
        fire(new HotelEvent(HotelEventType.GOTO_CINEMA,        40, guestId));
        fire(new HotelEvent(HotelEventType.START_CINEMA,       50, guestId));
        fire(new HotelEvent(HotelEventType.CLEANING_EMERGENCY, 60, guestId));
        fire(new HotelEvent(HotelEventType.CHECK_OUT,          70, guestId));
        fire(new HotelEvent(HotelEventType.EVACUATE,           80, guestId));
        fire(new HotelEvent(HotelEventType.GODZILLA,           90, guestId));
    }
}
