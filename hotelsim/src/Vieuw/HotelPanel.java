package Vieuw;

import Model.Hotel;
import Model.Ruimte;
import Model.Kamer;
import Model.Restaurant;
import Model.Bioscoop;
import Model.Fitnesruimte;

import javax.swing.*;
import java.awt.*;

import Model.ModelListener;

public class HotelPanel extends JPanel implements ModelListener {
    Hotel hotel;

    static int tileSize = 64;

    public HotelPanel(Hotel hotel) {
        this.hotel = hotel;
        hotel.voegListenerToe(this);
    }

    @Override
    public void modelGewijzigd() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (hotel.layout == null) {
            g.drawString("Geen layout geladen", 20, 20);
            return;
        }

        for (int x = 1; x <= hotel.breedte; x++) {
            for (int y = 1; y <= hotel.hoogte; y++) {
                Ruimte r = hotel.krijgRuimteOp(x, y);
                if (r == null) continue;

                if (r instanceof Kamer) g.setColor(Color.BLUE);
                else if (r instanceof Restaurant) g.setColor(Color.ORANGE);
                else if (r instanceof Bioscoop) g.setColor(Color.RED);
                else if (r instanceof Fitnesruimte) g.setColor(Color.GREEN);
                else g.setColor(Color.LIGHT_GRAY);

                g.fillRect((x - 1) * tileSize, (y - 1) * tileSize, tileSize, tileSize);
                g.setColor(Color.BLACK);
                g.drawRect((x - 1) * tileSize, (y - 1) * tileSize, tileSize, tileSize);
            }
        }
    }
}
