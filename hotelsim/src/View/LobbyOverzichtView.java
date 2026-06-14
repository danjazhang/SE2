package View;

import Controller.SimulatieController;
import Model.Hotel;
import Model.persoon.Persoon;
import Model.ruimte.Ruimte;

import javax.swing.*;
import java.awt.*;

// Verantwoordelijkheid: observatievenster dat de huidige staat van het hotel toont
// Pauzeert de simulatie bij openen en hervat bij sluiten
public class LobbyOverzichtView extends JDialog {

    private Hotel hotel;
    private SimulatieController simulatieController;
    private JTextArea linksArea;
    private JTextArea rechtsArea;
    private Timer refreshTimer;
    private boolean gesloten = false;

    public LobbyOverzichtView(Hotel hotel, SimulatieController simulatieController) {
        super((JFrame) null, "Hotel Observatie Scherm", true);
        this.hotel = hotel;
        this.simulatieController = simulatieController;

        setSize(1100, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // linker tekstvak voor ruimtes
        linksArea = new JTextArea();
        linksArea.setEditable(false);
        linksArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // rechter tekstvak voor personen
        rechtsArea = new JTextArea();
        rechtsArea.setEditable(false);
        rechtsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // splits het venster in twee kolommen
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(linksArea), new JScrollPane(rechtsArea));
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // sluitknop onderaan
        JButton sluitButton = new JButton("Sluiten & Hervatten");
        sluitButton.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.add(sluitButton);
        add(bottom, BorderLayout.SOUTH);

        // elke seconde de weergave verversen
        refreshTimer = new Timer(1000, e -> updateView());
        refreshTimer.start();

        updateView();
    }

    private void updateView() {
        updateLinks();
        updateRechts();
    }

    // linker kolom: algemene status, lift, kamers en faciliteiten
    private void updateLinks() {
        String text = "";

        // simulatiestatus
        text += "=== HOTEL OBSERVATIE [GEPAUZEERD] ===\n\n";
        text += "Gasten aanwezig : " + aantalGasten() + "\n";
        String brandalarmStatus;
        if (hotel.brandalarmActief) {
            brandalarmStatus = "ACTIEF";
        } else {
            brandalarmStatus = "uit";
        }
        text += "Brandalarm      : " + brandalarmStatus + "\n\n";

        // lift
        if (hotel.lift != null) {
            text += "=== LIFT ===\n";
            text += "  Verdieping  : " + hotel.lift.getHuidigeVerdieping() + "\n";
            int aantalIn = hotel.lift.getPassagiers().size();
            if (aantalIn == 0) {
                text += "  In lift     : niemand\n";
            } else {
                text += "  In lift     : " + aantalIn + " persoon/personen\n";
            }
            boolean iemandWacht = false;
            for (int v = 1; v <= hotel.hoogte; v++) {
                int wacht = hotel.lift.aantalWachtend(v);
                if (wacht > 0) {
                    text += "  Wacht v" + v + "    : " + wacht + " persoon/personen\n";
                    iemandWacht = true;
                }
            }
            if (!iemandWacht) text += "  Wachtrij    : leeg\n";
            text += "\n";
        }

        // kamers
        text += "=== KAMERS ===\n";
        for (Ruimte r : hotel.ruimtes) {
            if (!r.isKamer()) continue;
            text += "  " + r.getStatusTekst() + "\n";
        }
        text += "\n";

        // faciliteiten
        text += "=== FACILITEITEN ===\n";
        for (Ruimte r : hotel.ruimtes) {
            if (!r.isFaciliteit()) continue;
            text += "  " + r.getStatusTekst() + "\n";
        }

        linksArea.setText(text);
        linksArea.setCaretPosition(0);
    }

    // rechter kolom: gasten en schoonmakers
    private void updateRechts() {
        String text = "";

        // gasten
        text += "=== GASTEN ===\n";
        for (Persoon p : hotel.personen) {
            if (!p.isGast()) continue;
            text += "  " + p.getStatusTekst() + "\n\n";
        }
        if (aantalGasten() == 0) text += "  Geen gasten aanwezig\n";
        text += "\n";

        // schoonmakers
        text += "=== SCHOONMAKERS ===\n";
        boolean schoonmakerGevonden = false;
        for (Persoon p : hotel.personen) {
            if (!p.isSchoonmaker()) continue;
            schoonmakerGevonden = true;
            text += "  " + p.getStatusTekst() + "\n\n";
        }
        if (!schoonmakerGevonden) text += "  Geen schoonmakers\n";

        rechtsArea.setText(text);
        rechtsArea.setCaretPosition(0);
    }

    // tel het aantal gasten in de personenlijst
    private int aantalGasten() {
        int count = 0;
        for (Persoon p : hotel.personen) {
            if (p.isGast()) count++;
        }
        return count;
    }

    // stop de timer en hervat de simulatie bij sluiten
    @Override
    public void dispose() {
        // zorgt ervoor dat dispose maar 1x wordt aangeroepen
        if (gesloten) return;
        gesloten = true;
        if (refreshTimer != null) refreshTimer.stop();
        if (simulatieController != null) simulatieController.pauzeer();
        super.dispose();
    }
}
