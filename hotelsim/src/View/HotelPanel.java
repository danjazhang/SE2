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

    //pixelgrootte per vakje
    static int tileSize = 64;

    public HotelPanel(Hotel hotel) {
        this.hotel = hotel;
        hotel.voegListenerToe(this);
    }

    //vervangt huidige hotel met nieuwe hotel
    public void setHotel(Hotel hotel) {
    this.hotel = hotel;
    repaint();

    }

    //geeft huidige hotel terug om te controleren of er een geldige hotel is geladen
    public Hotel getHotel() {
        return hotel;
    }


    @Override
    public void modelGewijzigd() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g){
        //tekent de achtergrond leeg, altijd eerst aanroepen
        super.paintComponent(g);
        if (hotel.layout == null) {
            g.drawString("Geen layout geladen", 20, 20);
            return;
        }


        //loop over elk vakje in het grid
        for(int x = 1; x <= hotel.breedte; x++){
            for (int y = 1; y <= hotel.hoogte; y++){
                Ruimte r = hotel.krijgRuimteOp(x, y);
                if (r == null) continue;

                //kies kleur op basis van ruimtetype
                if (r instanceof Kamer) g.setColor(new Color(70, 130, 180)); //rgb kleur
                else if (r instanceof Restaurant) g.setColor(Color.ORANGE);
                else if (r instanceof Bioscoop) g.setColor(Color.RED);
                else if (r instanceof Fitnesruimte) g.setColor(Color.GREEN);
                else g.setColor(Color.LIGHT_GRAY);

                // verschuif alles 1 vakje naar rechts voor de lift
                g.fillRect(x * tileSize, (y-1)*tileSize, tileSize, tileSize);

                //teken zwarte rand eromheen
                g.setColor(Color.BLACK);
                g.drawRect(x * tileSize, (y-1)*tileSize, tileSize, tileSize);

                //naam tekenen
                String naam;
                if (r != null){
                    naam = r.getClass().getSimpleName();
                }else{
                    naam = "";
                }
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD,12));
                g.drawString(naam, x * tileSize + 4, (y-1) * tileSize + 16);
            }
        }

        // teken lift helemaal links
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, tileSize, (hotel.hoogte+1) * tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, tileSize, (hotel.hoogte + 1) *tileSize);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Lift", 4, 16);

        // teken trap helemaal rechts
        int trapX = (hotel.breedte + 1) * tileSize;
        g.setColor(Color.MAGENTA);
        g.fillRect(trapX, 0, tileSize, (hotel.hoogte+1) * tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(trapX, 0, tileSize, (hotel.hoogte+1) * tileSize);
        g.drawString("Trap", trapX + 4, 16);

        // teken lobby onderin, even breed als hotel
        int lobbyY = hotel.hoogte * tileSize;
        g.setColor(Color.YELLOW);
        g.fillRect(tileSize, lobbyY, hotel.breedte * tileSize, tileSize);
        g.setColor(Color.BLACK);
        g.drawRect(tileSize, lobbyY, hotel.breedte * tileSize, tileSize);
        g.drawString("Lobby", tileSize + 4, lobbyY + 16);

    }
}
