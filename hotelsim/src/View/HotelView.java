package View;

import Controller.EventController;
import Controller.HotelController;
import Controller.LayoutController;
import Controller.SimulatieController;
import Model.Hotel;
import View.dialog.InstellingenDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

// Verantwoordelijkheid: hoofdvenster tonen en gebruikersacties doorgeven aan controllers
public class HotelView extends JFrame {

    //huidige hotel die getoond wordt
    private Hotel hotel;
    //het panel dat het hotel grid tekent
    private LayoutView panel;
    //panel met stop en pauze knop
    private SimulatieView simulatieView;
    //beheert start pauze en stop
    private SimulatieController simulatieController;
    //beheert hotel model
    private HotelController hotelController;
    //beheert laden van layouts
    private LayoutController layoutController;
    //dropdown om tussen geladen layouts te switchen
    private JComboBox<String> layoutSelector;
    //import knop
    private JButton importButton = new JButton("Import layout");
    //start knop
    private JButton startButton = new JButton("Start");
    // knop om een klein instellingenvenster te openen
    private JButton instellingenButton = new JButton("Instellingen");
    //toont events grafisch
    private EventLogView eventLogView;
    // We bewaren de scrollpane apart, zodat het instellingenpaneel
    // de volledige logweergave zichtbaar of onzichtbaar kan maken.
    private JScrollPane zijLog;
    // Het echte opbouwen van het instellingenvenster lives in een aparte dialogklasse.
    private InstellingenDialog instellingenDialog = new InstellingenDialog();

    //constructor
    public HotelView(HotelController hotelController, EventLogView eventLogView, EventController eventController, SimulatieController simulatieController) {

        this.hotelController = hotelController;
        this.eventLogView = eventLogView;
        this.simulatieController = simulatieController;
        //haal layoutcontroller op via hotelcontroller
        this.layoutController = hotelController.getLayoutController();
        this.hotel = hotelController.getHotel();


        setTitle("Hotel Simulatie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panel = new LayoutView(hotel);

        layoutSelector = new JComboBox<>();

        // =========================
        // IMPORT BUTTON
        // =========================
        importButton.addActionListener((ActionEvent e) -> {

            //maak nieuwe filepicker
            JFileChooser chooser = new JFileChooser();

            //open filepicker en als de gebruiker een bestand kiest wordt dit goed gekeurd
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                //haal het gekozen bestand op
                File file = chooser.getSelectedFile();

                //laad de layout via de controller
                int id = layoutController.laadVanBestand(file.getAbsolutePath(), file.getName());
                //check of laden is mislukt want id begint bij 1, dus -1 betekent mislukt
                if (id == -1) {
                    JOptionPane.showMessageDialog(this, "Fout bij laden van layout!");
                    return;
                }

                //haal hotel op via id
                Hotel nieuwHotel = layoutController.getHotel(id);
                //update hotel in de controller
                hotelController.setHotel(nieuwHotel);
                //update hotel
                this.hotel = nieuwHotel;
                //update panel
                panel.setHotel(nieuwHotel);
                //voeg layout toe aan dropdown
                layoutSelector.addItem(id + " - " + file.getName());
                //selecteer automatisch de laatste toegevoegde layout
                layoutSelector.setSelectedIndex(layoutSelector.getItemCount() - 1);
            }
        });

        // =========================
        // DROPDOWN
        // =========================
        layoutSelector.addActionListener((ActionEvent e) -> {
            //als er niks geselecteerd is stop dan
            if (layoutSelector.getSelectedItem() == null) return;
            //haal de geselcteerde tekst op
            String selected = (String) layoutSelector.getSelectedItem();
            //haal het id op voor -
            int id = Integer.parseInt(selected.split(" - ")[0]);
            //haal het bijbehorende hotel op
            Hotel geselecteerd = layoutController.getHotel(id);
            if (geselecteerd == null) return;
            //update het hotel en het tekenpaneel
            this.hotel = geselecteerd;
            panel.setHotel(geselecteerd);
        });

        startButton.addActionListener((ActionEvent e) -> {
            //check of er een layout geladen is
            if (!hotelController.heeftLayout()) {
                JOptionPane.showMessageDialog(this, "Kies eerst een layout!");
                return;
            }
            // Zorg dat ook bij de allereerste start de actuele snelheid uit de GUI wordt gebruikt.
            simulatieView.pasSnelheidToe();
            //start de simulatie
            simulatieController.start();
        });

        // Open een klein instellingenpaneel met snelheid, eventlog en grootte.
        instellingenButton.addActionListener((ActionEvent e) -> openInstellingenPaneel());

        // =========================
        // UI
        // =========================
        JPanel top = new JPanel();
        top.add(importButton);
        top.add(layoutSelector);
        top.add(startButton);
        top.add(instellingenButton);

        //voeg hotel grid toe in het midden
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(panel), BorderLayout.CENTER);

        //maak de simulatieview
        simulatieView = new SimulatieView(simulatieController);
        top.add(simulatieView);

        // Toon de eventlog links met zowel verticale als horizontale scrollbars.
        zijLog = new JScrollPane(
                eventLogView.getLogArea(),
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        zijLog.setPreferredSize(new Dimension(240, 400));
        add(zijLog, BorderLayout.WEST);

        //venster grootte
        setSize(1200, 850);
        //venster in het midden van de scherm
        setLocationRelativeTo(null);
        //maak venster zichtbaar
        setVisible(true);
    }

    // Dit instellingenpaneel bundelt een paar simpele instellingen op een plaats.
    // Zo hoeft de gebruiker niet in de code te zoeken om snelheid, logzichtbaarheid of zoom te veranderen.
    private void openInstellingenPaneel() {
        InstellingenDialog.InstellingenResult resultaat = instellingenDialog.toon(
                this,
                simulatieView.getGekozenSnelheid(),
                zijLog.isVisible(),
                LayoutView.getTileSize()
        );

        if (resultaat == null) return;

        // Pas eerst de snelheid aan via de bestaande SimulatieView, zodat alle logica op een plek blijft.
        simulatieView.stelSnelheidIn(resultaat.getSnelheid());

        // Toon of verberg de eventlog zonder de rest van het venster te veranderen.
        zijLog.setVisible(resultaat.isEventlogZichtbaar());

        // Kies een andere tileSize voor een kleinere of grotere hotelweergave.
        panel.setTileSize(resultaat.getTileSize());

        revalidate();
        repaint();
    }
}
