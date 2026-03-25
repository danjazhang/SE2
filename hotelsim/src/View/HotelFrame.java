package View;
import Model.Hotel;
import Model.HotelManager;
import View.HotelPanel;
import hotelevents.HotelEventManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class HotelFrame extends JFrame {
    private Hotel hotel; // huidige geselecteerde hotel
    private HotelPanel panel; // panel dat de hotel layout tekent
    private HotelEventManager manager; // event manager voor simulatie

    private HotelManager hotelManager = new HotelManager(); // beheert meerdere hotels
    private JComboBox<String> layoutSelector; // dropdown om hotel layouts te kiezen

    public HotelFrame(Hotel hotel, HotelEventManager manager) {
        this.hotel = hotel;
        this.manager = manager;


        // Basis instellingen van het venster
        setTitle("Hotel Simulatie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel dat de hotel visualisatie toont
        panel = new HotelPanel(hotel);

        // UI componenten aanmaken
        JButton importButton = new JButton("Import layout");
        layoutSelector = new JComboBox<>();
        JButton startButton = new JButton("Start simulatie");

        // ================= IMPORT =================
        // Button om een hotel layout bestand te importeren
        importButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            // Toon bestandskiezer en check of gebruiker een bestand selecteert
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                // Laad hotel vanuit bestand
                Hotel nieuwHotel = new Hotel();
                nieuwHotel.laadLayoutBestand(file.getAbsolutePath());

                // Voeg hotel toe aan manager en krijg een ID terug
                int id = hotelManager.addLayout(file.getName(), nieuwHotel.layout);

                // sla hotel op in loadedHotels
                hotelManager.loadHotel(id, nieuwHotel);

                // Voeg item toe aan dropdown (ID + bestandsnaam)
                layoutSelector.addItem(id + " - " + file.getName());

                // Selecteer automatisch het laatst toegevoegde hotel
                layoutSelector.setSelectedIndex(layoutSelector.getItemCount() - 1);
            }
        });

        // ================= SELECTOR =================
        // Wanneer gebruiker een andere layout kiest
        layoutSelector.addActionListener(e -> {
            if (layoutSelector.getSelectedItem() == null) return;

            String selected = (String) layoutSelector.getSelectedItem();

            // ID uit de string halen (voor "-")
            int id = Integer.parseInt(selected.split(" - ")[0]);

            // Haal bijbehorend hotel op uit manager
            this.hotel = hotelManager.getHotel(id);

            if (this.hotel == null) return;

            // Update het panel met het nieuwe hotel
            panel.setHotel(this.hotel);
        });

        // ================= START =================
        // Start de simulatie wanneer knop wordt ingedrukt
        startButton.addActionListener(e -> {
            // Controleer of een geldig hotel en layout aanwezig zijn
            if (panel.getHotel() == null || panel.getHotel().layout == null) {
                JOptionPane.showMessageDialog(this, "Kies eerst een layout!");
                return;
            }

            // Start de simulatie via de event manager
            manager.start(1);
        });

        // ================= TOP BAR =================
        // Bovenste balk met knoppen en dropdown
        JPanel top = new JPanel();
        top.add(importButton);
        top.add(layoutSelector);
        top.add(startButton);

        add(top, BorderLayout.NORTH);

        // ================= CENTER =================
        // Hoofdweergave met scroll mogelijkheid voor grotere layouts
        add(new JScrollPane(panel), BorderLayout.CENTER);

        // Window instellingen
        setSize(800, 600);
        setVisible(true);
    }
}