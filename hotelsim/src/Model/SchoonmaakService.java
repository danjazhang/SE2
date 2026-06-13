package Model;

import Model.events.IEventListener;
import Model.layout.Vakje;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.Kamer;
import hotelevents.HotelEvent;
import hotelevents.HotelEventType;

// Verantwoordelijkheid: alle taaklogica rond schoonmaken centraal afhandelen.
// Deze service:
// 1. vertaalt events naar schoonmaaktaken,
// 2. beheert wachtende kamers die nog vuil zijn,
// 3. kiest welke vrije schoonmaker de volgende taak krijgt,
// 4. stuurt vrije schoonmakers alleen terug als er geen wachtende taken meer zijn.
// De schoonmaker zelf blijft een uitvoerder en hoeft deze beslislogica dus niet te kennen.
public class SchoonmaakService implements IEventListener {

    // Hotelcontext met kamers, personen, wachtrijen en pathfinder.
    private final Hotel hotel;

    // Hulpservice voor simpele persoon-opzoekacties, bijvoorbeeld gasten zoeken op id.
    private final PersonenService personenService;

    // Logger voor meldingen naar de GUI.
    private ILogger logger;

    // Constructor: bewaar context en maak meteen een PersonenService aan.
    public SchoonmaakService(Hotel hotel, ILogger logger) {
        // Hotelreferentie bewaren voor alle verdere schoonmaaklogica.
        this.hotel = hotel;
        // Logger bewaren zodat we statusmeldingen kunnen tonen.
        this.logger = logger;
        // PersonenService maken voor eenvoudige zoekacties zoals vindGast(...).
        this.personenService = new PersonenService(hotel);
    }

    // Laat de controller later een logger instellen of vervangen.
    public void setLogger(ILogger logger) {
        // Vervang de huidige loggerreferentie.
        this.logger = logger;
    }

    // Verwerk alle kamers die nog op schoonmaak wachten.
    public void verwerkWachtendeTaken(int tijd) {
        // Zonder hotel of pathfinder kunnen we geen route of taak plannen.
        if (hotel == null || hotel.pathfinder == null) return;

        // Ruim de wachtlijst op voordat we nieuwe taken toewijzen.
        // We verwijderen:
        // - null-verwijzingen,
        // - kamers die inmiddels al schoon zijn,
        // - kamers die al actief aan een schoonmaker gekoppeld zijn.
        hotel.wachtendeSchoonmaakKamers.removeIf(kamer ->
                kamer == null || kamer.isSchoon() || isAlToegewezen(kamer));

        // Blijf taken uitdelen zolang er tegelijk een vrije schoonmaker en een wachtende kamer bestaat.
        while (true) {
            // Neem de eerstvolgende kamer die nog echt wacht op schoonmaak.
            Kamer kamer = vindVolgendeWachtendeKamer();
            // Kies vervolgens de vrije schoonmaker die het dichtst bij deze kamer staat.
            Schoonmaker schoonmaker = vindDichtstbijzijndeVrijeSchoonmaker(kamer);

            // Als een van beide ontbreekt, kunnen we nu niets meer plannen.
            if (schoonmaker == null || kamer == null) break;

            // Geef de schoonmaker de eventtijd mee voor consistente logberichten.
            schoonmaker.setHuidigeTijd(tijd);
            // Koppel de kamer inhoudelijk als nieuwe taak aan de schoonmaker.
            schoonmaker.maakKamerSchoon(kamer);
            // Bereken en zet meteen de route naar de gekozen kamer.
            hotel.pathfinder.zetRoute(schoonmaker, kamer);
            // Verwijder de kamer uit de wachtlijst omdat ze nu ingepland is.
            hotel.verwijderWachtendeSchoonmaak(kamer);

            // Log naar de GUI dat de schoonmaker vertrekt.
            if (logger != null) {
                logger.log("[" + tijd + "] Schoonmaker gaat naar kamer " + kamer.getKamernummer());
            }
        }

        // Als er geen wachttaken meer open staan, mogen vrije schoonmakers terug naar hun wachtplek.
        stuurVrijeSchoonmakersTerugNaarWachtplek();
    }

