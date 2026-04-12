package Model;

// Stelt een schoonmaker voor in het hotel
// Erft van Persoon en implementeert IEventListener
// De schoonmaker is verantwoordelijk voor schoonmaak logica (single responsibility)
public class Schoonmaker extends Persoon implements IEventListener {

    // of de schoonmaker momenteel bezig is
    public boolean bezig;

    // de kamer die de schoonmaker momenteel schoonmaakt
    public Kamer kamer;

    // logger voor het loggen naar de GUI
    private ILogger logger;

    // constructor met logger
    public Schoonmaker(ILogger logger) {
        this.bezig = false;
        this.kamer = null;
        this.logger = logger;
    }

    // lege constructor voor als er geen logger nodig is (bijv. in testen)
    public Schoonmaker() {
        this.bezig = false;
        this.kamer = null;
    }

    // wordt aangeroepen door EventController als er een intern event binnenkomt
    // schoonmaker reageert alleen op schoonmaak events
    @Override
    public void onEvent(InternEvent event) {
        // als het een schoonmaak event is, log dat en zet bezig op true
        if (event instanceof SchoonmaakEvent) {
            if (logger != null) logger.log("[" + event.getTijd() + "] Schoonmaker: noodsituatie!");
            this.bezig = true;
        }
    }

    // maak een kamer schoon
    public void maakKamerSchoon(Kamer k) {
        this.kamer = k;
        this.bezig = true;
        k.schoonmaken();
        this.bezig = false;
        this.kamer = null;
    }

    // handel een noodsituatie af
    public void handelEmergency(Kamer k) {
        maakKamerSchoon(k);
    }

    // ga naar de optimale positie in het hotel
    public void gaNaarOptimalePositie() {}
}

