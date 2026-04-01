package Controller;

import View.HotelFrame;

public class Main {
    public static void main(String[] args) {
        Simulatie sim = new Simulatie();
        new HotelFrame(sim.getHotel(), sim.getManager());
    }
}