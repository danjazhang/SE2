package View;

import javax.swing.*;
import Model.ILogger;

public class EventLogView implements ILogger {

    //maak nieuwe tekstvak
    private JTextArea logArea = new JTextArea();

    public EventLogView() {
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setRows(100);
        logArea.setColumns(30);
    }

    //geef tekstvak terug
    public JTextArea getLogArea() {
        return logArea;
    }

    //voeg een regel tekst aan het tekstvak
    public void log(String bericht){
        //append plakt tekst achteraan
        logArea.append(bericht + "\n");
        //om tekst te printen doe je EventLog.log("Tekst") ipv System.Out.println("Tekst")
    }
}