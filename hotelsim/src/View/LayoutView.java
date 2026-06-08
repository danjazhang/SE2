package View;

import Controller.SimulatieController;
import Model.*;
import Model.persoon.Gast;
import Model.persoon.Persoon;
import Model.persoon.Schoonmaker;
import Model.ruimte.*;
import java.awt.*;
import javax.swing.*;

/**
 * Tekent het hotel op het scherm.
 *
 * Coördinaten-systeem:
 *   Model y=1  = "buiten" — evacuatiebestemming, buiten het gebouw
 *   Model y=2  = lobby (onderaan het gebouw)
 *   Model y=3..hoogte = kamers en andere ruimtes (hoog y = bovenaan scherm)
 *
 * Flip-formule voor een ruimte met posY en hoogte h:
 *   tekenY = (hotel.hoogte - posY - h) * tileSize
 *
 * Voor een enkel vakje op y:
 *   schermY = (hotel.hoogte - y - 1) * tileSize
 *
 * Voorbeelden met hoogte=11:
 *   y=1  (buiten)         → (11-1-1)*64 = 576px  (onderaan, onder de lobby)
 *   y=2  (lobby)          → (11-2-1)*64 = 512px
 *   y=3  (laagste kamer)  → (11-3-1)*64 = 448px
 *   y=10 (bovenste kamer) → (11-10-1)*64 = 0px   (bovenaan) ✓
 */
public class LayoutView extends JPanel implements ModelListener {

    Hotel hotel;
    static int tileSize = 64;
    private static final int DREMPEL_BEZET = 3;

    private SimulatieController simulatieController;
    private Runnable onLobbyClick;

