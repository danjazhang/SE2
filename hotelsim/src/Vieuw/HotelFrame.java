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
        this.hotel2 = new Hotel();

        // Basis instellingen van het venster
        setTitle("Hotel Simulatie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel dat de hotel visualisatie toont
        panel = new HotelPanel(hotel);
        panel1 = new HotelPanel(hotel1);
        panel2 = new HotelPanel(hotel2);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1, panel2);
        add(splitPane, BorderLayout.CENTER);

        // UI componenten aanmaken
        JButton importButton = new JButton("Import layout");
        layoutSelector = new JComboBox<>();
        JButton importButton1 = new JButton("Import layout 1");
        JButton importButton2 = new JButton("Import layout 2");
        JButton startButton = new JButton("Start simulatie");

        // ================= IMPORT =================
        // Button om een hotel layout bestand te importeren
        importButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            // Toon bestandskiezer en check of gebruiker een bestand selecteert
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                // Laad hotel vanuit bestand
                Hotel nieuwHotel = Hotel.laadVanBestand(file.getAbsolutePath());

                // Voeg hotel toe aan manager en krijg een ID terug
                int id = hotelManager.addHotel(file.getName(), nieuwHotel);

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

        importButton1.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                hotel1.laadLayoutBestand(file.getAbsolutePath());
                panel1.setPreferredSize(new Dimension(hotel1.breedte * HotelPanel.tileSize, hotel1.hoogte * HotelPanel.tileSize));
                panel1.revalidate();
                panel1.repaint();
                pack();
            }
        });

        importButton2.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                hotel2.laadLayoutBestand(file.getAbsolutePath());
                panel2.setPreferredSize(new Dimension(hotel2.breedte * HotelPanel.tileSize, hotel2.hoogte * HotelPanel.tileSize));
                panel2.revalidate();
                panel2.repaint();
                pack();
            }
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