package Model;

// Verantwoordelijkheid: een interface voor het loggen van berichten naar de GUI.
// Ruimtes en services gebruiken ILogger zodat ze niet direct aan de View gekoppeld zijn.
// EventLogView implementeert ILogger en toont de berichten in het tekstvak in de GUI.
public interface ILogger {
    // method log krijgt een string bericht mee 
    void log(String bericht);
}
