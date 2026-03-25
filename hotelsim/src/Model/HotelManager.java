package Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HotelManager {
    private int volgendeId = 1;
    private Map<Integer, Layout> allLayouts = new HashMap<>();
    private Map<Integer, Hotel> loadedHotels = new HashMap<>();

    public int addLayout(String naam, Layout layout) {
        int id = volgendeId++;
        layout.id = id;
        layout.naam = naam;
        allLayouts.put(id, layout);
        return id;
    }

    public void loadHotel(int id, Hotel hotel) { loadedHotels.put(id, hotel); }
    public Layout getLayout(int id) { return allLayouts.get(id); }
    public void removeLayout(int id) { allLayouts.remove(id); }
    public Set<Integer> getAllLayoutIds() { return allLayouts.keySet(); }
    public Hotel getHotel(int id) { return loadedHotels.get(id); }
}
