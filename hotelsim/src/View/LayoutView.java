package View;

import Controller.SimulatieController;
import Model.*;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

// View klasse: tekent het hotel grid op het scherm
public class LayoutView extends JPanel implements ModelListener {

    Hotel hotel;
    // breedte van een vakje
    static final int TILE_W = 64;
    // hoogte van een normaal vakje (kamer, lift, etc.)
    static final int TILE_H = 64;
    // hoogte van een gang-vakje (smaller dan normaal)
    static final int GANG_H = 20;

    private static final int DREMPEL_BEZET = 3;
    private SimulatieController simulatieController;

    public LayoutView(Hotel hotel) {
        this.hotel = hotel;
    }

    public void setSimulatieController(SimulatieController sc) {
        this.simulatieController = sc;
    }

    public Hotel getHotel() { return hotel; }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        repaint();
    }

    @Override
    public void modelGewijzigd() { repaint(); }

    // Bereken de pixel-y voor een grid-rij y (1-geïndexeerd).
    // Gang-rijen zijn smaller dan normale rijen.
    private int pixelY(int gridY, int offsetY) {
        if (hotel.layout == null) return offsetY;
        int py = offsetY;
        for (int row = 1; row < gridY; row++) {
            py += rijHoogte(row);
        }
        return py;
    }

    // Hoogte in pixels van grid-rij y.
    private int rijHoogte(int gridY) {
        if (hotel.layout == null) return TILE_H;
        // check of er een gang-vakje op deze rij staat
        for (int x = 1; x <= hotel.breedte; x++) {
            Ruimte r = hotel.krijgRuimteOp(x, gridY);
            if (r instanceof Gang) return GANG_H;
        }
        return TILE_H;
    }

    // Totale pixel-hoogte van het grid
    private int totaleHoogte(int offsetY) {
        if (hotel.layout == null) return offsetY;
        int h = offsetY;
        for (int row = 1; row <= hotel.hoogte; row++) {
            h += rijHoogte(row);
        }
        return h;
    }

    @Override
    public Dimension getPreferredSize() {
        if (hotel == null || hotel.layout == null) return new Dimension(400, 400);
        return new Dimension(hotel.breedte * TILE_W, totaleHoogte(0) + 64);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (hotel.layout == null) {
            g.drawString("Geen layout geladen", 20, 20);
            return;
        }

        int offsetY = 0;

        // brandalarm balk
        if (hotel.brandalarmActief) {
            g.setColor(new Color(200, 30, 30));
            g.fillRect(0, 0, getWidth(), 40);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("🚨 BRANDALARM – EVACUEER DIRECT", 10, 26);
            offsetY += 40;
        }

        // tick teller en klok
        if (simulatieController != null) {
            g.setColor(new Color(40, 40, 40));
            g.fillRect(0, offsetY, getWidth(), 24);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 13));
            String klok = "HTE: " + simulatieController.getTikTeller()
                    + "    Real Time: " + simulatieController.getRealTijd();
            g.drawString(klok, 10, offsetY + 17);
            offsetY += 24;
        }

        // --- Stap 1: teken gang-rijen als aaneengesloten smalle balken ---
        for (int y = 1; y <= hotel.hoogte; y++) {
            int startX = -1, eindX = -1;
            for (int x = 1; x <= hotel.breedte; x++) {
                Ruimte r = hotel.krijgRuimteOp(x, y);
                if (r instanceof Gang) {
                    if (startX == -1) startX = x;
                    eindX = x;
                }
            }
            if (startX == -1) continue;

            int tekenX = (startX - 1) * TILE_W;
            int tekenY = pixelY(y, offsetY);
            int tekenB = (eindX - startX + 1) * TILE_W;
            int tekenH = GANG_H;

            g.setColor(new Color(210, 210, 210));
            g.fillRect(tekenX, tekenY, tekenB, tekenH);
            g.setColor(new Color(160, 160, 160));
            g.drawRect(tekenX, tekenY, tekenB, tekenH);
        }

        // --- Stap 2: teken alle andere ruimtes ---
        Set<Ruimte> getekend = new HashSet<>();

        for (int x = 1; x <= hotel.breedte; x++) {
            for (int y = 1; y <= hotel.hoogte; y++) {
                Ruimte r = hotel.krijgRuimteOp(x, y);
                if (r == null || r instanceof Gang) continue;
                if (getekend.contains(r)) continue;
                getekend.add(r);

                Color kleur;
                if (r instanceof Kamer k2)       kleur = k2.isBezet() ? new Color(220, 80, 80) : new Color(222, 229, 240);
                else if (r instanceof Restaurant) kleur = new Color(220, 193, 185);
                else if (r instanceof Bioscoop)   kleur = new Color(247, 234, 219);
                else if (r instanceof Fitnessruimte) kleur = new Color(235, 241, 223);
                else if (r instanceof Lift)       kleur = new Color(171, 87, 81);
                else if (r instanceof Trap)       kleur = new Color(162, 185, 103);
                else if (r instanceof Lobby)      kleur = new Color(123, 102, 158);
                else                              kleur = Color.LIGHT_GRAY;

                int tekenX = (r.posX - 1) * TILE_W;
                int tekenY = pixelY(r.posY, offsetY);
                int tekenB = r.breedte * TILE_W;
                // hoogte in pixels: som van alle rij-hoogtes die de ruimte beslaat
                int tekenH = 0;
                for (int row = r.posY; row < r.posY + r.hoogte; row++) {
                    tekenH += rijHoogte(row);
                }

                g.setColor(kleur);
                g.fillRect(tekenX, tekenY, tekenB, tekenH);
                g.setColor(Color.BLACK);
                g.drawRect(tekenX, tekenY, tekenB, tekenH);

                // bezet-overlay
                if (r.getAanwezigen().size() >= DREMPEL_BEZET) {
                    ((Graphics2D) g).setColor(new Color(220, 50, 50, 80));
                    ((Graphics2D) g).fillRect(tekenX, tekenY, tekenB, tekenH);
                }

                // labels
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                if (r instanceof Lift) {
                    g.drawString("Schacht", tekenX + 4, tekenY + 16);
                } else {
                    g.drawString(r.getClass().getSimpleName(), tekenX + 4, tekenY + 16);
                }

                if (r instanceof Kamer k) {
                    g.drawString(String.valueOf(k.getKamernummer()), tekenX + 4, tekenY + 30);
                    g.drawString(k.getSterrenLabel(), tekenX + 4, tekenY + 44);
                }

                // lift cabine
                if (r instanceof Lift) {
                    int cabineY = pixelY(hotel.lift.getHuidigeVerdieping(), offsetY);
                    g.setColor(new Color(202, 152, 150));
                    g.fillRect(tekenX, cabineY, TILE_W, TILE_H);
                    g.setColor(Color.BLACK);
                    g.drawRect(tekenX, cabineY, TILE_W, TILE_H);
                    g.drawString("Lift", tekenX + 4, cabineY + 16);
                }
            }
        }

        // --- Stap 3: teken personen ---
        for (Persoon p : hotel.personen) {
            if (p.huidigVakje == null) continue;
            int gridX = p.huidigVakje.x;
            int gridY = p.huidigVakje.y;
            int px = (gridX - 1) * TILE_W + TILE_W / 4;
            int py = pixelY(gridY, offsetY) + rijHoogte(gridY) / 4;

            if (p instanceof Gast gast) {
                px += (gast.gastId % 3) * 10;
                g.setColor(Color.WHITE);
                g.fillOval(px, py, TILE_W / 3, TILE_W / 3);
                g.setColor(Color.BLACK);
                g.drawOval(px, py, TILE_W / 3, TILE_W / 3);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString(String.valueOf(gast.gastId), px + TILE_W / 8, py + TILE_W / 3);
            } else if (p instanceof Schoonmaker) {
                g.setColor(new Color(232, 145, 68));
                g.fillRoundRect(px, py, TILE_W / 3, TILE_W / 3, 10, 10);
                g.setColor(Color.BLACK);
                g.drawRoundRect(px, py, TILE_W / 3, TILE_W / 3, 10, 10);
                g.setFont(new Font("Arial", Font.BOLD, 11));
                g.drawString("S", px + 7, py + 14);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillOval(px, py, TILE_W / 3, TILE_W / 3);
                g.setColor(Color.BLACK);
                g.drawOval(px, py, TILE_W / 3, TILE_W / 3);
            }
        }
    }
}
