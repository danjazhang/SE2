package Model.persoon;

import Model.ruimte.Kamer;
import Model.ruimte.Ruimte;

// Gast
public class Gast extends Persoon {

    public int gastId;

    public int gewensteSterren;

    public Kamer kamer;

    public boolean uitcheckend = false;

    // lift flags
    public boolean inLift = false;

    public boolean gebruiktLift = false;

    // gewenste verdieping voor lift
    public int gewensteVerdieping = 1;

    public boolean wachtOpLift = false;
    public boolean moetUitstappen = false;
    public Ruimte eindbestemming = null;

    public Gast(int gastId, int gewensteSterren) {

        this.gastId = gastId;
        this.gewensteSterren = gewensteSterren;
        this.kamer = null;
    }

    public void gaNaarActiviteit() {}

    public void gaNaarkamer() {

        if (kamer != null) {
            kamer.gastKomtBinnen(this);
        }
    }

    public void verlaatKamer() {

        if (kamer != null) {
            kamer.gastVerlaatKamer(this);
        }
    }
}