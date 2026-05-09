package View;

import Controller.SimulatieController;
import javax.swing.*;
import java.awt.event.ActionEvent;

// Verantwoordelijkheid: simulatie knoppen tonen
public class SimulatieView extends JPanel {

    private SimulatieController simulatieController;
    private JButton pauseButton = new JButton("Pauze");
    private JButton stopButton = new JButton("Stop");
    private String gekozenSnelheid = "Normaal";

    public SimulatieView(SimulatieController simulatieController) {
        this.simulatieController = simulatieController;

        // Normaal is de standaardwaarde wanneer het scherm opent.
        pasSnelheidToe();

        // pauze knop
        pauseButton.addActionListener((ActionEvent e) -> {
            simulatieController.pauzeer();
            if (pauseButton.getText().equals("Pauze")) {
                pauseButton.setText("Resume");
            } else {
                pauseButton.setText("Pauze");
            }
        });

        // stop knop
        stopButton.addActionListener((ActionEvent e) -> simulatieController.stop());

        add(pauseButton);
        add(stopButton);
    }

    public void pasSnelheidToe() {
        // Deze view vertaalt alleen de woorden uit de GUI
        // naar een getal dat de controller kan gebruiken.
        if ("Langzaam".equals(gekozenSnelheid)) {
            // Langzaam gebruikt een lagere bewegingsfrequentie.
            simulatieController.setSnelheid(0);
        } else if ("Snel".equals(gekozenSnelheid)) {
            // Snel laat personen per NONE-tick meerdere stappen na elkaar zetten.
            simulatieController.setSnelheid(4);
        } else {
            // Normaal laat personen per NONE-tick een gewone stap zetten.
            simulatieController.setSnelheid(1);
        }
    }

    // Laat andere schermdelen, zoals het instellingenpaneel, de gekozen snelheid aanpassen.
    public void stelSnelheidIn(String snelheid) {
        gekozenSnelheid = snelheid;
        pasSnelheidToe();
    }

    public String getGekozenSnelheid() {
        // Het instellingenpaneel gebruikt deze getter om de
        // huidige keuze opnieuw in het keuzemenu te tonen.
        return gekozenSnelheid;
    }
}
