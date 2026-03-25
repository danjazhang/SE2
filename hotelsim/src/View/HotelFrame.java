package View;

import Model.Hotel;
import hotelevents.HotelEventManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

// View klasse: het hoofdvenster van de applicatie
// Toont twee hotel panels naast elkaar en knoppen voor import en simulatie
public class HotelFrame extends JFrame {

    // de twee hotel modellen voor de twee panels
    private Hotel hotel1;
    private Hotel hotel2;

    // de twee panels die de hotels tekenen
    private HotelPanel panel1;
    private HotelPanel panel2;

    // de event manager die events verstuurt naar alle listeners
    private HotelEventManager manager;

    // constructor: bouw het venster op met twee panels en knoppen
    public HotelFrame(Hotel hotel, HotelEventManager manager) {
        this.hotel1 = hotel;
        this.manager = manager;
        // maak een tweede hotel aan voor het tweede panel
        this.hotel2 = new Hotel();

        setTitle("Hotel Simulatie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // maak de twee hotel panels aan
        panel1 = new HotelPanel(hotel1);
        panel2 = new HotelPanel(hotel2);

        // plaats de panels naast elkaar in een splitpane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1, panel2);
        add(splitPane, BorderLayout.CENTER);

        // knoppen voor importeren en starten
        JButton importButton1 = new JButton("Import layout 1");
        JButton importButton2 = new JButton("Import layout 2");
        JButton startButton = new JButton("Start simulatie");

        // start de simulatie als er een layout geladen is
        startButton.addActionListener(e -> {
            if (hotel.layout == null) {
                JOptionPane.showMessageDialog(this, "Laad eerst een layout!");
                return;
            }
            manager.start(1);
        });

        // open een bestandskiezer en laad de layout voor hotel 1
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

        // open een bestandskiezer en laad de layout voor hotel 2
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

        // voeg de knoppen toe aan een panel bovenaan
        JPanel top = new JPanel();
        top.add(importButton1);
        top.add(importButton2);
        top.add(startButton);
        add(top, BorderLayout.NORTH);

        setSize(600, 600);
        setVisible(true);
    }
}
