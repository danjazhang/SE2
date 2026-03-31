package View;

//import Controller.Simulatie;
import Model.Hotel;
import Model.HotelManager;
import Model.Klok;

import hotelevents.HotelEventManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class HotelFrame extends JFrame {

    private Hotel hotel;
    private HotelPanel panel;
    private HotelEventManager manager;
    //private Simulatie simulatie;

    private HotelManager hotelManager = new HotelManager();
    private JComboBox<String> layoutSelector;

    public HotelFrame(Hotel hotel, HotelEventManager manager) {
        this.hotel = hotel;
        this.manager = manager;

        // simulatie zonder Scenario
        //simulatie = new Simulatie(hotel, new Klok(), manager);

        setTitle("Hotel Simulatie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panel = new HotelPanel(hotel);

        JButton importButton = new JButton("Import layout");
        layoutSelector = new JComboBox<>();
        JButton startButton = new JButton("Start simulatie");
        JButton pauseButton = new JButton("Pauze");
        JButton stopButton = new JButton("Stop simulatie");

        // IMPORT
        importButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                Hotel nieuwHotel = new Hotel();
                nieuwHotel.laadLayoutBestand(file.getAbsolutePath());

                int id = hotelManager.addLayout(file.getName(), nieuwHotel.layout);
                hotelManager.loadHotel(id, nieuwHotel);

                layoutSelector.addItem(id + " - " + file.getName());
                layoutSelector.setSelectedIndex(layoutSelector.getItemCount() - 1);
            }
        });

        // SELECTOR
        layoutSelector.addActionListener(e -> {
            if (layoutSelector.getSelectedItem() == null) return;

            String selected = (String) layoutSelector.getSelectedItem();
            int id = Integer.parseInt(selected.split(" - ")[0]);

            this.hotel = hotelManager.getHotel(id);
            if (this.hotel == null) return;

            panel.setHotel(this.hotel);
        });

        // START
        startButton.addActionListener(e -> {
            if (panel.getHotel() == null || panel.getHotel().layout == null) {
                JOptionPane.showMessageDialog(this, "Kies eerst een layout!");
                return;
            }

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

        JPanel top = new JPanel();
        top.add(importButton);
        top.add(layoutSelector);
        top.add(startButton);
        top.add(pauseButton);
        top.add(stopButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(panel), BorderLayout.CENTER);

        setSize(800, 600);
        setVisible(true);
    }
}