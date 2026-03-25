package Vieuw;

import Model.Hotel;
import Model.HotelEventManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class HotelFrame extends JFrame {
    private Hotel hotel1;
    private Hotel hotel2;

    private HotelPanel panel1;
    private HotelPanel panel2;

    private HotelEventManager manager;

    public HotelFrame(Hotel hotel, HotelEventManager manager) {
        this.hotel1 = hotel;
        this.manager = manager;
        this.hotel2 = new Hotel();

        setTitle("Hotel Simulatie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panel1 = new HotelPanel(hotel1);
        panel2 = new HotelPanel(hotel2);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1, panel2);
        add(splitPane, BorderLayout.CENTER);

        JButton importButton1 = new JButton("Import layout 1");
        JButton importButton2 = new JButton("Import layout 2");
        JButton startButton = new JButton("Start simulatie");

        startButton.addActionListener(e -> {
            if (hotel.layout == null) {
                JOptionPane.showMessageDialog(this, "Laad eerst een layout!");
                return;
            }
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

        JPanel top = new JPanel();
        top.add(importButton1);
        top.add(importButton2);
        top.add(startButton);
        add(top, BorderLayout.NORTH);

        setSize(600, 600);
        setVisible(true);
    }
}
