package View;

import javax.swing.*;

public class EventLog {

    //maak nieuwe tekstvak
    private static JTextArea logArea = new JTextArea();

    //geef tekstvak terug
    public static JTextArea getLogArea() {
        return logArea;
    }

    //voeg een regel tekst aan het tekstvak
    public static void log(String bericht){
        //append plakt tekst achteraan
        logArea.append(bericht + "\n");
        //om tekst te printen doe je EventLog.log("Tekst") ipv System.Out.println("Tekst")
    }
}
