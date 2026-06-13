package Model;

import Model.layout.Layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Verantwoordelijkheid: alle geladen layouts en hotels opslaan en opzoeken via een id.
// HotelManager is als een woordenboek: je geeft een id en krijgt een Layout of Hotel terug.
public class HotelManager {

    // Het volgende id dat uitgedeeld wordt aan een nieuwe layout.
    // Begint bij 1 en telt elke keer met 1 op.
    private int volgendeId = 1;

    // Een map van alle layouts: sleutel is het id (int), waarde is de Layout.
    private Map<Integer, Layout> allLayouts = new HashMap<>();

    // Een map van alle geladen hotels: sleutel is het id (int), waarde is het Hotel.
    private Map<Integer, Hotel> loadedHotels = new HashMap<>();

    // Voeg een layout toe aan de map: geef hem een id, sla naam en id op in de layout,
    // en stop hem in de map. Geef het toegewezen id terug.
    // 'volgendeId++' betekent: gebruik de huidige waarde en verhoog daarna met 1.
    public int addLayout(String naam, Layout layout) {
        int id = volgendeId++;
        layout.id = id;
        layout.naam = naam;
        // 'allLayouts.put(id, layout)' betekent: sla op met id als sleutel en layout als waarde.
        allLayouts.put(id, layout);
        return id;
    }

    // Sla het hotel op in de map met het id als sleutel.
    public void loadHotel(int id, Hotel hotel) { loadedHotels.put(id, hotel); }

    // Geef de layout terug die hoort bij het opgegeven id.
    // 'allLayouts.get(id)' haalt de waarde op voor de sleutel id.
    public Layout getLayout(int id) { return allLayouts.get(id); }

    // Verwijder de layout met het opgegeven id uit de map.
    public void removeLayout(int id) { allLayouts.remove(id); }

    // Geef alle ids terug die in de layouts-map staan.
    // 'allLayouts.keySet()' geeft een set van alle sleutels.
    public Set<Integer> getAllLayoutIds() { return allLayouts.keySet(); }

    // Geef het hotel terug dat hoort bij het opgegeven id.
    public Hotel getHotel(int id) { return loadedHotels.get(id); }
}
