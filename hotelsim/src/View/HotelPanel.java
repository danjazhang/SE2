package View;

import Model.Hotel;
import Model.Ruimte;
import Model.Kamer;
import Model.Restaurant;
import Model.Bioscoop;
import Model.Fitnessruimte;
import Model.ModelListener;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

// Tekent het hotel op het scherm.
// De panel schaalt mee met het venster en tekent elke ruimte als één geheel,
// zodat kleuren, randen en kamernummers overzichtelijk blijven.
public class HotelPanel extends JPanel implements ModelListener {

    // het hotel model waarvan de data gelezen wordt
    Hotel hotel;

    // de pixelgrootte van elk vakje in het grid
    static int tileSize = 64;

    // constructor: registreer dit panel als observer bij het hotel
    public HotelPanel(Hotel hotel) {
        this.hotel = hotel;
        hotel.voegListenerToe(this);
    }

    // geef het hotel terug
    public Hotel getHotel() { return hotel; }

    // stel een nieuw hotel in en herteken het panel
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        hotel.voegListenerToe(this);
        repaint();
    }

    // wordt aangeroepen door Hotel als de layout veranderd is
    // repaint() zorgt dat paintComponent opnieuw aangeroepen wordt
    @Override
    public void modelGewijzigd() {
        repaint();
    }

    // teken het hotel grid op het scherm
    @Override
    protected void paintComponent(Graphics g) {
        // teken de achtergrond leeg, altijd eerst aanroepen
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // als er geen layout is, toon een melding
        if (hotel.layout == null) {
            g.drawString("Geen layout geladen", 20, 20);
            return;
        }

        tileSize = berekenTileSize();
        int hotelBreedtePixels = (hotel.breedte + 2) * tileSize;
        int hotelHoogtePixels = (hotel.hoogte + 1) * tileSize;
        int offsetX = Math.max(0, (getWidth() - hotelBreedtePixels) / 2);
        int offsetY = Math.max(0, (getHeight() - hotelHoogtePixels) / 2);

        g2.translate(offsetX, offsetY);

        Set<Ruimte> getekendeRuimtes = new HashSet<>();

        // verzamel elke ruimte slechts één keer zodat kleuren en randen niet overlappen
        for (int x = 1; x <= hotel.breedte; x++) {
            for (int y = 1; y <= hotel.hoogte; y++) {
                Ruimte r = hotel.krijgRuimteOp(x, y);
                if (r == null) continue;
                getekendeRuimtes.add(r);
            }
        }

        // teken daarna elke ruimte als één rechthoek
        for (Ruimte ruimte : getekendeRuimtes) {
            tekenRuimte(g2, ruimte);
        }

        // teken de lift helemaal links in cyaan
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, tileSize, (hotel.hoogte + 1) * tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, tileSize, (hotel.hoogte + 1) * tileSize);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Lift", 4, 16);

        // teken de trap helemaal rechts in magenta
        int trapX = (hotel.breedte + 1) * tileSize;
        g.setColor(Color.MAGENTA);
        g.fillRect(trapX, 0, tileSize, (hotel.hoogte + 1) * tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(trapX, 0, tileSize, (hotel.hoogte + 1) * tileSize);
        g.drawString("Trap", trapX + 4, 16);

        // teken de lobby onderin, even breed als het hotel
        int lobbyY = hotel.hoogte * tileSize;
        g.setColor(Color.GREEN);
        g.fillRect(tileSize, lobbyY, hotel.breedte * tileSize, tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(tileSize, lobbyY, hotel.breedte * tileSize, tileSize);
        g.drawString("Lobby", tileSize + 4, lobbyY + 16);

        g2.translate(-offsetX, -offsetY);
    }

    private int berekenTileSize() {
        int kolommen = hotel.breedte + 2;
        int rijen = hotel.hoogte + 1;
        int breedteOpBasisVanScherm = Math.max(1, getWidth() / Math.max(1, kolommen));
        int hoogteOpBasisVanScherm = Math.max(1, getHeight() / Math.max(1, rijen));
        return Math.max(32, Math.min(breedteOpBasisVanScherm, hoogteOpBasisVanScherm));
    }

    // Bepaalt de kleur per ruimtetype.
    private Color krijgRuimteKleur(Ruimte ruimte) {
        if (ruimte instanceof Fitnessruimte) {
            return new Color(255, 165, 0);
        }
        if (ruimte instanceof Restaurant) {
            return Color.YELLOW;
        }
        if (ruimte instanceof Kamer) {
            return new Color(70, 130, 180);
        }
        return Color.LIGHT_GRAY;
    }

    // tekent één ruimte volledig in één keer om overlappende vakjes te vermijden
    private void tekenRuimte(Graphics2D g2, Ruimte ruimte) {
        int startX = ruimte.getX() * tileSize;
        int startY = (ruimte.getY() - 1) * tileSize;
        int breedte = ruimte.getBreedte() * tileSize;
        int hoogte = ruimte.getHoogte() * tileSize;

        g2.setColor(krijgRuimteKleur(ruimte));
        g2.fillRect(startX, startY, breedte, hoogte);

        g2.setColor(Color.BLACK);
        g2.drawRect(startX, startY, breedte, hoogte);

        Shape oudeClip = g2.getClip();
        g2.setClip(startX + 1, startY + 1, Math.max(1, breedte - 2), Math.max(1, hoogte - 2));

        if (ruimte instanceof Kamer) {
            Kamer kamer = (Kamer) ruimte;
            if (hoogte <= tileSize) {
                tekenGecentreerdeTekst(g2, "K" + kamer.getKamerNummer(), startX, startY + 20, breedte);
            } else {
                tekenGecentreerdeTekst(g2, krijgRuimteLabel(ruimte), startX, startY + 20, breedte);
                tekenGecentreerdeTekst(g2, "K" + kamer.getKamerNummer(), startX, startY + 38, breedte);
            }
        } else {
            tekenGecentreerdeTekst(g2, krijgRuimteLabel(ruimte), startX, startY + 20, breedte);
        }

        g2.setClip(oudeClip);
    }

    // Geeft het label terug dat in de ruimte wordt getoond.
    private String krijgRuimteLabel(Ruimte ruimte) {
        if (ruimte instanceof Fitnessruimte) {
            return "Fitnessruimte";
        }
        if (ruimte instanceof Restaurant) {
            return "Restaurant";
        }
        return ruimte.getClass().getSimpleName();
    }

    // Zet tekst horizontaal in het midden van de ruimte.
    private void tekenGecentreerdeTekst(Graphics2D g2, String tekst, int x, int y, int breedte) {
        Font font = new Font("Arial", Font.BOLD, 12);
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics(font);
        int tekstX = x + Math.max(4, (breedte - metrics.stringWidth(tekst)) / 2);
        g2.drawString(tekst, tekstX, y);
    }

}
