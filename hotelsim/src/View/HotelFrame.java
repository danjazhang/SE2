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
import java.util.ArrayList;
import java.util.List;

public class HotelFrame extends JFrame {

    private Hotel hotel;
    private HotelPanel panel;
    private HotelController controller;
    private HotelEventManager manager;
    private JComboBox<String> layoutSelector;
    private List<Integer> layoutIds;

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
        layoutIds = new ArrayList<>();

        // Laad een layout in en voeg die meteen toe aan de keuzelijst.
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

                layoutIds.add(controller.getLaatsteHotelId());
                layoutSelector.addItem(file.getName());
                layoutSelector.setSelectedIndex(layoutSelector.getItemCount() - 1);
            }
        });

        // De dropdown toont alle geladen layouts.
        // De echte ids worden apart bewaard, zodat wisselen stabiel blijft.
        layoutSelector.addActionListener((ActionEvent e) -> {

            int index = layoutSelector.getSelectedIndex();
            if (index < 0) return;
            if (index >= layoutIds.size()) return;

            Hotel geselecteerd = controller.getHotelById(layoutIds.get(index));
            if (geselecteerd == null) return;

            this.hotel = geselecteerd;
            panel.setHotel(geselecteerd);
        });

        // Start de simulatie alleen als er een layout geladen is.
        startButton.addActionListener((ActionEvent e) -> {

            if (hotel == null || hotel.layout == null) {
                JOptionPane.showMessageDialog(this, "Kies eerst een layout!");
                return;
            }

            // 👉 senin sistemde ID yok → sabit başlat
            manager.start(0);
        });

        // Pauze wisselt tussen pauzeren en hervatten.
        pauseButton.addActionListener((ActionEvent e) -> {

            manager.pauze();

            if (pauseButton.getText().equals("Pauze")) {
                pauseButton.setText("Resume");
            } else {
                pauseButton.setText("Pauze");
            }
        });

        // Stop zet de simulatie volledig stil.
        stopButton.addActionListener((ActionEvent e) -> {
            manager.stop();
        });

        // Opbouw van het venster: knoppen bovenaan, hotel in het midden en log links.
        JPanel top = new JPanel();
        top.add(importButton);
        top.add(layoutSelector);
        top.add(startButton);
        top.add(pauseButton);
        top.add(stopButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(panel), BorderLayout.CENTER);

        JScrollPane eventScrollPane = new JScrollPane(EventLog.getLogArea());
        eventScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        eventScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        eventScrollPane.setPreferredSize(new Dimension(240, 0));
        add(eventScrollPane, BorderLayout.WEST);

        setSize(730, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
