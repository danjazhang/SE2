package Model;

public class Lobby extends Ruimte implements IEventListener {

    //positie van
    private int balieX;
    private int balieY;
    private Hotel hotel;
    private ILogger logger;

    public Lobby(int posX, int posY, int breedte, int hoogte, int balieX, int balieY, Hotel hotel, ILogger logger) {
        super(posX, posY, breedte, hoogte);
        this.balieX = balieX;
        this.balieY = balieY;
        this.hotel = hotel;
        this.logger = logger;
    }

    @Override
    //onEvent wordt aangeroepen bij elk event
    public void onEvent(InternEvent event) {
        //check of het een checkin of checkout event is en roep de juiste methode aan
        if (event instanceof CheckInEvent) {
            behandelCheckIn((CheckInEvent) event);
        } else if (event instanceof CheckOutEvent) {
            behandelCheckOut((CheckOutEvent) event);
        }
    }

    private void behandelCheckIn(CheckInEvent event) {
        //haal gastid op van event
        int gastId = event.getGastId();
        //maak nieuwe gast met dat id en 1 ster voorkeur
        //1 ster is tijdelijke voorkeur
        //er moet een getter komen voor de sterren uit de library
        Gast gast = new Gast(gastId, 1);
        //geef gast layout
        gast.layout = hotel.layout;
        //zet gast op de balie als startpunt
        Vakje startVakje = hotel.layout.krijgVakje(1, hotel.hoogte);
        if (startVakje != null) gast.zetStartPositie(startVakje);
        //voeg persoon toe aan personenlijst in hotel
        hotel.voegPersoonToe(gast);
        //zoek een vrije schone kamer
        Kamer kamer = vindVrijeKamer();
        if (kamer != null) {
            //koppel de gast aan kamer
            gast.checkIn(kamer);
            //stel kamer als doel
            gast.zetDoel(hotel.layout.krijgVakje(kamer.posX, kamer.posY));
        }
        //toon het event in de eventlog visueel
        if (logger != null) logger.log("[" + event.getTijd() + "] Lobby: gast " + gastId + " checkt in");
    }

    private void behandelCheckOut(CheckOutEvent event) {
        //loop door alle personen en zoek de gast met het juiste id
        int gastId = event.getGastId();
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast && ((Gast) p).gastId == gastId) {
                Gast gast = (Gast) p;
                //sla kamer op want na uitchecken is kamer null
                Kamer kamer = gast.kamer;
                gast.checkOut();
                //zoek vrije schoonmaker
                Schoonmaker schoonmaker = vindVrijeSchoonmaker();
                //check of er een schoonmaker is en of de gast een kamer had
                if (schoonmaker != null && kamer != null) {
                    schoonmaker.maakKamerSchoon(kamer);
                }
                break;
            }
        }
        //toon checkouut bericht
        if (logger != null) logger.log("[" + event.getTijd() + "] Lobby: gast " + gastId + " checkt uit");
    }

    private Kamer vindVrijeKamer() {
        //loop door alle ruimtes
        for (Ruimte r : hotel.ruimtes) {
            //check of het een kamer is
            if (r instanceof Kamer) {
                Kamer k = (Kamer) r;
                //check of het niet bezet is en of die schoon is
                if (!k.isBezet() && k.isSchoon()) return k;
            }
        }//geef null terug als er geen vrije kamer is
        return null;
    }

    private Schoonmaker vindVrijeSchoonmaker() {
        //loop door alle personen
        for (Persoon p : hotel.personen) {
            //zoek een schoonmaker die niet bezig is
            if (p instanceof Schoonmaker && !((Schoonmaker) p).bezig) return (Schoonmaker) p;
        }
        //geef null teruug als alle schoonmakers bezig zijn
        return null;
    }

    public void setLogger(ILogger logger) { this.logger = logger; }
    public void toonStatusScherm() { System.out.println("Status van hotel wordt getoond..."); }
    public int getBalieX() { return balieX; }
    public int getBalieY() { return balieY; }
}
