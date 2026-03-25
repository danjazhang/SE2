package Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Beheert alle layouts en hotels in de simulatie
// Slaat layouts op in een map met een uniek id als sleutel
// Slaat geladen hotels op in een aparte map
public class HotelManager {

    // teller voor het genereren van unieke ids
    private int volgendeId = 1;

    // map van alle layouts, sleutel is het unieke id
    private Map<Integer, Layout> allLayouts = new HashMap<>();

    // map van alle geladen hotels, sleutel is het unieke id
    private Map<Integer, Hotel> loadedHotels = new HashMap<>();

    // voeg een nieuwe layout toe en geef het id terug
    public int addLayout(String naam, Layout layout) {
        int id = volgendeId++;
        layout.id = id;
        layout.naam = naam;
        allLayouts.put(id, layout);
        return id;
    }

    // sla een hotel op met een id als sleutel
    public void loadHotel(int id, Hotel hotel) { loadedHotels.put(id, hotel); }

    // geef een layout terug op basis van het id
    public Layout getLayout(int id) { return allLayouts.get(id); }

    // verwijder een layout op basis van het id
    public void removeLayout(int id) { allLayouts.remove(id); }

    // geef alle layout ids terug
    public Set<Integer> getAllLayoutIds() { return allLayouts.keySet(); }

    // geef een hotel terug op basis van het id
    public Hotel getHotel(int id) { return loadedHotels.get(id); }
}
