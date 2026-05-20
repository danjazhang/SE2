package View;

import Controller.EventController;
import Controller.HotelController;
import Controller.LayoutController;
import Controller.SimulatieController;
import Model.Hotel;

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
    //toont events grafisch
    private EventLogView eventLogView;
    //event controller voor het registreren van listeners
    private EventController eventController;

    //constructor
    public HotelView(HotelController hotelController, EventLogView eventLogView, EventController eventController, SimulatieController simulatieController) {

        this.hotelController = hotelController;
        this.eventLogView = eventLogView;
        this.eventController = eventController;
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
                //registreer layoutview als observer
                hotelController.voegListenerToe(panel);
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
            //haal de geselecteerde tekst op
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
            //zorg dat de actuele snelheid uit de GUI wordt gebruikt bij de start
            simulatieView.pasSnelheidToe();
            //haal het gekozen scenario op uit de simulatieview en start daarmee
            int scenario = simulatieView.getGekozenScenario();
            simulatieController.start(scenario);
        });

        // =========================
        // UI
        // =========================
        JPanel top = new JPanel();
        top.add(importButton);
        top.add(layoutSelector);
        top.add(startButton);

        //voeg hotel grid toe in het midden
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(panel), BorderLayout.CENTER);

        //maak de simulatieview
        simulatieView = new SimulatieView(simulatieController);
        top.add(simulatieView);

        //toon de eventlog links zonder horizontale scrollbar
        JScrollPane zijLog = new JScrollPane(eventLogView.getLogArea(),
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        zijLog.setPreferredSize(new Dimension(360, 400));
        add(zijLog, BorderLayout.WEST);

        //venster grootte
        setSize(1200, 800);
        //venster in het midden van het scherm
        setLocationRelativeTo(null);
        //maak venster zichtbaar
        setVisible(true);
    }
}
