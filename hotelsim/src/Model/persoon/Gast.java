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
}