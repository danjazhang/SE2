package View;

import Controller.SimulatieController;
import Model.Hotel;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.*;

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

        // splits het venster in twee gelijke kolommen
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                linksArea,
                new JScrollPane(rechtsArea));
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
        text += "Brandalarm      : " + (hotel.brandalarmActief ? "ACTIEF" : "uit") + "\n\n";

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
            if (!(r instanceof Kamer k)) continue;
            String status;
            if (k.isBezet()) {
                String gastInfo = "";
                for (Gast g : k.getIngecheckteGasten()) {
                    if (!gastInfo.isEmpty()) gastInfo += ", ";
                    gastInfo += "gast " + g.gastId;
                }
                status = "BEZET (" + gastInfo + ")";
            } else if (!k.isSchoon()) {
                status = "WORDT SCHOONGEMAAKT";
            } else {
                status = "vrij";
            }
            text += "  Kamer " + k.getKamernummer() + " (" + k.getSterrenLabel() + ") : " + status + "\n";
        }
        text += "\n";

        // faciliteiten
        text += "=== FACILITEITEN ===\n";
        for (Ruimte r : hotel.ruimtes) {
            if (r instanceof Restaurant rest) {
                int aanwezig = rest.getAanwezigen().size();
                String vol = rest.capaciteit > 0 && aanwezig >= rest.capaciteit ? " [VOL]" : "";
                text += "  Restaurant (cap " + rest.capaciteit + ") : " + aanwezig + " aanwezig" + vol + "\n";
            } else if (r instanceof Fitnessruimte fit) {
                text += "  Fitness : " + fit.getAanwezigen().size() + " aanwezig\n";
            } else if (r instanceof Bioscoop bio) {
                String film = bio.filmBezig ? "film bezig" : "geen film";
                text += "  Bioscoop : " + bio.getAanwezigen().size() + " aanwezig, " + film + "\n";
            }
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
            if (!(p instanceof Gast g)) continue;
            String locatie = bepaalGastLocatie(g);
            String activiteit = bepaalGastActiviteit(g);
            text += "  Gast " + g.gastId + " (" + g.gewensteSterren + "★)\n";
            text += "    Locatie   : " + locatie + "\n";
            text += "    Activiteit: " + activiteit + "\n\n";
        }
        if (aantalGasten() == 0) text += "  Geen gasten aanwezig\n";
        text += "\n";

        // schoonmakers
        text += "=== SCHOONMAKERS ===\n";
        boolean schoonmakerGevonden = false;
        for (Persoon p : hotel.personen) {
            if (!(p instanceof Schoonmaker s)) continue;
            schoonmakerGevonden = true;
            String status;
            if (s.bezig && s.kamer != null) {
                status = "bezig met kamer " + s.kamer.getKamernummer();
            } else if (s.bezig) {
                status = "onderweg naar kamer";
            } else {
                status = "vrij inzetbaar";
            }
            String positie;
            if (s.huidigVakje != null) {
                positie = "(" + s.huidigVakje.x + "," + s.huidigVakje.y + ")";
            } else {
                positie = "geen positie";
            }
            text += "  Schoonmaker " + positie + "\n";
            text += "    Status: " + status + "\n\n";
        }
        if (!schoonmakerGevonden) text += "  Geen schoonmakers\n";

        rechtsArea.setText(text);
        rechtsArea.setCaretPosition(0);
    }

    // bepaal de locatie van een gast als leesbare tekst
    private String bepaalGastLocatie(Gast g) {
        if (g.inLift) return "in lift";
        if (g.huidigVakje == null) return "geen positie";
        Ruimte r = g.huidigVakje.ruimte;
        if (r instanceof Kamer k) return "kamer " + k.getKamernummer();
        if (r instanceof Restaurant) return "restaurant";
        if (r instanceof Fitnessruimte) return "fitness";
        if (r instanceof Bioscoop) return "bioscoop";
        if (r instanceof Lobby) return "lobby";
        if (r instanceof Lift) return "lift";
        return "(" + g.huidigVakje.x + "," + g.huidigVakje.y + ")";
    }

    // bepaal wat de gast op dit moment aan het doen is
    private String bepaalGastActiviteit(Gast g) {
        if (g.uitcheckend) return "aan het uitchecken";
        if (g.inLift) return "in lift";
        if (g.wachtOpLift) return "wacht op lift";
        if (g.huidigVakje != null && g.huidigVakje.ruimte instanceof Restaurant) return "aan het eten";
        if (g.huidigVakje != null && g.huidigVakje.ruimte instanceof Fitnessruimte) return "aan het sporten";
        if (g.huidigVakje != null && g.huidigVakje.ruimte instanceof Bioscoop) return "kijkt film";
        if (g.kamer != null && g.huidigVakje != null && g.huidigVakje.ruimte == g.kamer) return "in kamer";
        if (g.doelVakje != null) return "onderweg";
        return "wacht";
    }

    // tel het aantal gasten in de personenlijst
    private int aantalGasten() {
        int count = 0;
        for (Persoon p : hotel.personen) {
            if (p instanceof Gast) count++;
        }
        return count;
    }

    // stop de timer en hervat de simulatie bij sluiten
    @Override
    public void dispose() {
        //zorgt ervoor dat dispose maar 1x wordt aangeroepen
        if (gesloten) return;
        gesloten = true;
        if (refreshTimer != null) refreshTimer.stop();
        if (simulatieController != null) simulatieController.pauzeer();
        super.dispose();
    }
}