    public LayoutView(Hotel hotel) {
        this.hotel = hotel;

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (LayoutView.this.hotel == null || LayoutView.this.hotel.layout == null) return;
                int x = e.getX() / tileSize + 1;
                int y = LayoutView.this.hotel.hoogte - e.getY() / tileSize; // flip terug naar model-y
                Ruimte r = LayoutView.this.hotel.krijgRuimteOp(x, y);
                if (r instanceof Lobby && onLobbyClick != null) {
                    onLobbyClick.run();
                }
            }
        });
    }

    public void setOnLobbyClick(Runnable onLobbyClick) { this.onLobbyClick = onLobbyClick; }
    public void setSimulatieController(SimulatieController sc) { this.simulatieController = sc; }
    public Hotel getHotel() { return hotel; }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        if (hotel != null && hotel.breedte > 0 && hotel.hoogte > 0) {
            // hotel.hoogte bevat alle rijen inclusief buiten (y=1) en lobby (y=2)
            setPreferredSize(new Dimension(hotel.breedte * tileSize, hotel.hoogte * tileSize));
            revalidate();
        }
        repaint();
    }

    @Override
    public void modelGewijzigd() { repaint(); }

    /** Zet model-y van een vakje om naar scherm-y pixels. */
    private int schermY(int modelY, int offsetY) {
        return (hotel.hoogte - modelY - 1) * tileSize + offsetY;
    }

    /** Zet model-y van een ruimte (posY + hoogte) om naar tekenY (bovenkant op scherm). */
    private int ruimteTekenY(int posY, int hoogte, int offsetY) {
        return (hotel.hoogte - posY - hoogte) * tileSize + offsetY;
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
            g.drawString("BRANDALARM - EVACUEER DIRECT", 10, 26);
            offsetY += 40;
        }

        // tick teller en klok
        if (simulatieController != null) {
            g.setColor(new Color(40, 40, 40));
            g.fillRect(0, offsetY, getWidth(), 24);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 13));
            g.drawString("HTE: " + simulatieController.getTikTeller()
                    + "    Real Time: " + simulatieController.getRealTijd(), 10, offsetY + 17);
            offsetY += 24;
        }

        java.util.Set<Ruimte> getekend = new java.util.HashSet<>();

        for (int x = 1; x <= hotel.breedte; x++) {
            for (int y = 1; y <= hotel.hoogte; y++) {

                Ruimte r = hotel.krijgRuimteOp(x, y);
                if (r == null) continue;
                if (getekend.contains(r)) continue;
                getekend.add(r);

                // kleur per ruimtetype
                if (r instanceof Kamer) {
                    g.setColor(((Kamer) r).isBezet()
                            ? new Color(220, 80, 80) : new Color(222, 229, 240));
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
                int tekenY = ruimteTekenY(r.posY, r.hoogte, offsetY);
                int tekenB = r.breedte * tileSize;
                int tekenH = r.hoogte * tileSize;

                // lift en trap beslaan alleen de kamerrijen (y=3..hoogte), niet lobby (y=2) of buiten (y=1)
                if (r instanceof Lift || r instanceof Trap) {
                    tekenY = offsetY;
                    tekenH = (hotel.hoogte - 2) * tileSize; // hoogte - 2 rijen (zonder lobby en buiten)
                }

                g.fillRect(tekenX, tekenY, tekenB, tekenH);
                g.setColor(Color.BLACK);
                g.drawRect(tekenX, tekenY, tekenB, tekenH);

                // rood overlay als ruimte vol is
                if (r.getAanwezigen().size() >= DREMPEL_BEZET) {
                    ((Graphics2D) g).setColor(new Color(220, 50, 50, 80));
                    ((Graphics2D) g).fillRect(tekenX, tekenY, tekenB, tekenH);
                }

                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));

                if (r instanceof Lift) {
                    g.drawString("Schacht", tekenX + 4, tekenY + 16);
                } else {
                    g.drawString(r.getClass().getSimpleName(), tekenX + 4, tekenY + 16);
                }

                if (r instanceof Kamer) {
                    Kamer k = (Kamer) r;
                    g.drawString(String.valueOf(k.getKamernummer()), tekenX + 4, tekenY + 30);
                    g.drawString(k.getSterrenLabel(), tekenX + 4, tekenY + 44);
                }

                // teken lift-cabine op de huidige verdieping
                if (r instanceof Lift) {
                    int v = hotel.lift.getHuidigeVerdieping();
                    int cabineY = schermY(v, offsetY);
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

            int grootte = tileSize / 3;
            int px;
            int py;

            Ruimte r2 = p.huidigVakje.ruimte;
            // een schoonmaker die actief schoonmaakt is ook "op zijn doel" (geen beweging)
            boolean schoonmakerBezig = (p instanceof Schoonmaker)
                    && ((Schoonmaker) p).bezig
                    && p.huidigVakje.ruimte == ((Schoonmaker) p).kamer;
            boolean heeftDoel = p.doelVakje != null && !schoonmakerBezig;
            // centreer alleen als de persoon zijn einddoel bereikt heeft én in een echte ruimte staat
            // (niet lift/trap/lobby en niet buiten het gebouw)
            boolean centreer = !heeftDoel
                    && r2 != null
                    && !(r2 instanceof Lift)
                    && !(r2 instanceof Trap)
                    && !(r2 instanceof Lobby)
                    && p.huidigVakje.y > hotel.lobby.posY; // niet op of onder de lobby

            if (centreer) {
                // persoon in kamer/restaurant/etc: midden van de ruimte
                int midX = (r2.posX - 1) * tileSize + (r2.breedte * tileSize) / 2;
                int midY = ruimteTekenY(r2.posY, r2.hoogte, offsetY) + (r2.hoogte * tileSize) / 2;
                px = midX - grootte / 2;
                py = midY - grootte / 2;
            } else {
                // onderweg of in lift/trap/lobby: teken onderaan het vakje
                px = (p.huidigVakje.x - 1) * tileSize + tileSize / 4;
                py = schermY(p.huidigVakje.y, offsetY) + tileSize - grootte - 2;
            }

            if (p instanceof Gast) {
                px += (((Gast) p).gastId % 3) * 10;
            }

            if (p instanceof Gast) {
                Gast gast = (Gast) p;

                // summoning animatie: straal van boven + gast vliegt omhoog
                if (gast.summonTick >= 0) {
                    float progress = (float) gast.summonTick / 8f; // 0.0 → 1.0 in 8 ticks

                    // basisposities
                    int basisPx = (p.huidigVakje.x - 1) * tileSize + tileSize / 2;
                    int basisPy = schermY(p.huidigVakje.y, offsetY) + tileSize / 2;

                    // gast vliegt omhoog: py daalt proportioneel met progress
                    int vliegPy = (int) (basisPy - progress * (basisPy + tileSize));
                    int vliegGrootte = (int) (grootte * (1f - progress * 0.8f));

                    // tractor beam: breed van boven smal toelopend naar de gast
                    Graphics2D g2d = (Graphics2D) g;
                    // pulserende breedte op basis van tick
                    int puls = (int) (8 + 6 * Math.sin(gast.summonTick * 0.8));
                    int straalBovBreedte = tileSize + puls;
                    int straalOnderBreedte = Math.max(4, grootte - (int)(progress * grootte * 0.5f));

                    // straal loopt van y=0 (top scherm) tot aan de gast
                    int[] xPunten = {
                        basisPx - straalBovBreedte / 2,
                        basisPx + straalBovBreedte / 2,
                        basisPx + straalOnderBreedte / 2,
                        basisPx - straalOnderBreedte / 2
                    };
                    int[] yPunten = {
                        offsetY,
                        offsetY,
                        vliegPy + vliegGrootte / 2,
                        vliegPy + vliegGrootte / 2
                    };

                    // semi-transparante groene straal met pulserende alpha
                    int alpha = (int) (60 + 80 * Math.abs(Math.sin(gast.summonTick * 0.6)));
                    g2d.setColor(new Color(50, 255, 120, Math.min(255, alpha)));
                    g2d.fillPolygon(xPunten, yPunten, 4);

                    // lichtere kern in het midden
                    int kernBreedte = straalBovBreedte / 3;
                    int[] xKern = {
                        basisPx - kernBreedte / 2,
                        basisPx + kernBreedte / 2,
                        basisPx + straalOnderBreedte / 4,
                        basisPx - straalOnderBreedte / 4
                    };
                    g2d.setColor(new Color(180, 255, 200, Math.min(255, alpha + 60)));
                    g2d.fillPolygon(xKern, yPunten, 4);

                    // teken de gast zelf (vliegt omhoog en wordt kleiner)
                    if (vliegGrootte > 2) {
                        g.setColor(Color.WHITE);
                        g.fillOval(basisPx - vliegGrootte / 2, vliegPy - vliegGrootte / 2,
                                vliegGrootte, vliegGrootte);
                        g.setColor(new Color(50, 255, 120));
                        g.drawOval(basisPx - vliegGrootte / 2, vliegPy - vliegGrootte / 2,
                                vliegGrootte, vliegGrootte);
                    }
                    continue; // geen normale tekening
                }

                g.setColor(Color.WHITE);
                g.fillOval(px, py, grootte, grootte);
                g.setColor(Color.BLACK);
                g.drawOval(px, py, grootte, grootte);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString(String.valueOf(gast.gastId), px + grootte / 4, py + grootte);

            } else if (p instanceof Schoonmaker) {
                g.setColor(new Color(232, 145, 68));
                g.fillRoundRect(px, py, grootte, grootte, 10, 10);
                g.setColor(Color.BLACK);
                g.drawRoundRect(px, py, grootte, grootte, 10, 10);
                g.setFont(new Font("Arial", Font.BOLD, 11));
                g.drawString("S", px + 7, py + 14);

            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillOval(px, py, grootte, grootte);
                g.setColor(Color.BLACK);
                g.drawOval(px, py, grootte, grootte);
            }
        }
    }
}
