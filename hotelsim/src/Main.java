public class Main {
    public static void main(String[] args) {
        Hotel hotel = new Hotel();

        // pas het pad aan naar waar jouw JSON file staat
        hotel.laadLayoutBestand("layout.json");

        // test: print alle ruimtes
        for (Ruimte r : hotel.ruimtes) {
            System.out.println(r.getClass().getSimpleName() +
                " op positie (" + r.posX + ", " + r.posY + ")" +
                " dimensie " + r.breedte + "x" + r.hoogte);
        }
    }
}
