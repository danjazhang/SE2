package Model;

import Model.layout.Layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HotelManager {
    private int volgendeId = 1;

    //map voor alle layouts
    private Map<Integer, Layout> allLayouts = new HashMap<>();

    //map voor alle geladen layouts en opslaan als hotel object
    private Map<Integer, Hotel> loadedHotels = new HashMap<>();

    public int addLayout(String naam, Layout layout) {
        //geef layout een id en naam en sla dat op
        int id = volgendeId++;
        layout.id = id;
        layout.naam = naam;
        //id is sleutel, layout is waarde
        //dus je kan met id de layout vinden
        allLayouts.put(id, layout);
        return id;
    }

    //sla hotel op in loadedHotels map met id als sleutel
    public void loadHotel(int id, Hotel hotel) { loadedHotels.put(id, hotel); }

    // geef hotel terug op basis van id
    public Hotel getHotel(int id) { return loadedHotels.get(id); }
}