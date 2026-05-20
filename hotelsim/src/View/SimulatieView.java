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

    // dropdown voor het kiezen van een scenario
    private JComboBox<String> scenarioSelector =
            new JComboBox<>(new String[]{"Scenario 1", "Scenario 2", "Scenario 3", "Scenario 4"});

    public SimulatieView(SimulatieController simulatieController) {
        this.simulatieController = simulatieController;

        // standaard op normaal
        snelheidSelector.setSelectedIndex(1);

        // snelheid direct toepassen
        pasSnelheidToe();

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

        add(new JLabel("Scenario:"));
        add(scenarioSelector);
        add(new JLabel("Snelheid:"));
        add(snelheidSelector);
        add(pauseButton);
        add(stopButton);
    }

    // geef het gekozen scenario terug als getal (1 t/m 4)
    public int getGekozenScenario() {
        return scenarioSelector.getSelectedIndex() + 1;
    }

    // view geeft alleen de keuze door
    public void pasSnelheidToe() {
        String keuze = (String) snelheidSelector.getSelectedItem();
        simulatieController.pasSnelheidToe(keuze);
    }
}
