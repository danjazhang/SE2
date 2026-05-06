package Model.ruimte;

import Model.persoon.Gast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Erft van Ruimte en heeft een aantal sterren, een gast en een schoon-status
public class Kamer extends Ruimte {

    // het aantal sterren van de kamer (1 t/m 5)
    public int sterren;

    //kamernummer
    public int kamernummer;

    // gasten die aan deze kamer gekoppeld zijn (ingecheckt)
    //true = gast is fysiek in de kamer, false = gast is ergens anders maar nog ingecheckt
    private Map<Gast, Boolean> ingecheckteGasten = new HashMap<>();

    // of de kamer schoon is
    public boolean schoon;

    // of de kamer bezet is
    private boolean bezet = false;

    // constructor: kamer begint schoon en zonder gast
    public Kamer() {
        this.schoon = true;
    }

    // koppel gast(en) aan de kamer
    public void koppelGast(Gast g) {
        //ingecheckt maar nog niet fysiek in de kamer
        ingecheckteGasten.put(g, false);
        g.kamer = this;
        zetBezet(true);
    }

    // ontkoppelen van kamer
    public void ontkoppelGast(Gast g) {
        //key is de sleutel/ eerste waarde in een hashmap in dit geval gast
        if (ingecheckteGasten.containsKey(g)) {
            //als gast nog fysiek in de kamer is, verwijder uit aanwezigen
            if (ingecheckteGasten.get(g)) {
                verlaat(g);
            }
            ingecheckteGasten.remove(g);
            g.kamer = null;
        }

        //kamer is vies na uitchecken van gasten
        if (ingecheckteGasten.isEmpty()) {
            zetBezet(false);
            schoon = false;
        }
    }

    //gast betreedt fysiek de kamer
    public void gastKomtBinnen(Gast g) {
        if (ingecheckteGasten.containsKey(g)) {
            ingecheckteGasten.put(g, true);
            betreed(g);
        }
    }

    public void gastVerlaatKamer(Gast g) {
        if (ingecheckteGasten.containsKey(g)) {
            ingecheckteGasten.put(g, false);
            verlaat(g);
        }
    }

    // maak de kamer schoon
    public void schoonmaken() {
        this.schoon = true;
    }

    //is de kamer bezet?
    public boolean isBezet() {
        return bezet;
    }

    // zet de kamer als bezet of vrij
    public void zetBezet(boolean bezet) {
        this.bezet = bezet;
    }

    //is een specifieke gast in de kamer?
    public boolean isGastAanwezig(Gast g) {
        //haal waarde op via sleutel (g) als die niet bestaat geeft die (g,false) terug ipv null
        return ingecheckteGasten.getOrDefault(g, false);
    }

    // geef alle gekoppelde gasten terug
    public List<Gast> getIngecheckteGasten() {
        return new ArrayList<>(ingecheckteGasten.keySet());
    }

    public boolean isSchoon() {
        return schoon;
    }

    // geef deze kamer terug als die vrij en schoon is, anders null
    @Override
    public Kamer getVrijeKamer() {
        if (!isBezet() && isSchoon()) {
            return this;
        }
        return null;
    }

    public int getKamernummer() {
        return kamernummer;
    }
}
