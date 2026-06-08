package View;

import Controller.SimulatieController;
import Model.*;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.*;

import javax.swing.*;
import java.awt.*;

// View klasse: tekent het hotel grid op het scherm
public class LayoutView extends JPanel implements ModelListener {

    Hotel hotel;
    static int tileSize = 64;
    private static final int DREMPEL_BEZET = 3;

    // simulatiecontroller voor de realtime klok en tick teller
    private SimulatieController simulatieController;

    // callback voor lobby klik
    private Runnable onLobbyClick;

    public LayoutView(Hotel hotel) {
        this.hotel = hotel;

        // mouse listener voor klikken op het grid
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                // bereken welke tile is aangeklikt op basis van muiscoördinaten
                int x = e.getX() / tileSize + 1;
                int y = e.getY() / tileSize + 1;

                // zoek welke ruimte op deze positie zit in het model
                if (LayoutView.this.hotel == null || LayoutView.this.hotel.layout == null) return;

                // check of de lobby aangeklikt is — die staat hardcoded onder het grid
                int lobbyRij = LayoutView.this.hotel.hoogte;
                if (y == lobbyRij) {
                    if (onLobbyClick != null) onLobbyClick.run();
                    return;
                }

                Ruimte r = LayoutView.this.hotel.krijgRuimteOp(x, y);

                // check of het de lobby is
                if (r instanceof Lobby) {
                    if (onLobbyClick != null) {
                        onLobbyClick.run();
                    }
                }
            }
        });
    }

    // setter zodat HotelView kan koppelen wat er gebeurt bij klik
    public void setOnLobbyClick(Runnable onLobbyClick) {
        this.onLobbyClick = onLobbyClick;
    }

    // stel de simulatiecontroller in zodat de klok getoond kan worden
    public void setSimulatieController(SimulatieController sc) {
        this.simulatieController = sc;
    }

    public Hotel getHotel() { return hotel; }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        // stel de vaste grootte in zodat de scrollpane niet terugspringt
        if (hotel != null && hotel.breedte > 0 && hotel.hoogte > 0) {
            // +1 rij voor de lobby die onder het grid getekend wordt
            setPreferredSize(new Dimension(hotel.breedte * tileSize, (hotel.hoogte + 1) * tileSize));
            revalidate();
        }
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

        int offsetY = 0;

        // teken rode brandalarm balk bovenaan als het alarm actief is
        if (hotel.brandalarmActief) {
            g.setColor(new Color(200, 30, 30));
            g.fillRect(0, 0, getWidth(), 40);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("🚨 BRANDALARM – EVACUEER DIRECT", 10, 26);
            offsetY += 40;
        }

        // teken de HTE tick teller en realtime klok
        if (simulatieController != null) {
            g.setColor(new Color(40, 40, 40));
            g.fillRect(0, offsetY, getWidth(), 24);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 13));

            String klok = "HTE: " + simulatieController.getTikTeller() +
                    "    Real Time: " + simulatieController.getRealTijd();

            g.drawString(klok, 10, offsetY + 17);
            offsetY += 24;
        }

        java.util.Set<Ruimte> getekend = new java.util.HashSet<>();

        for (int x = 1; x <= hotel.breedte; x++) {
            for (int y = 1; y <= hotel.hoogte; y++) {

                Ruimte r = hotel.krijgRuimteOp(x, y);
                if (r == null) continue;
                if (getekend.contains(r)) continue;
                getekend.add(r);

                if (r instanceof Kamer) {
                    if (((Kamer) r).isBezet()) {
                        g.setColor(new Color(220, 80, 80));
                    } else {
                        g.setColor(new Color(222, 229, 240));
                    }
                } else if (r instanceof Restaurant) {
                    g.setColor(new Color(220, 193, 185));
                } else if (r instanceof Bioscoop) {
                    g.setColor(new Color(247, 234, 219));
                } else if (r instanceof Fitnessruimte) {
                    g.setColor(new Color(235, 241, 223));
                } else if (r instanceof Lift) {
                    g.setColor(new Color(171, 87, 81));
                } else if (r instanceof Trap) {
                    g.setColor(new Color(162, 185, 103));
                } else if (r instanceof Lobby) {
                    g.setColor(new Color(123, 102, 158));
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }

                int tekenX = (r.posX - 1) * tileSize;
                int tekenY;
                int tekenB = r.breedte * tileSize;
                int tekenH = r.hoogte * tileSize;

                // lobby hardcoded onder de hotel
                if (r instanceof Lobby) {
                    tekenY = (hotel.hoogte - 1) * tileSize + offsetY;
                    tekenH = tileSize;
                    // breedte: van rechts van lift tot links van trap
                    tekenB = (hotel.breedte - 3) * tileSize;
                    tekenX = tileSize; // begin rechts van de lift
                // lift hardcoded van bovenaan tot en met de lobby
                } else if (r instanceof Lift) {
                    tekenY = offsetY;
                    tekenH = hotel.hoogte * tileSize;
                // trap hardcoded van bovenaan tot en met de lobby
                } else if (r instanceof Trap) {
                    tekenY = offsetY;
                    tekenH = hotel.hoogte * tileSize;
                } else {
                    // gebruik de onderste rij van de ruimte als startpunt
                    // zodat ruimtes met meerdere vakjes hoogte correct getekend worden
                    int ondersteRij = r.posY + r.hoogte - 1;
                    tekenY = (hotel.hoogte - ondersteRij - 1) * tileSize + offsetY;
                }

                g.fillRect(tekenX, tekenY, tekenB, tekenH);
                g.setColor(Color.BLACK);
                g.drawRect(tekenX, tekenY, tekenB, tekenH);

                if (r.getAanwezigen().size() >= DREMPEL_BEZET) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(new Color(220, 50, 50, 80));
                    g2d.fillRect(tekenX, tekenY, tekenB, tekenH);
                }

                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));

                if (r instanceof Lift) {
                    g.drawString("Schacht", tekenX + 4, tekenY + 16);
                } else {
                    g.drawString(r.getClass().getSimpleName(), tekenX + 4, tekenY + 16);
                }

                if (r instanceof Kamer) {
                    g.drawString(String.valueOf(((Kamer) r).getKamernummer()),
                            tekenX + 4, tekenY + 30);
                    g.drawString(((Kamer) r).getSterrenLabel(),
                            tekenX + 4, tekenY + 44);
                }

                if (r instanceof Lift) {
                    // cabine positie: verdieping 1 is onderaan de schacht (net boven de lobby)
                    int cabineY = (hotel.hoogte - hotel.lift.getHuidigeVerdieping() - 1) * tileSize + offsetY;

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
            int py;
            // personen op de lobby rij hardcoded onderaan tekenen
            if (p.huidigVakje.y == hotel.hoogte) {
                py = (hotel.hoogte - 1) * tileSize + tileSize / 4 + offsetY;
            } else {
                py = (hotel.hoogte - p.huidigVakje.y - 1) * tileSize + tileSize / 4 + offsetY;
            }

            if (p instanceof Gast) {
                int offset = (((Gast) p).gastId % 3) * 10;
                px += offset;
            }

            if (p instanceof Gast) {
                g.setColor(Color.WHITE);
                g.fillOval(px, py, tileSize / 3, tileSize / 3);

                g.setColor(Color.BLACK);
                g.drawOval(px, py, tileSize / 3, tileSize / 3);

                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString(String.valueOf(((Gast) p).gastId),
                        px + tileSize / 8, py + tileSize / 3);

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
        }
    }
}