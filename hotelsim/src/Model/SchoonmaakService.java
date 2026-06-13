package Model;

import Model.events.IEventListener;
import Model.persoon.Gast;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

// Verantwoordelijkheid: schoonmaak-noodgevallen (CLEANING_EMERGENCY) vertalen naar een concrete taak.
// Deze service zoekt de juiste gast, de juiste kamer en een vrije schoonmaker,
// en stuurt de schoonmaker vervolgens naar de kamer.
// De schoonmaker zelf hoeft geen eventlogica te kennen.
// SchoonmaakService implementeert IEventListener zodat EventController hem events kan sturen.
public class SchoonmaakService implements IEventListener {

    // Het hotel zodat we gasten en schoonmakers kunnen opzoeken.
    private final Hotel hotel;

    // Service voor het opzoeken van gasten en schoonmakers.
    private final PersonenService personenService;

    // Logger voor het sturen van berichten naar de GUI.
    private ILogger logger;

    // Constructor: sla het hotel en de logger op en maak een personenService aan.
    public SchoonmaakService(Hotel hotel, ILogger logger) {
        this.hotel = hotel;
        this.logger = logger;
        this.personenService = new PersonenService(hotel);
    }

    // Stel een nieuwe logger in.
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    // '@Override' betekent: deze methode vervangt onEvent() van de interface IEventListener.
    // Wordt aangeroepen door EventController bij elk binnenkomend event.
    // Reageert alleen op CLEANING_EMERGENCY, alle andere events worden genegeerd.
    @Override
    public void onEvent(HotelEvent event) {
        // Als het eventtype niet gelijk is aan (!=) CLEANING_EMERGENCY, stop dan meteen.
        if (event.getEventType() != HotelEventType.CLEANING_EMERGENCY) return;

        // Als het hotel of de pathfinder leeg is (null), kan er geen route berekend worden, stop dan.
        if (hotel == null || hotel.pathfinder == null) return;

        // Zoek de gast op basis van het gastId in het event.
        Gast gast = personenService.vindGast(event.getGuestId());

        // Als de gast niet gevonden is (null) of geen kamer heeft (null), stop dan.
        if (gast == null || gast.kamer == null) return;

        // Sla de kamer van de gast op als lokale variabele.
        Kamer kamer = gast.kamer;

        // Zoek een vrije schoonmaker die voor noodtaken bedoeld is.
        Schoonmaker schoonmaker = personenService.vindVrijeSchoonmakerVoorNoodsituatie();

        // Als er geen vrije schoonmaker is (null), stop dan.
        if (schoonmaker == null) return;

        if (logger != null) {
            logger.log("[" + event.getTime() + "] Schoonmaker: noodsituatie!");
        }

        // Wijs de kamer toe aan de schoonmaker via maakKamerSchoon().
        schoonmaker.maakKamerSchoon(kamer);

        // Bereken de route van de schoonmaker naar de kamer via de pathfinder.
        hotel.pathfinder.zetRoute(schoonmaker, kamer);

        if (logger != null) {
            logger.log("[" + event.getTime() + "] Schoonmaker gaat naar kamer " + kamer.getKamernummer());
        }
    }
}
