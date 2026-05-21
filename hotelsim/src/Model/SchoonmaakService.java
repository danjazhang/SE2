package Model;

import Model.events.IEventListener;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

// Verantwoordelijkheid: schoonmaak-noodgevallen vertalen naar een concrete taak.
// Deze service kiest dus niet alleen het juiste event, maar zoekt daarna ook
// de juiste gast, de juiste kamer en een vrije schoonmaker.
// De schoonmaker zelf blijft uitvoerder en hoeft daardoor geen eventlogica te kennen.
public class SchoonmaakService implements IEventListener {

    private final Hotel hotel;
    private final PersonenService personenService;
    private ILogger logger;

    public SchoonmaakService(Hotel hotel, ILogger logger) {
        this.hotel = hotel;
        this.logger = logger;
        this.personenService = new PersonenService(hotel);
    }

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    // Reageer alleen op een schoonmaak-noodgeval uit de library.
    // Andere events vallen buiten de verantwoordelijkheid van deze service.
    @Override
    public void onEvent(HotelEvent event) {
        if (event.getEventType() != HotelEventType.CLEANING_EMERGENCY) return;
        if (hotel == null || hotel.pathfinder == null) return;

        Gast gast = personenService.vindGast(event.getGuestId());
        if (gast == null || gast.kamer == null) return;

        Kamer kamer = gast.kamer;
        Schoonmaker schoonmaker = personenService.vindVrijeSchoonmaker();
        if (schoonmaker == null) return;

        if (logger != null) {
            logger.log("[" + event.getTime() + "] Schoonmaker: noodsituatie!");
        }

        // Geef de schoonmaker eerst de taak en laat daarna Pathfinder de route zetten.
        // Zo blijft de routeberekening op één centrale plek in het model.
        schoonmaker.maakKamerSchoon(kamer);
        hotel.pathfinder.zetRoute(schoonmaker, kamer);

        if (logger != null) {
            logger.log("Schoonmaker gaat naar kamer " + kamer.getKamernummer());
        }
    }
}
