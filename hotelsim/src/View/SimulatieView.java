package View;

import Controller.SimulatieController;
import javax.swing.*;
import java.awt.event.ActionEvent;

// Verantwoordelijkheid: simulatie knoppen tonen
public class SimulatieView extends JPanel {

    private SimulatieController simulatieController;
    private JButton pauseButton = new JButton("Pauze");
    private JButton stopButton = new JButton("Stop");
    private JComboBox<String> snelheidSelector = new JComboBox<>(new String[]{"Langzaam", "Normaal", "Snel"});

    public SimulatieView(SimulatieController simulatieController) {
        this.simulatieController = simulatieController;

        // Normaal is de standaardkeuze wanneer het scherm opent.
        snelheidSelector.setSelectedIndex(1);
        // Zet meteen de beginwaarde door naar de controller.
        pasSnelheidToe();
        // Als de gebruiker van snelheid wisselt, sturen we die keuze direct door.
        snelheidSelector.addActionListener((ActionEvent e) -> pasSnelheidToe());

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

        add(new JLabel("Snelheid:"));
        add(snelheidSelector);
        add(pauseButton);
        add(stopButton);
    }

    public void pasSnelheidToe() {
        int index = snelheidSelector.getSelectedIndex();
        if (index == 0) {
            // Langzaam gebruikt een lagere bewegingsfrequentie.
            simulatieController.setSnelheid(0);
        } else if (index == 1) {
            // Normaal laat personen per NONE-tick een gewone stap zetten.
            simulatieController.setSnelheid(1);
        } else {
            // Snel laat personen per NONE-tick meerdere stappen na elkaar zetten.
            simulatieController.setSnelheid(4);
        }
    }
}
