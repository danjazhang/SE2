package Model.ruimte;

import Model.events.IEventListener;
import Model.ILogger;
import Model.persoon.Gast;
import Model.events.FilmEindEvent;
import Model.GastRoutingService;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Verantwoordelijkheid: gasten registreren, een film starten en gasten na de film terugsturen.
// Bij GOTO_CINEMA registreert de bioscoop de gast.
// Bij START_CINEMA slaat de bioscoop de eindtijd op.
// Bij NONE checkt de bioscoop elke tick of de film klaar is.
// Bioscoop erft van Ruimte en implementeert IEventListener.
public class Bioscoop extends Ruimte implements IEventListener {

    // Sla op of er momenteel een film bezig is: true = bezig, false = geen film.
    public boolean filmBezig;

    // De duur van de huidige film in ticks.
    public int filmDuur;

    // Lijst van gasten die momenteel in de bioscoop zijn (legacy, aanvulling op aanwezigeGastIds).
    public List<Gast> gasten;

    // Het tijdstip waarop de huidige film eindigt.
    private int filmEindTijd;

    // Een set van gastIds van gasten die momenteel in de bioscoop zitten.
    // 'Set<Integer>' betekent: een verzameling van unieke int-waarden, geen duplicaten.
    private Set<Integer> aanwezigeGastIds = new HashSet<>();

    // 'private static final' betekent: dit getal is voor alle Bioscoopobjecten hetzelfde en verandert nooit.
    // Een film duurt 40 ticks na START_CINEMA.
    private static final int FILMDUUR = 40;

    // Logger voor het sturen van berichten naar de GUI.
    private ILogger logger;

    // Service voor het terugsturen van gasten naar hun kamer nadat de film afgelopen is.
    private GastRoutingService gastTerugService;

    // Constructor met logger: zet filmBezig op false en maak een lege gastenlijst aan.
    public Bioscoop(ILogger logger) {
        this.gasten = new ArrayList<>();
        this.filmBezig = false;
        this.filmDuur = 0;
        this.filmEindTijd = 0;
        this.logger = logger;
    }

    // Lege constructor voor als er geen logger nodig is, bijvoorbeeld in tests.
    public Bioscoop() {
        this.gasten = new ArrayList<>();
        this.filmBezig = false;
        this.filmDuur = 0;
        this.filmEindTijd = 0;
    }

    // Sla de gastTerugService op zodat we gasten na de film kunnen terugsturen.
    public void setGastTerugService(GastRoutingService gastTerugService) {
        this.gastTerugService = gastTerugService;
    }

    // '@Override' betekent: deze methode vervangt onEvent() van de interface IEventListener.
    // Wordt aangeroepen door EventController bij elk binnenkomend event.
    @Override
    public void onEvent(HotelEvent event) {

        // GOTO_CINEMA: een gast gaat naar de bioscoop. Voeg zijn gastId toe aan de set en log het.
        // 'aanwezigeGastIds.add(event.getGuestId())' betekent: voeg de gastId toe aan de set.
        if (event.getEventType() == HotelEventType.GOTO_CINEMA) {
            aanwezigeGastIds.add(event.getGuestId());
            if (logger != null) logger.log("[" + event.getTime() + "] Bioscoop: gast " + event.getGuestId() + " komt binnen");
        }

        // START_CINEMA: een film start. Zet filmBezig op true en sla de eindtijd op.
        // 'filmEindTijd = event.getTime() + FILMDUUR' betekent: eindtijd is gelijk aan huidigeTijd plus 40.
        else if (event.getEventType() == HotelEventType.START_CINEMA) {
            filmBezig = true;
            filmEindTijd = event.getTime() + FILMDUUR;
            if (logger != null) logger.log("[" + event.getTime() + "] Bioscoop: film start");
        }

        // NONE: elke tick controleren we of de film klaar is.
        else if (event.getEventType() == HotelEventType.NONE) {
            int tijd = event.getTime();
            // Als filmBezig gelijk is aan true én de huidige tijd groter is dan of gelijk aan (>=) de eindtijd:
            if (filmBezig && tijd >= filmEindTijd) {
                // Zet filmBezig op false want de film is klaar.
                filmBezig = false;
                FilmEindEvent eindEvent = new FilmEindEvent(tijd, -1);
                if (logger != null) logger.log("[" + eindEvent.getTijd() + "] Bioscoop: film eindigt");

                // Stuur alle gasten in de set terug naar hun kamer.
                if (gastTerugService != null) {
                    for (int gastId : aanwezigeGastIds) {
                        gastTerugService.stuurTerugNaarKamer(gastId);
                    }
                }
                // Maak de set leeg zodat die klaar is voor de volgende film.
                aanwezigeGastIds.clear();
            }
        }
    }

    // Lege methoden als placeholders.
    public void startFilm() {}
    public void stopFilm() {}
    public void betreedBioscoop() {}
}
