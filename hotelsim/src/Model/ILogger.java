package Model;

// Verantwoordelijkheid: een interface voor het loggen van berichten naar de GUI.
// Een interface is een contract: elke klasse die ILogger implementeert moet de methode log() hebben.
// Ruimtes en services gebruiken ILogger zodat ze niet direct aan de View gekoppeld zijn.
// EventLogView implementeert ILogger en toont de berichten in het tekstvak in de GUI.
public interface ILogger {
    // Stuur het opgegeven bericht naar de GUI zodat het zichtbaar wordt in het logvenster.
    void log(String bericht);
}
