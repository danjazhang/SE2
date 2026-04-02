package View;

import Controller.LayoutController;
import Model.Hotel;
import Model.HotelManager;
import Model.Persoon;
import Model.Ruimte;
import View.HotelPanel;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

// View klasse: het hoofdvenster van de applicatie
// Toont één hotel panel met een dropdown om tussen layouts te wisselen
public class HotelFrame extends JFrame {

    // het momenteel geselecteerde hotel
    private Hotel hotel;

    // het panel dat de hotel layout tekent
    private HotelPanel panel;

    //beheert het laden en opslaan van layouts
    private LayoutController layoutController = new LayoutController();


    //moet in evencontroller
    // de event manager die events verstuurt naar alle listeners
    private HotelEventManager manager;

    // dropdown om tussen geladen hotel layouts te kiezen
    private JComboBox<String> layoutSelector;

    // constructor: bouw het venster op
    public HotelFrame(Hotel hotel, HotelEventManager manager) {
        this.hotel = hotel;
        this.manager = manager;

        // basisinstellingen van het venster
        setTitle("Hotel Simulatie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // panel dat de hotel visualisatie toont
        panel = new HotelPanel(hotel);

        // UI componenten aanmaken
        JButton importButton = new JButton("Import layout");
        layoutSelector = new JComboBox<>();
        JButton startButton = new JButton("Start simulatie");
        JButton pauseButton = new JButton("Pauzeer");
        JButton stopButton = new JButton("Stop");

        // button om een hotel layout bestand te importeren
        importButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            // toon bestandskiezer en check of gebruiker een bestand selecteert
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                //laad hotel via layoutcontroller en sla het id op
                int id = layoutController.laadHotel(file.getAbsolutePath(), file.getName());
                // haal het geladen hotel op via het id
                Hotel nieuwHotel = layoutController.getHotel(id);

                // registreer de ruimtes uit de layout als event listeners
                for (Ruimte r : nieuwHotel.ruimtes) {
                    if (r instanceof HotelEventListener) {
                        manager.register((HotelEventListener) r);
                    }
                }

                //hotel bijwerken naar nieuwe hotel
                this.hotel = nieuwHotel;
                //tekent nieuwe hotel
                panel.setHotel(nieuwHotel);

                // voeg item toe aan dropdown (ID + bestandsnaam)
                layoutSelector.addItem(id + " - " + file.getName());

                // selecteer automatisch het laatst toegevoegde hotel
                layoutSelector.setSelectedIndex(layoutSelector.getItemCount() - 1);
            }
        });

        // wanneer gebruiker een andere layout kiest in de dropdown
        layoutSelector.addActionListener(e -> {
            if (layoutSelector.getSelectedItem() == null) return;

            String selected = (String) layoutSelector.getSelectedItem();

            // ID uit de string halen (voor " - ")
            int id = Integer.parseInt(selected.split(" - ")[0]);

            // haal bijbehorend hotel op uit manager
            this.hotel = layoutController.getHotel(id);


            if (this.hotel == null) return;

            // update het panel met het nieuwe hotel
            panel.setHotel(this.hotel);
        });

        // start de simulatie wanneer knop wordt ingedrukt
        startButton.addActionListener(e -> {
            // controleer of een geldig hotel en layout aanwezig zijn
            if (panel.getHotel() == null || panel.getHotel().layout == null) {
                JOptionPane.showMessageDialog(this, "Kies eerst een layout!");
                return;
            }
            // start de simulatie via de event manager
            manager.start(1);
        });

        // PAUZE
        pauseButton.addActionListener(e -> {
            manager.pauze();

            if (pauseButton.getText().equals("Pauze")) {
                pauseButton.setText("Resume");
            } else {
                pauseButton.setText("Pauze");
            }
        });

        // STOP
        stopButton.addActionListener(e -> {
            manager.stop();
        });

        // bovenste balk met knoppen en dropdown
        JPanel top = new JPanel();
        top.add(importButton);
        top.add(layoutSelector);
        top.add(startButton);
        top.add(pauseButton);
        top.add(stopButton);
        add(top, BorderLayout.NORTH);

        // hoofdweergave met scroll mogelijkheid voor grotere layouts
        add(new JScrollPane(panel), BorderLayout.CENTER);

        //bepaal grootte van event log
        EventLog.getLogArea().setPreferredSize(new Dimension (200,0));
        //maak het tekstvak scrollbaar
        JScrollPane logPane = new JScrollPane(EventLog.getLogArea());
        //voeg event log toe aan de onderkant van het venster
        add(logPane, BorderLayout.WEST);
        //venster grootte
        setSize(730, 650);
        //laad venster in midden van scherm
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
