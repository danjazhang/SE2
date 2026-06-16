package Model.persoon;

import Model.ruimte.Kamer;
import Model.ruimte.Ruimte;

// Klasse voor hotelgast
public class Gast extends Persoon {

    // Unieke id
    public int gastId;

    // Gewenste sterrenniveau
    public int gewensteSterren;

    // Kamer van de gast
    public Kamer kamer;

    // Check-out status
    public boolean uitcheckend = false;

    // Lift status
    public boolean inLift = false;

    // Gebruikt lift of niet
    public boolean gebruiktLift = false;

    // Gewenste verdieping voor lift
    public int gewensteVerdieping = 1;

    // Wacht op lift
    public boolean wachtOpLift = false;

    // Moet uitstappen uit lift
    public boolean moetUitstappen = false;

    // Eindbestemming na lift
    public Ruimte eindbestemming = null;

    // Aantal ticks dat de gast stilstaat (geen doel, niet in lift)
    public int wachtTicks = 0;

    // Summoning animatie: -1 = niet actief, 0..8 = animatie bezig
    public int summonTick = -1;

    // Wacht op toegang tot een vol restaurant (telt niet mee voor summoning)
    public boolean wachtOpRestaurant = false;

    // Het restaurant waar de gast op wacht
    public Model.ruimte.Restaurant wachtRestaurant = null;

    // True zolang de gast na een brandalarm nog terug het hotel in en naar zijn kamer moet.
    // Tijdens deze fase negeren we nieuwe activiteiten-events tijdelijk.
    public boolean keertTerugNaAlarm = false;

    public Gast(int gastId, int gewensteSterren) {

        this.gastId = gastId;
        this.gewensteSterren = gewensteSterren;
        this.kamer = null;
    }

    // Placeholder activiteit
    public void gaNaarActiviteit() {}

    // Laat gast naar kamer gaan
    public void gaNaarkamer() {

        if (kamer != null) {
            kamer.gastKomtBinnen(this);
        }
    }

    // Laat gast kamer verlaten
    public void verlaatKamer() {

        if (kamer != null) {
            kamer.gastVerlaatKamer(this);
        }
    }

    @Override
    public boolean isGast() { return true; }

    @Override
    public String getStatusTekst() {
        String locatie;
        if (inLift) {
            locatie = "in lift";
        } else if (huidigVakje == null) {
            locatie = "geen positie";
        } else if (huidigVakje.ruimte != null) {
            locatie = huidigVakje.ruimte.getNaam();
        } else {
            locatie = "(" + huidigVakje.x + "," + huidigVakje.y + ")";
        }

        String activiteit;
        if (uitcheckend) {
            activiteit = "aan het uitchecken";
        } else if (inLift) {
            activiteit = "in lift";
        } else if (wachtOpLift) {
            activiteit = "wacht op lift";
        } else if (kamer != null && huidigVakje != null && huidigVakje.ruimte == kamer) {
            activiteit = "in kamer";
        } else if (doelVakje != null) {
            activiteit = "onderweg";
        } else {
            activiteit = "wacht";
        }

        return "Gast " + gastId + " (" + gewensteSterren + "★) : " + locatie + " — " + activiteit;
    }
}
