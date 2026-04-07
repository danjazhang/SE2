package View;

import Controller.HotelController;
import Model.Hotel;
import View.EventLog;
import View.HotelPanel;
import hotelevents.HotelEventManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class HotelFrame extends JFrame {

    private Hotel hotel;
    private HotelPanel panel;
    private HotelController controller;
    private HotelEventManager manager;
    private JComboBox<String> layoutSelector;

    public HotelFrame(HotelController controller) {

        this.controller = controller;
        this.hotel = controller.getHotel();
        this.manager = controller.getManager();

        setTitle("Hotel Simulatie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panel = new HotelPanel(hotel);

        JButton importButton = new JButton("Import layout");
        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pauze");
        JButton stopButton = new JButton("Stop");

        layoutSelector = new JComboBox<>();

        // =========================
        // IMPORT BUTTON
        // =========================
        importButton.addActionListener((ActionEvent e) -> {

            JFileChooser chooser = new JFileChooser();

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                File file = chooser.getSelectedFile();

                // ✅ DOĞRU HALİ
                Hotel nieuwHotel = controller.importHotel(
                        file.getAbsolutePath(),
                        file.getName()
                );

                // güvenlik (null gelirse)
                if (nieuwHotel == null) {
                    JOptionPane.showMessageDialog(this, "Fout bij laden van layout!");
                    return;
                }

                this.hotel = nieuwHotel;
                panel.setHotel(nieuwHotel);

                layoutSelector.addItem(file.getName());
            }
        });

        // =========================
        // DROPDOWN
        // =========================
        layoutSelector.addActionListener((ActionEvent e) -> {

            int index = layoutSelector.getSelectedIndex();
            if (index < 0) return;

            Hotel geselecteerd = controller.getHotelByIndex(index);
            if (geselecteerd == null) return;

            this.hotel = geselecteerd;
            panel.setHotel(geselecteerd);
        });

        // =========================
        // START BUTTON
        // =========================
        startButton.addActionListener((ActionEvent e) -> {

            if (hotel == null || hotel.layout == null) {
                JOptionPane.showMessageDialog(this, "Kies eerst een layout!");
                return;
            }

            // 👉 senin sistemde ID yok → sabit başlat
            manager.start(0);
        });

        // =========================
        // PAUZE
        // =========================
        pauseButton.addActionListener((ActionEvent e) -> {

            manager.pauze();

            if (pauseButton.getText().equals("Pauze")) {
                pauseButton.setText("Resume");
            } else {
                pauseButton.setText("Pauze");
            }
        });

        // =========================
        // STOP
        // =========================
        stopButton.addActionListener((ActionEvent e) -> {
            manager.stop();
        });

        // =========================
        // UI
        // =========================
        JPanel top = new JPanel();
        top.add(importButton);
        top.add(layoutSelector);
        top.add(startButton);
        top.add(pauseButton);
        top.add(stopButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(panel), BorderLayout.CENTER);

        EventLog.getLogArea().setPreferredSize(new Dimension(200, 0));
        add(new JScrollPane(EventLog.getLogArea()), BorderLayout.WEST);

        setSize(730, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}