package Model.persoon;

import Model.ruimte.Kamer;
import Model.ruimte.Ruimte;

// Verantwoordelijkheid: een hotelgast bijhouden en zijn kamerbewegingen afhandelen.

public class Gast extends Persoon {

    // Het unieke nummer van deze gast, zodat je hem kunt opzoeken.
    public int gastId;

    // Het aantal sterren dat de gast wil: 1, 2, 3, 4 of 5.
    public int gewensteSterren;

    // De kamer die aan deze gast gekoppeld is. Begint als null (geen kamer).
    public Kamer kamer;

    // 'boolean uitcheckend' sla op of de gast aan het uitchecken is.
    // 'false' betekent: nog niet uitcheckend. 'true' betekent: wel uitcheckend.
    public boolean uitcheckend = false;

    // Sla op of de gast momenteel in de lift zit.
    // Als dit true is, beweegt de gast niet zelfstandig (de lift beweegt hem).
    public boolean inLift = false;

    // Sla op of de gast de lift gebruikt voor zijn route.
    public boolean gebruiktLift = false;

    // De verdieping waar de gast wil uitstappen uit de lift.
    // Begint op 1 (de lobby verdieping).
    public int gewensteVerdieping = 1;

    // Sla op of de gast aan het wachten is op de lift.
    // Als dit true is, staat de gast stil bij de liftdeur.
    public boolean wachtOpLift = false;

    // Sla op of de gast uit de lift moet stappen op de huidige verdieping.
    public boolean moetUitstappen = false;

    // De eindbestemming na de lift, bijvoorbeeld een kamer of restaurant.
    // Begint als null (nog geen eindbestemming).
    public Ruimte eindbestemming = null;

    // Constructor: wordt aangeroepen als je 'new Gast(gastId, gewensteSterren)' schrijft.
    // Sla gastId en gewensteSterren op in dit object. Kamer begint als null.
    public Gast(int gastId, int gewensteSterren) {
        this.gastId = gastId;
        this.gewensteSterren = gewensteSterren;
        this.kamer = null;
    }

    // Lege methode als placeholder voor het sturen naar een activiteit.
    public void gaNaarActiviteit() {}

    // Stuur de gast naar zijn kamer als hij een kamer heeft (kamer is niet null).
    // 'kamer.gastKomtBinnen(this)' betekent: roep op de kamer de methode gastKomtBinnen aan
    // en geef deze gast (this) mee als argument.
    public void gaNaarkamer() {
        if (kamer != null) {
            kamer.gastKomtBinnen(this);
        }
    }

    // Laat de gast zijn kamer verlaten als hij een kamer heeft (kamer is niet null).
    public void verlaatKamer() {
        if (kamer != null) {
            kamer.gastVerlaatKamer(this);
        }
    }
}
