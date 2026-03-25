import javax.swing.*;
import java.awt.*;

public class HotelPanel extends JPanel {
    Hotel hotel;

    //pixelgrootte per vakje
    static int tileSize = 64;

    public HotelPanel(Hotel hotel) {
        this.hotel = hotel;
        //repaint();
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
                else if (r instanceof Lobby) g.setColor(Color.YELLOW);
                else if (r != null && "Lift".equals(r.type)) g.setColor(Color.CYAN);
                else if (r != null && "Trap".equals(r.type)) g.setColor(Color.MAGENTA);

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

        // teken lift helemaal links, midden
        int liftY = (hotel.hoogte / 2 - 1) * tileSize;
        g.setColor(Color.CYAN);
        g.fillRect(0, liftY, tileSize, tileSize * 2);
        g.setColor(Color.BLACK);
        g.drawRect(0, liftY, tileSize, tileSize * 2);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Lift", 4, liftY + 16);

        // teken trap helemaal rechts, midden
        int trapX = (hotel.breedte + 1) * tileSize;
        g.setColor(Color.MAGENTA);
        g.fillRect(trapX, liftY, tileSize, tileSize * 2);
        g.setColor(Color.BLACK);
        g.drawRect(trapX, liftY, tileSize, tileSize * 2);
        g.drawString("Trap", trapX + 4, liftY + 16);
    }
}
