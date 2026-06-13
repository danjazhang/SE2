package Model.ruimte;

import Model.persoon.Gast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Verantwoordelijkheid: een hotelkamer bijhouden met sterren, gasten en schoonmaakstatus.
// Kamer erft alles van Ruimte via 'extends Ruimte'.
public class Kamer extends Ruimte {

    // Het aantal sterren van deze kamer: 1 t/m 5.
    public int sterren;

    // Het unieke kamernummer, bijvoorbeeld 101 of 203.
    public int kamernummer;

    // Een map die per gast bijhoudt of hij fysiek in de kamer is.
    // 'Map<Gast, Boolean>' betekent: de sleutel is een Gast, de waarde is true of false.
    // true betekent: gast is fysiek aanwezig in de kamer.
    // false betekent: gast is ingecheckt maar momenteel ergens anders.
    private Map<Gast, Boolean> ingecheckteGasten = new HashMap<>();

    // Sla op of de kamer schoon is. Begint als true (schoon).
    public boolean schoon;

    // Sla op of de kamer bezet is (iemand is ingecheckt).
    // 'private' betekent: alleen via isBezet() en zetBezet() te bereiken.
    private boolean bezet = false;

    // Geef terug of de kamer bezet is.
    public boolean isBezet() {
        return bezet;
    }

    // Zet de bezetsstatus op de meegegeven waarde (true of false).
    public void zetBezet(boolean bezet) {
        this.bezet = bezet;
    }

    // Constructor: kamer begint schoon en zonder gasten.
    public Kamer() {
        this.schoon = true;
    }

    // Koppel gast g aan deze kamer: voeg hem toe aan de map met waarde false (nog niet fysiek aanwezig),
    // wijs deze kamer toe aan de gast, en zet de kamer als bezet.
    public void koppelGast(Gast g) {
        ingecheckteGasten.put(g, false);
        g.kamer = this;
        zetBezet(true);
    }

    // Ontkoppel gast g van deze kamer.
    // 'ingecheckteGasten.containsKey(g)' betekent: als de map de gast als sleutel bevat.
    // 'ingecheckteGasten.get(g)' haalt de waarde (true of false) op voor deze gast.
    // Als de waarde true is, is de gast fysiek aanwezig: roep dan verlaat() aan.
    public void ontkoppelGast(Gast g) {
        if (ingecheckteGasten.containsKey(g)) {
            if (ingecheckteGasten.get(g)) {
                verlaat(g);
            }
            // Verwijder de gast volledig uit de map en wis de kamerkoppeling bij de gast.
            ingecheckteGasten.remove(g);
            g.kamer = null;
        }
        // Als de map nu leeg is, zijn er geen gasten meer: zet bezet op false en schoon op false.
        if (ingecheckteGasten.isEmpty()) {
            zetBezet(false);
            schoon = false;
        }
    }

    // Markeer dat gast g de kamer fysiek betreedt.
    // Alleen als de gast al ingecheckt is (in de map staat), zet zijn waarde op true en roep betreed() aan.
    public void gastKomtBinnen(Gast g) {
        if (ingecheckteGasten.containsKey(g)) {
            ingecheckteGasten.put(g, true);
            betreed(g);
        }
    }

    // Markeer dat gast g de kamer fysiek verlaat.
    // Zet zijn waarde in de map op false en roep verlaat() aan.
    public void gastVerlaatKamer(Gast g) {
        if (ingecheckteGasten.containsKey(g)) {
            ingecheckteGasten.put(g, false);
            verlaat(g);
        }
    }

    // Zet schoon op true zodat de kamer als schoon gemarkeerd is.
    public void schoonmaken() {
        this.schoon = true;
    }

    // Geef terug of gast g fysiek in de kamer is.
    // 'getOrDefault(g, false)' betekent: haal de waarde op voor g, en als g niet in de map staat geef false terug.
    public boolean isGastAanwezig(Gast g) {
        return ingecheckteGasten.getOrDefault(g, false);
    }

    // Geef een lijst van alle ingecheckte gasten terug.
    // 'ingecheckteGasten.keySet()' geeft alle sleutels (gasten) uit de map.
    // 'new ArrayList<>(...)' maakt een kopie zodat de originele map niet gewijzigd kan worden.
    public List<Gast> getIngecheckteGasten() {
        return new ArrayList<>(ingecheckteGasten.keySet());
    }

    // Geef terug of de kamer schoon is.
    public boolean isSchoon() {
        return schoon;
    }

    // '@Override' betekent: deze methode vervangt getVrijeKamer() van de bovenliggende klasse Ruimte.
    // Als de kamer niet bezet is (!isBezet()) én schoon is (isSchoon()), geef dan deze kamer (this) terug.
    // Anders geef null terug.
    @Override
    public Kamer getVrijeKamer() {
        if (!isBezet() && isSchoon()) {
            return this;
        }
        return null;
    }

    // Bouw een string van sterren op basis van het aantal sterren van de kamer.
    // 'label += "★"' betekent: voeg een stersteken toe aan de string label.
    // Na de lus bevat label evenveel sterren als sterren-waarde, bijvoorbeeld "★★★".
    public String getSterrenLabel() {
        String label = "";
        for (int i = 0; i < sterren; i++) {
            label += "★";
        }
        return label;
    }

    // Geef het kamernummer terug.
    public int getKamernummer() {
        return kamernummer;
    }
}
