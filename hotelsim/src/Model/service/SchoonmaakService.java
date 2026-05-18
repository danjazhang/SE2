package Model.service;

import Model.Hotel;
import Model.IEventListener;
import Model.ILogger;
import Model.events.SchoonmaakEindEvent;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

import java.util.List;

// Verantwoordelijkheid: schoonmaaktaken toewijzen.
// Deze service voert de beslislogica uit bij een schoonmaak-noodgeval:
// gast zoeken, juiste kamer bepalen, vrije schoonmaker kiezen en daarna de route laten zetten.
public class SchoonmaakService implements IEventListener {

    private final Hotel hotel;
    private ILogger logger;

    public SchoonmaakService(Hotel hotel, ILogger logger) {
        this.hotel = hotel;
        this.logger = logger;
    }

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    // Reageer alleen op CLEANING_EMERGENCY.
    // Andere events horen niet bij de verantwoordelijkheid van deze service.
    @Override
    public void onEvent(HotelEvent event) {
        if (event.getEventType() != HotelEventType.CLEANING_EMERGENCY) return;

        Gast gast = vindGast(event.getGuestId());
        if (gast == null || gast.kamer == null) return;

        Schoonmaker schoonmaker = vindVrijeSchoonmaker();
        if (schoonmaker == null) return;

        SchoonmaakEindEvent eindEvent = new SchoonmaakEindEvent(event.getTime(), event.getGuestId());
        if (logger != null) {
            logger.log("[" + eindEvent.getTijd() + "] Schoonmaker: noodsituatie!");
        }

        stuurSchoonmakerNaarKamer(schoonmaker, gast.kamer);
    }

    // Zoek de gast op basis van het guestId uit het event.
    // De service gebruikt de gast alleen als tussenstap om de juiste kamer te vinden.
    private Gast vindGast(int gastId) {
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                return (Gast) p;
            }
        }
        return null;
    }

    // Kies de eerste schoonmaker die niet bezig is met een andere kamer.
    private Schoonmaker vindVrijeSchoonmaker() {
        for (Persoon p : hotel.personen) {
            if (p instanceof Schoonmaker && !((Schoonmaker) p).bezig) {
                return (Schoonmaker) p;
            }
        }
        return null;
    }

    // Geef de schoonmaker de kamer en gebruik daarna Pathfinder om een zichtbare looproute klaar te zetten.
    // De schoonmaker blijft uitvoerder; deze service doet alleen de toewijzing.
    private void stuurSchoonmakerNaarKamer(Schoonmaker schoonmaker, Kamer kamer) {
        if (schoonmaker.huidigVakje == null) return;

        Vakje doel = hotel.layout.krijgVakje(kamer.posX, kamer.posY);
        if (doel == null) return;

        List<Vakje> route = hotel.pathfinder.berekenRoute(schoonmaker.huidigVakje, doel);
        if (route.isEmpty()) return;

        schoonmaker.maakKamerSchoon(kamer);
        schoonmaker.zetRouteNaarKamer(route.get(0));
        for (int i = 1; i < route.size(); i++) {
            schoonmaker.voegTussendoelToe(route.get(i));
        }

        if (logger != null) {
            logger.log("Schoonmaker gaat naar kamer " + kamer.getKamernummer());
        }
    }
}
