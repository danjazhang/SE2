import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HotelManager {
    private int volgendeId= 1;
    //maak nieuwe HashMap om layout op te slaan 
    private Map<Integer, Layout> allLayouts = new HashMap<>();
    //maak nieuwe hashmap om geladen layouts als hotel object op te slaan
    private Map<Integer, Hotel> loadedHotels = new HashMap<>();

    //test om alles in te zien
    public Map<Integer, Layout> getAllLayouts() {
        return allLayouts;
    }

    //voeg nieuwe layout
    public int addLayout(String naam, Layout layout) {
        //geef een id en naam
        int id= volgendeId++;
        layout.id = id;
        layout.naam = naam;
        allLayouts.put(id, layout);
        return id;
    }

    public int addHotel(String naam, Hotel hotel) {
        int id = volgendeId++;
        hotel.layout.id = id;
        hotel.layout.naam = naam;

        allLayouts.put(id, hotel.layout);
        loadedHotels.put(id, hotel);

        return id;
    }

    //sla hotel op in map met id als sleutel
    public void loadHotel(int id, Hotel hotel){
        loadedHotels.put(id,hotel);
    }


    //krijg layout
    public Layout getLayout(int id) {
        return allLayouts.get(id);
    }


    //verwijder layout
    public void removeLayout(int id) {
        allLayouts.remove(id);
    }

    //krijg alle layouts
    //map bestaat uit keys en keySet geeft alle keys terug als een set
    public Set<Integer> getAllLayoutIds() {
        return allLayouts.keySet();
    }

    //vraagt een hotel op met id
    public Hotel getHotel(int id) {
        return loadedHotels.get(id);
    }
}