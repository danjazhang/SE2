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

// View klasse: tekent het hotel grid op het scherm
// Implementeert ModelListener zodat het automatisch hertekent als het Model verandert
// Dit is het Observer pattern: Hotel notificeert HotelPanel via modelGewijzigd()
public class LayoutView extends JPanel implements ModelListener {

    // het hotel model waarvan de data gelezen wordt
    Hotel hotel;

    // de pixelgrootte van elk vakje in het grid
    static int tileSize = 64;

    // constructor: registreer dit panel als observer bij het hotel
    public LayoutView(Hotel hotel) {
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

        // als er geen layout is, toon een melding
        if (hotel.layout == null) {
            g.drawString("Geen layout geladen", 20, 20);
            return;
        }

        //bijhouden welke ruimtes al getekend zijn
        //hashset slaat unieke objecten op, hetzelfde object kan er maar 1 keer in zitten
        java.util.Set<Ruimte> getekend = new java.util.HashSet<>();

        // loop over elk vakje in het grid
        for (int x = 1; x <= hotel.breedte; x++) {
            for (int y = 1; y <= hotel.hoogte; y++) {
                Ruimte r = hotel.krijgRuimteOp(x, y);
                if (r == null) continue;
                //al getekend, sla over
                if (getekend.contains(r)) continue;
                //voeg ruimte toe aan hashset
                getekend.add(r);

                // kies kleur op basis van ruimtetype
                if (r instanceof Kamer) g.setColor(new Color(70, 130, 180));
                else if (r instanceof Restaurant) g.setColor(Color.ORANGE);
                else if (r instanceof Bioscoop) g.setColor(Color.RED);
                else if (r instanceof Fitnessruimte) g.setColor(Color.GREEN);
                else g.setColor(Color.LIGHT_GRAY);

                // teken het hele blok in één keer op basis van positie en afmetingen
                int tekenX = r.posX * tileSize;
                // verschuif alles 1 vakje naar rechts zodat de lift links past
                int tekenY = (r.posY - 1) * tileSize;
                int tekenB = r.breedte * tileSize;
                int tekenH = r.hoogte * tileSize;

                g.fillRect(tekenX, tekenY, tekenB, tekenH);
                g.setColor(Color.BLACK);
                //1 rand om het hele blok
                g.drawRect(tekenX, tekenY, tekenB, tekenH);

                // teken de naam van de ruimte op het vakje
                String naam = r.getClass().getSimpleName();
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(naam, tekenX + 4, tekenY + 16);

                //teken kamernummer als het een kamer is
                if (r instanceof Kamer){
                    //((Kamer) r) zet r van type ruimte naar kamer
                    //String.valueof zet int naar string
                    g.drawString(String.valueOf(((Kamer) r).getKamernummer()), tekenX + 4, tekenY + 30);
                    
                }
            }
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
        g.setColor(Color.YELLOW);
        g.fillRect(tileSize, lobbyY, hotel.breedte * tileSize, tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(tileSize, lobbyY, hotel.breedte * tileSize, tileSize);
        g.drawString("Lobby", tileSize + 4, lobbyY + 16);
    }
}
