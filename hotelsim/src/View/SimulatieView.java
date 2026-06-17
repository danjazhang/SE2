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

    // knop om het instellingenvenster te openen
    private JButton instellingenButton = new JButton("Instellingen");

    // huidige schoonmaaktijd — standaard 20 ticks
    private int schoonmaakDuur = 20;

    // huidige filmduur — standaard 40 ticks
    private int filmDuur = 40;

    // huidige traptijd — standaard 3 ticks per verdieping
    private int trapTijd = 3;

    // huidige maximale wachttijd — standaard 5 ticks
    private int maxWachtTicks = 5;

    // dropdown voor het kiezen van een scenario
    private JComboBox<String> scenarioSelector =
            new JComboBox<>(new String[]{"Scenario 1", "Scenario 2", "Scenario 3", "Scenario 4"});

    // callback die HotelView uitvoert als reset gedrukt wordt
    private Runnable onReset;

    // callback die HotelView uitvoert als pauze gedrukt wordt, geeft true mee als gepauzeerd
    private Consumer<Boolean> onPauze;

    // callback die HotelView uitvoert als instellingen opgeslagen worden
    private Runnable onInstellingenOpgeslagen;

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

        // instellingen knop opent het instellingenvenster
        instellingenButton.addActionListener((ActionEvent e) -> {
            // pauzeer simulatie als die nog niet gepauzeerd was
            boolean wasAlGepauzeerd = gepauzeerd;
            if (!gepauzeerd) {
                simulatieController.pauzeer();
                gepauzeerd = true;
                pauseButton.setText("Resume");
                if (onPauze != null) onPauze.accept(true);
            }

            InstellingenView instellingen = new InstellingenView(
                    (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this),
                    schoonmaakDuur,
                    filmDuur,
                    trapTijd,
                    maxWachtTicks);
            instellingen.setVisible(true);
            schoonmaakDuur = instellingen.getSchoonmaakDuur();
            filmDuur = instellingen.getFilmDuur();
            trapTijd = instellingen.getTrapTijd();
            maxWachtTicks = instellingen.getMaxWachtTicks();
            // pas instellingen direct toe als de simulatie al loopt
            if (onInstellingenOpgeslagen != null) {
                onInstellingenOpgeslagen.run();
            }

            // hervat simulatie als die hiervoor nog niet gepauzeerd was
            if (!wasAlGepauzeerd) {
                simulatieController.pauzeer();
                gepauzeerd = false;
                pauseButton.setText("Pauze");
                if (onPauze != null) onPauze.accept(false);
            }
        });

        add(new JLabel("Scenario:"));
        add(scenarioSelector);
        add(new JLabel("Snelheid:"));
        add(snelheidSelector);
        add(instellingenButton);
        add(pauseButton);
        add(stopButton);
        add(resetButton);
    }

    // stel de reset callback in vanuit HotelView
    public void setOnReset(Runnable onReset) {
        this.onReset = onReset;
    }

    // stel de instellingen callback in vanuit HotelView
    public void setOnInstellingenOpgeslagen(Runnable onInstellingenOpgeslagen) {
        this.onInstellingenOpgeslagen = onInstellingenOpgeslagen;
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

    // geef de ingestelde schoonmaaktijd terug
    public int getSchoonmaakDuur() {
        return schoonmaakDuur;
    }

    // geef de ingestelde filmduur terug
    public int getFilmDuur() {
        return filmDuur;
    }

    // geef de ingestelde traptijd terug
    public int getTrapTijd() {
        return trapTijd;
    }

    //geef de ingestelde max wachttijd terug
    public int getMaxWachtTicks() {return maxWachtTicks; }

    // view geeft alleen de keuze door
    public void pasSnelheidToe() {
        String keuze = (String) snelheidSelector.getSelectedItem();
        simulatieController.pasSnelheidToe(keuze);
    }
}
