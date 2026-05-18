package View.dialog;

import javax.swing.*;
import java.awt.*;

// Verantwoordelijkheid: alleen het instellingenvenster opbouwen en de gekozen waarden teruggeven.
// HotelView blijft hierdoor verantwoordelijk voor het hoofdvenster en niet voor dialog-opbouw.
public class InstellingenDialog {

    // Bouw het instellingenvenster op en geef daarna alleen de gekozen waarden terug.
    // De caller blijft verantwoordelijk voor het echt toepassen van die instellingen.
    public InstellingenResult toon(Component parent, String huidigeSnelheid, boolean eventlogZichtbaar, int huidigeTileSize) {
        JPanel instellingenPanel = new JPanel(new GridLayout(0, 2, 8, 8));

        // De dialoog begint altijd met de huidige toestand van de simulatie,
        // zodat de gebruiker ziet welke waarden nu actief zijn.
        JComboBox<String> snelheidKeuze = new JComboBox<>(new String[]{"Langzaam", "Normaal", "Snel"});
        snelheidKeuze.setSelectedItem(huidigeSnelheid);

        JCheckBox toonEventlog = new JCheckBox("Toon eventlog", eventlogZichtbaar);

        JComboBox<String> grootteKeuze = new JComboBox<>(new String[]{"Klein", "Normaal", "Groot"});
        grootteKeuze.setSelectedItem(bepaalGrootteLabel(huidigeTileSize));

        instellingenPanel.add(new JLabel("Snelheid:"));
        instellingenPanel.add(snelheidKeuze);
        instellingenPanel.add(new JLabel("Eventlog:"));
        instellingenPanel.add(toonEventlog);
        instellingenPanel.add(new JLabel("Grootte:"));
        instellingenPanel.add(grootteKeuze);

        int keuze = JOptionPane.showConfirmDialog(
                parent,
                instellingenPanel,
                "Instellingen",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (keuze != JOptionPane.OK_OPTION) return null;

        // Stop alle gekozen waarden in een klein resultaatobject,
        // zodat HotelView ze later rustig kan uitlezen en toepassen.
        return new InstellingenResult(
                (String) snelheidKeuze.getSelectedItem(),
                toonEventlog.isSelected(),
                bepaalTileSize((String) grootteKeuze.getSelectedItem())
        );
    }

    // Vertaal de huidige tileSize naar een label dat in de GUI getoond kan worden.
    private String bepaalGrootteLabel(int tileSize) {
        if (tileSize <= 48) return "Klein";
        if (tileSize >= 80) return "Groot";
        return "Normaal";
    }

    // Vertaal de tekstkeuze uit de GUI weer terug naar de echte tileSize voor LayoutView.
    private int bepaalTileSize(String grootte) {
        if ("Klein".equals(grootte)) return 48;
        if ("Groot".equals(grootte)) return 88;
        return 64;
    }

    public static class InstellingenResult {
        // Dit object bewaart de drie keuzes van de gebruiker op een nette manier,
        // zodat ze niet los als tijdelijke variabelen hoeven rond te zwerven.
        private final String snelheid;
        private final boolean eventlogZichtbaar;
        private final int tileSize;

        public InstellingenResult(String snelheid, boolean eventlogZichtbaar, int tileSize) {
            this.snelheid = snelheid;
            this.eventlogZichtbaar = eventlogZichtbaar;
            this.tileSize = tileSize;
        }

        public String getSnelheid() {
            return snelheid;
        }

        public boolean isEventlogZichtbaar() {
            return eventlogZichtbaar;
        }

        public int getTileSize() {
            return tileSize;
        }
    }
}
