package Model;

import hotelevents.HotelEvent;

// Interface voor het observer pattern voor library events
// Elke klasse die wil reageren op library events implementeert dit
// De EventController stuurt library events door naar alle geregistreerde listeners
public interface IEventListener {

    // wordt aangeroepen door EventController als er een library event binnenkomt
    // elke klasse beslist zelf of hij iets doet met het event
    void onEvent(HotelEvent event);
}
