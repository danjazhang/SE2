package View;

import Controller.SimulatieController;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

// Verantwoordelijkheid: simulatie knoppen tonen
public class SimulatieView extends JPanel {

    private SimulatieController simulatieController;
    private JButton pauseButton = new JButton("Pauze");
    private JButton stopButton = new JButton("Stop");
    private JButton resetButton = new JButton("Reset");
    private JComboBox<String> snelheidSelector =
            new JComboBox<>(new String[]{"Langzaam", "Normaal", "Snel"});

    // dropdown voor het kiezen van een scenario
    private JComboBox<String> scenarioSelector =
            new JComboBox<>(new String[]{"Scenario 1", "Scenario 2", "Scenario 3", "Scenario 4"});

    // callback die HotelView uitvoert als reset gedrukt wordt
    private Runnable onReset;

    // callback die HotelView uitvoert als pauze gedrukt wordt, geeft true mee als gepauzeerd
    private Consumer<Boolean> onPauze;

    // bijhouden of de simulatie gepauzeerd is
    private boolean gepauzeerd = false;

    public SimulatieView(SimulatieController simulatieController) {
        this.simulatieController = simulatieController;

        snelheidSelector.setSelectedIndex(1);
        pasSnelheidToe();
        snelheidSelector.addActionListener((ActionEvent e) -> pasSnelheidToe());

        pauseButton.addActionListener((ActionEvent e) -> {
            simulatieController.pauzeer();
            gepauzeerd = !gepauzeerd;
            if (gepauzeerd) {
                pauseButton.setText("Resume");
            } else {
                pauseButton.setText("Pauze");
            }
            // stuur pauze status door naar HotelView zodat de timer mee pauzeert
            if (onPauze != null) onPauze.accept(gepauzeerd);
        });

        stopButton.addActionListener((ActionEvent e) -> simulatieController.stop());

        // reset knop: stop simulatie en roep de reset callback aan
        resetButton.addActionListener((ActionEvent e) -> {
            try { simulatieController.stop(); } catch (Exception ignored) {}
            gepauzeerd = false;
            pauseButton.setText("Pauze");
            if (onReset != null) onReset.run();
        });

        add(new JLabel("Scenario:"));
        add(scenarioSelector);
        add(new JLabel("Snelheid:"));
        add(snelheidSelector);
        add(pauseButton);
        add(stopButton);
        add(resetButton);
    }

    // stel de reset callback in vanuit HotelView
    public void setOnReset(Runnable onReset) {
        this.onReset = onReset;
    }

    //callback is stukje code dat je meegeeft aan andere klasse,
    //zodat die klasse het later kan uitvoeren op het juiste moment

    // stel de pauze callback in vanuit HotelView
    public void setOnPauze(Consumer<Boolean> onPauze) {
        this.onPauze = onPauze;
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
