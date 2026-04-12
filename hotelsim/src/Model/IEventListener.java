package Model;

// Interface voor het observer pattern voor interne events
// Werkt hetzelfde als ModelListener maar dan voor events
// Elke klasse die wil reageren op interne events implementeert dit
public interface IEventListener {

    // wordt aangeroepen door EventController als er een intern event binnenkomt
    // elke klasse beslist zelf of hij iets doet met het event
    void onEvent(InternEvent event);
}

