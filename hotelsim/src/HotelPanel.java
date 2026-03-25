import javax.swing.*;
import java.awt.*;

public class HotelPanel extends JPanel {
    Hotel hotel;

    //pixelgrootte per vakje
    static int tileSize = 64;

    public HotelPanel(Hotel hotel) {
        this.hotel = hotel;
        revalidate();
        repaint();
    }
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        repaint();
    }
    public Hotel getHotel() {
        return hotel;
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
                if (r instanceof Kamer) g.setColor(Color.BLUE);
                else if (r instanceof Restaurant) g.setColor(Color.ORANGE);
                else if (r instanceof Bioscoop) g.setColor(Color.RED);
                else if (r instanceof Fitnesruimte) g.setColor(Color.GREEN);
                else g.setColor(Color.LIGHT_GRAY);

                //teken gevulde rechthoek op de juiste pixelpositie
                g.fillRect((x-1)* tileSize, (y-1)*tileSize, tileSize, tileSize);

                //teken zwarte rand eromheen
                g.setColor(Color.BLACK);
                g.drawRect((x-1)* tileSize, (y-1)*tileSize, tileSize, tileSize);


            }
        }
    }
}
