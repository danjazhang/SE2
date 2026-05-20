package View;

import Model.*;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.*;

import javax.swing.*;
import java.awt.*;

// View klasse: tekent het hotel grid op het scherm
// Implementeert ModelListener zodat het automatisch hertekent als het Model verandert
public class LayoutView extends JPanel implements ModelListener {

    // het hotel model waarvan de data gelezen wordt
    Hotel hotel;

    // de pixelgrootte van elk vakje in het grid
    static int tileSize = 64;

    // drempel voor het markeren van drukke ruimtes
    private static final int DREMPEL_BEZET = 3;

    // constructor
    public LayoutView(Hotel hotel) {
        this.hotel = hotel;
    }

    public Hotel getHotel() { return hotel; }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        repaint();
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

        java.util.Set<Ruimte> getekend = new java.util.HashSet<>();

        for (int x = 1; x <= hotel.breedte; x++) {
            for (int y = 1; y <= hotel.hoogte; y++) {
                Ruimte r = hotel.krijgRuimteOp(x, y);
                if (r == null) continue;
                if (getekend.contains(r)) continue;
                getekend.add(r);

                // kies kleur op basis van ruimtetype
                if (r instanceof Kamer) g.setColor(new Color(222, 229, 240));
                else if (r instanceof Restaurant) g.setColor(new Color(228, 223, 235));
                else if (r instanceof Bioscoop) g.setColor(new Color(247, 234, 219));
                else if (r instanceof Fitnessruimte) g.setColor(new Color(235, 241, 223));
                else if (r instanceof Lift) g.setColor(new Color(171, 87, 81));
                else if (r instanceof Trap) g.setColor(new Color(162, 185, 103));
                else if (r instanceof Lobby) g.setColor(new Color(123, 102, 158));
                else g.setColor(Color.LIGHT_GRAY);

                int tekenX = (r.posX - 1) * tileSize;
                int tekenY = (r.posY - 1) * tileSize;
                int tekenB = r.breedte * tileSize;
                int tekenH = r.hoogte * tileSize;

                g.fillRect(tekenX, tekenY, tekenB, tekenH);
                g.setColor(Color.BLACK);
                g.drawRect(tekenX, tekenY, tekenB, tekenH);

                // teken licht rode overlay als de ruimte druk bezet is
                if (r.getAanwezigen().size() >= DREMPEL_BEZET) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(new Color(220, 50, 50, 80));
                    g2d.fillRect(tekenX, tekenY, tekenB, tekenH);
                }

                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                if (r instanceof Lift) g.drawString("Schacht", tekenX + 4, tekenY + 16);
                else g.drawString(r.getClass().getSimpleName(), tekenX + 4, tekenY + 16);

                // teken kamernummer en sterren als het een kamer is
                if (r instanceof Kamer) {
                    g.drawString(String.valueOf(((Kamer) r).getKamernummer()), tekenX + 4, tekenY + 30);
                    g.drawString(((Kamer) r).getSterrenLabel(), tekenX + 4, tekenY + 44);
                }

                // teken lift cabine
                if (r instanceof Lift) {
                    int cabineY = (hotel.lift.getHuidigeVerdieping() - 1) * tileSize;
                    g.setColor(new Color(202, 152, 150));
                    g.fillRect(tekenX, cabineY, tileSize, tileSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(tekenX, cabineY, tileSize, tileSize);
                    g.drawString("Lift", tekenX + 4, cabineY + 16);
                }
            }
        }

        // teken personen
        for (Persoon p : hotel.personen) {
            if (p.huidigVakje == null) continue;
            int px = (p.huidigVakje.x - 1) * tileSize + tileSize / 4;
            int py = (p.huidigVakje.y - 1) * tileSize + tileSize / 4;

            if (p instanceof Gast) {
                int offset = (((Gast) p).gastId % 3) * 10;
                px += offset;
            }

            if (p instanceof Gast) {
                g.setColor(Color.WHITE);
                g.fillOval(px, py, tileSize / 3, tileSize / 3);
                g.setColor(Color.BLACK);
                g.drawOval(px, py, tileSize / 3, tileSize / 3);
            } else if (p instanceof Schoonmaker) {
                g.setColor(new Color(232, 145, 68));
                g.fillRoundRect(px, py, tileSize / 3, tileSize / 3, 10, 10);
                g.setColor(Color.BLACK);
                g.drawRoundRect(px, py, tileSize / 3, tileSize / 3, 10, 10);
                g.setFont(new Font("Arial", Font.BOLD, 11));
                g.drawString("S", px + 7, py + 14);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillOval(px, py, tileSize / 3, tileSize / 3);
                g.setColor(Color.BLACK);
                g.drawOval(px, py, tileSize / 3, tileSize / 3);
            }

            if (p instanceof Gast) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString(String.valueOf(((Gast) p).gastId), px + tileSize / 8, py + tileSize / 3);
            }
        }
    }
}
