import javax.swing.*;

public class HotelFrame extends JFrame {
    public HotelFrame(Hotel hotel) {
        //titel
        setTitle("Hotel Simulatie");

        //venstergrootte gebaseerd op grid grootte
        setSize((hotel.breedte + 2) * HotelPanel.tileSize, hotel.hoogte * HotelPanel.tileSize);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //voeg tekenpaneel toe
        add(new HotelPanel(hotel));
        setVisible(true);
    }
}
