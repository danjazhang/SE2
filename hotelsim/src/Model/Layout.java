package Model;

public class Layout {
    public int id;
    public String naam;
    public int breedte;
    public int hoogte;
    public Vakje[][] vakjes;

    public Layout(int breedte, int hoogte) {
        this.breedte = breedte;
        this.hoogte = hoogte;
        this.vakjes = new Vakje[breedte][hoogte];
        for (int x = 0; x < breedte; x++) {
            for (int y = 0; y < hoogte; y++) {
                vakjes[x][y] = new Vakje();
                vakjes[x][y].x = x + 1;
                vakjes[x][y].y = y + 1;
            }
        }
    }

    public void plaatsRuimte(Ruimte ruimte) {
        for (int x = ruimte.posX; x < ruimte.posX + ruimte.breedte; x++) {
            for (int y = ruimte.posY; y < ruimte.posY + ruimte.hoogte; y++) {
                if (x <= breedte && y <= hoogte) {
                    vakjes[x - 1][y - 1].ruimte = ruimte;
                }
            }
        }
    }

    public Vakje krijgVakje(int x, int y) {
        if (x >= 1 && x <= breedte && y >= 1 && y <= hoogte) {
            return vakjes[x - 1][y - 1];
        }
        return null;
    }
}
