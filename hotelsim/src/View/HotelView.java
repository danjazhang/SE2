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

    private Hotel hotel;
    private LayoutView panel;
    private SimulatieView simulatieView;
    private SimulatieController simulatieController;
    private HotelController hotelController;
    private LayoutController layoutController;
    private JComboBox<String> layoutSelector;
    private JButton importButton = new JButton("Import layout");
    private JButton startButton = new JButton("Start");
    private EventLogView eventLogView;

    public HotelView(HotelController hotelController, EventLogView eventLogView, EventController eventController, SimulatieController simulatieController) {

        this.hotelController = hotelController;
        this.eventLogView = eventLogView;
        this.simulatieController = simulatieController;
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

            JFileChooser chooser = new JFileChooser();

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                File file = chooser.getSelectedFile();

                int id = layoutController.laadVanBestand(file.getAbsolutePath(), file.getName());
                if (id == -1) {
                    JOptionPane.showMessageDialog(this, "Fout bij laden van layout!");
                    return;
                }

                Hotel nieuwHotel = layoutController.getHotel(id);
                hotelController.setHotel(nieuwHotel);
                this.hotel = nieuwHotel;
                panel.setHotel(nieuwHotel);
                layoutSelector.addItem(id + " - " + file.getName());
                layoutSelector.setSelectedIndex(layoutSelector.getItemCount() - 1);
            }
        });

        // =========================
        // DROPDOWN
        // =========================
        layoutSelector.addActionListener((ActionEvent e) -> {
            if (layoutSelector.getSelectedItem() == null) return;
            String selected = (String) layoutSelector.getSelectedItem();
            int id = Integer.parseInt(selected.split(" - ")[0]);
            Hotel geselecteerd = layoutController.getHotel(id);
            if (geselecteerd == null) return;
            this.hotel = geselecteerd;
            panel.setHotel(geselecteerd);
        });

        startButton.addActionListener((ActionEvent e) -> {
            if (!hotelController.heeftLayout()) {
                JOptionPane.showMessageDialog(this, "Kies eerst een layout!");
                return;
            }
            simulatieController.start();
        });

        // =========================
        // UI
        // =========================
        JPanel top = new JPanel();
        top.add(importButton);
        top.add(layoutSelector);
        top.add(startButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(panel), BorderLayout.CENTER);

        simulatieView = new SimulatieView(simulatieController);
        top.add(simulatieView);

        eventLogView.getLogArea().setPreferredSize(new Dimension(200, 0));
        add(new JScrollPane(eventLogView.getLogArea()), BorderLayout.WEST);

        setSize(730, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}