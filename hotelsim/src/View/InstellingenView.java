package View;

import javax.swing.*;
import java.awt.*;

// Verantwoordelijkheid: instellingen venster voor configureerbare simulatiewaarden
public class InstellingenView extends JDialog {

    // invoerveld voor de schoonmaaktijd
    private JSpinner schoonmaakDuurSpinner;

    // invoerveld voor de filmduur
    private JSpinner filmDuurSpinner;

    // invoerveld voor de traptijd
    private JSpinner trapTijdSpinner;

    // invoerveld voor de maximale wachttijd
    private JSpinner maxWachtTicksSpinner;

    public InstellingenView(JFrame parent, int huidigSchoonmaakDuur, int huidigFilmDuur, int huidigTrapTijd, int huidigMaxWachtTicks) {
        super(parent, "Instellingen", true);

        setSize(350, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel instellingenPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        instellingenPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        schoonmaakDuurSpinner = new JSpinner(new SpinnerNumberModel(huidigSchoonmaakDuur, 1, 100, 1));
        instellingenPanel.add(new JLabel("Schoonmaaktijd (ticks):"));
        instellingenPanel.add(schoonmaakDuurSpinner);

        filmDuurSpinner = new JSpinner(new SpinnerNumberModel(huidigFilmDuur, 1, 200, 1));
        instellingenPanel.add(new JLabel("Filmduur (ticks):"));
        instellingenPanel.add(filmDuurSpinner);

        trapTijdSpinner = new JSpinner(new SpinnerNumberModel(huidigTrapTijd, 1, 20, 1));
        instellingenPanel.add(new JLabel("Traptijd per verdieping:"));
        instellingenPanel.add(trapTijdSpinner);

        maxWachtTicksSpinner = new JSpinner(new SpinnerNumberModel(huidigMaxWachtTicks, 1, 200, 1));
        instellingenPanel.add(new JLabel("Max wachttijd gast (ticks):"));
        instellingenPanel.add(maxWachtTicksSpinner);

        // sluitknop
        JButton sluitButton = new JButton("Opslaan & Sluiten");
        sluitButton.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.add(sluitButton);

        add(instellingenPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    // geef de ingestelde schoonmaaktijd terug
    public int getSchoonmaakDuur() {
        return (int) schoonmaakDuurSpinner.getValue();
    }

    // geef de ingestelde filmduur terug
    public int getFilmDuur() {
        return (int) filmDuurSpinner.getValue();
    }

    // geef de ingestelde traptijd terug
    public int getTrapTijd() {
        return (int) trapTijdSpinner.getValue();
    }

    // geef de ingestelde maximale wachttijd terug
    public int getMaxWachtTicks() {
        return (int) maxWachtTicksSpinner.getValue();
    }
}
