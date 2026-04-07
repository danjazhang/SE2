package View;

import Controller.SimulatieController;
import javax.swing.*;
import java.awt.event.ActionEvent;

// Verantwoordelijkheid: simulatie knoppen tonen
public class SimulatieView extends JPanel {

    private SimulatieController simulatieController;
    private JButton pauseButton = new JButton("Pauze");
    private JButton stopButton = new JButton("Stop");

    public SimulatieView(SimulatieController simulatieController) {
        this.simulatieController = simulatieController;
  

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
}
