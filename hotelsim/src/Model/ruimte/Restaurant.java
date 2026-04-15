package Model.ruimte;

import Model.persoon.Gast;

// Stelt het restaurant voor in het hotel
// Erft van Ruimte en reageert op eten events
public class Restaurant extends Ruimte {

    // het maximaal aantal gasten dat het restaurant kan bevatten
    public int capaciteit;

    // de gast die momenteel in het restaurant is
    public Gast gasten;

    // lege constructor
    public Restaurant() {}

    // laat een gast het restaurant betreden
    public void betreedRestaurant() {}

    // laat een gast het restaurant verlaten
    public void verlaatRestaurant() {}

    // controleer of het restaurant vol is
    public void isVol() {}
}
