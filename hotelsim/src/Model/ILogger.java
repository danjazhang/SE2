package Model;
// Verantwoordelijkheid: berichten loggen naar de GUI
// Ruimtes gebruiken deze interface zodat ze niet direct aan de View gekoppeld zijn
// EventLogView implementeert dit en toont de berichten in het tekstvak
public interface ILogger {
    void log(String bericht);
}
