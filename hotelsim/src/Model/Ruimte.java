package Model;

import java.util.ArrayList;
import java.util.List;

public class Ruimte {

    public int posX;
    public int posY;
    public int breedte;
    public int hoogte;
    public String type = "";

    private int ingangX;
    private int ingangY;

    private List<Persoon> aanwezigen;

    public Ruimte(int posX, int posY, int breedte, int hoogte) {
        this.posX = posX;
        this.posY = posY;
        this.breedte = breedte;
        this.hoogte = hoogte;
        this.aanwezigen = new ArrayList<>();
    }

    public Ruimte() {}

    public void setPositie(int x, int y) { this.posX = x; this.posY = y; }
    public void setAfmetingen(int b, int h) { this.breedte = b; this.hoogte = h; }
    public int getX() { return posX; }
    public int getY() { return posY; }
    public int getBreedte() { return breedte; }
    public int getHoogte() { return hoogte; }
    public void betreed(Persoon p) { aanwezigen.add(p); }
    public void verlaat(Persoon p) { aanwezigen.remove(p); }
    public void setIngang(int x, int y) { this.ingangX = x; this.ingangY = y; }
    public int[] krijgIngang() { return new int[]{ingangX, ingangY}; }
}
