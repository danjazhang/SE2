package View;

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

                if (r instanceof Kamer) g.setColor(new Color(70, 130, 180));
                else if (r instanceof Restaurant) g.setColor(Color.ORANGE);
                else if (r instanceof Bioscoop) g.setColor(Color.RED);
                else if (r instanceof Fitnesruimte) g.setColor(Color.GREEN);
                else g.setColor(Color.LIGHT_GRAY);

                g.fillRect(x * tileSize, (y - 1) * tileSize, tileSize, tileSize);
                g.setColor(Color.BLACK);
                g.drawRect(x * tileSize, (y - 1) * tileSize, tileSize, tileSize);

                String naam = r.getClass().getSimpleName();
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(naam, x * tileSize + 4, (y - 1) * tileSize + 16);
            }
        }

        // teken lift helemaal links
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, tileSize, (hotel.hoogte + 1) * tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, tileSize, (hotel.hoogte + 1) * tileSize);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Lift", 4, 16);

        // teken trap helemaal rechts
        int trapX = (hotel.breedte + 1) * tileSize;
        g.setColor(Color.MAGENTA);
        g.fillRect(trapX, 0, tileSize, (hotel.hoogte + 1) * tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(trapX, 0, tileSize, (hotel.hoogte + 1) * tileSize);
        g.drawString("Trap", trapX + 4, 16);

        // teken lobby onderin
        int lobbyY = hotel.hoogte * tileSize;
        g.setColor(Color.YELLOW);
        g.fillRect(tileSize, lobbyY, hotel.breedte * tileSize, tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(tileSize, lobbyY, hotel.breedte * tileSize, tileSize);
        g.drawString("Lobby", tileSize + 4, lobbyY + 16);
    }
}