    // Reageer op externe hotel-events van de library.
    @Override
    public void onEvent(HotelEvent event) {
        // Alleen een schoonmaaknoodgeval is relevant voor deze service.
        if (event.getEventType() != HotelEventType.CLEANING_EMERGENCY) return;
        // Zonder hotel of pathfinder kunnen we de opdracht niet uitvoeren.
        if (hotel == null || hotel.pathfinder == null) return;

        // Zoek de gast die in het event genoemd wordt.
        Gast gast = personenService.vindGast(event.getGuestId());
        // Als er geen gast is of de gast geen kamer heeft, is er niets om te reinigen.
        if (gast == null || gast.kamer == null) return;

        // Gebruik de kamer van deze gast als doelkamer.
        Kamer kamer = gast.kamer;
        // Kies ook bij noodgevallen gewoon de dichtstbijzijnde vrije schoonmaker.
        Schoonmaker schoonmaker = vindDichtstbijzijndeVrijeSchoonmaker(kamer);
        // Geen vrije schoonmaker beschikbaar: dan stopt deze opdracht hier.
        if (schoonmaker == null) return;

        // Log eerst dat het om een noodsituatie gaat.
        if (logger != null) {
            logger.log("[" + event.getTime() + "] Schoonmaker: noodsituatie!");
        }

        // Koppel de kamer aan de gekozen schoonmaker.
        schoonmaker.maakKamerSchoon(kamer);
        // Laat daarna Pathfinder de route naar de kamer zetten.
        hotel.pathfinder.zetRoute(schoonmaker, kamer);

        // Log ook de concrete kamerbestemming.
        if (logger != null) {
            logger.log("[" + event.getTime() + "] Schoonmaker gaat naar kamer " + kamer.getKamernummer());
        }
    }

    // Zoek de eerste kamer in de wachtlijst die nog echt schoongemaakt moet worden.
    private Kamer vindVolgendeWachtendeKamer() {
        // Doorloop de kamers in de huidige wachtrijvolgorde.
        for (Kamer kamer : hotel.wachtendeSchoonmaakKamers) {
            // Alleen een bestaande, vuile en nog niet toegewezen kamer is geldig.
            if (kamer != null && !kamer.isSchoon() && !isAlToegewezen(kamer)) return kamer;
        }
        // Geen bruikbare kamer gevonden.
        return null;
    }

    // Controleer of een kamer al als actieve taak aan een schoonmaker hangt.
    private boolean isAlToegewezen(Kamer kamer) {
        // Kijk naar alle personen in het hotel.
        for (Persoon persoon : hotel.personen) {
            // Alleen schoonmakers kunnen een schoonmaaktaak bezitten.
            if (!(persoon instanceof Schoonmaker schoonmaker)) continue;
            // Als deze schoonmaker exact deze kamer als huidige taak heeft, is ze al toegewezen.
            if (schoonmaker.kamer == kamer) return true;
        }
        // Geen actieve toewijzing gevonden.
        return false;
    }

    // Stuur vrije schoonmakers pas terug naar hun wachtplek als er geen openstaande schoonmaak meer is.
    private void stuurVrijeSchoonmakersTerugNaarWachtplek() {
        // Zolang er nog kamers wachten, moeten vrije schoonmakers beschikbaar blijven voor nieuwe toewijzing.
        if (!hotel.wachtendeSchoonmaakKamers.isEmpty()) return;

        // Doorloop alle personen om vrije schoonmakers te vinden.
        for (Persoon persoon : hotel.personen) {
            // Alleen schoonmakers zijn hier relevant.
            if (!(persoon instanceof Schoonmaker schoonmaker)) continue;
            // Sla schoonmakers over die nog bezig zijn of al op hun wachtplek staan.
            if (schoonmaker.bezig || schoonmaker.staatOpWachtVakje()) continue;
            // Stuur deze vrije schoonmaker terug naar zijn vaste wachtpositie.
            schoonmaker.gaNaarWachtVakje();
        }
    }

    // Kies voor een concrete doelkamer de vrije schoonmaker die het dichtstbij staat.
    private Schoonmaker vindDichtstbijzijndeVrijeSchoonmaker(Kamer doelKamer) {
        // Zonder doelkamer kunnen we geen zinvolle afstand berekenen.
        if (doelKamer == null) return null;

        // Beste kandidaat tot nu toe.
        Schoonmaker dichtste = null;
        // Start met een extreem grote waarde zodat de eerste echte kandidaat altijd beter is.
        int kleinsteAfstand = Integer.MAX_VALUE;

        // Vergelijk alle vrije schoonmakers met elkaar.
        for (Persoon persoon : hotel.personen) {
            // Alleen vrije schoonmakers doen mee.
            if (!(persoon instanceof Schoonmaker schoonmaker) || schoonmaker.bezig) continue;

            // Gebruik de huidige positie als startpunt.
            // Als die ontbreekt, val dan terug op de vaste wachtplek.
            Vakje startVakje = schoonmaker.huidigVakje != null ? schoonmaker.huidigVakje : schoonmaker.wachtVakje;
            // Zonder startpositie kunnen we deze schoonmaker niet meenemen.
            if (startVakje == null) continue;

            // Gebruik eenvoudige Manhattan-afstand: horizontale + verticale afstand.
            int afstand = Math.abs(startVakje.x - doelKamer.posX) + Math.abs(startVakje.y - doelKamer.posY);
            // Werk de beste kandidaat bij als deze schoonmaker dichterbij is.
            if (afstand < kleinsteAfstand) {
                kleinsteAfstand = afstand;
                dichtste = schoonmaker;
            }
        }

        // Geef de dichtstbijzijnde vrije schoonmaker terug, of null als niemand vrij was.
        return dichtste;
    }
}
