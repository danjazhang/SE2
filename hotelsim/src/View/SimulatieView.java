package View;

import Controller.SimulatieController;

import javax.swing.*;
import java.awt.event.ActionEvent;

// Verantwoordelijkheid: simulatie knoppen tonen
public class SimulatieView extends JPanel {

    private SimulatieController simulatieController;

    private JButton pauseButton = new JButton("Pauze");
    private JButton stopButton = new JButton("Stop");

    private JComboBox<String> snelheidSelector =
            new JComboBox<>(new String[]{"Langzaam", "Normaal", "Snel"});

    public SimulatieView(SimulatieController simulatieController) {

        this.simulatieController = simulatieController;

        // standaard op normaal
        snelheidSelector.setSelectedIndex(1);

        // snelheid direct toepassen
        pasSnelheidToe();

        snelheidSelector.addActionListener(
                (ActionEvent e) -> pasSnelheidToe()
        );

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
        stopButton.addActionListener(
                (ActionEvent e) -> simulatieController.stop()
        );

        add(new JLabel("Snelheid:"));
        add(snelheidSelector);
        add(pauseButton);
        add(stopButton);
    }

    // view geeft alleen de keuze door
    public void pasSnelheidToe() {

        String keuze =
                (String) snelheidSelector.getSelectedItem();

        simulatieController.pasSnelheidToe(keuze);
    }
}