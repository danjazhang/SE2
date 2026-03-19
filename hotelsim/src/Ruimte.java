import java.util.ArrayList;
import java.util.List;

public class Ruimte {

    public int posX;
    public int posY;
    public int breedte;
    public int hoogte;

    private int ingangX;
    private int ingangY;

    private List<Persoon> aanwezigen;

    // constructor
    public Ruimte(int posX, int posY, int breedte, int hoogte) {
        this.posX = posX;
        this.posY = posY;
        this.breedte = breedte;
        this.hoogte = hoogte;
        this.aanwezigen = new ArrayList<>();
    }

    public Ruimte(){

        //todo: nog implementeren
    }

    public void setPositie(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public void setAfmetingen(int b, int h) {
        this.breedte = b;
        this.hoogte = h;
    }

    // getters
    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public int getBreedte() {
        return breedte;
    }

    public int getHoogte() {
        return hoogte;
    }

    // persoon betreedt ruimte
    public void betreed(Persoon p) {
        aanwezigen.add(p);
    }

    // persoon verlaat ruimte
    public void verlaat(Persoon p) {
        aanwezigen.remove(p);
    }

    // ingang instellen
    public void setIngang(int x, int y) {
        this.ingangX = x;
        this.ingangY = y;
    }

    // ingang ophalen
    public int[] krijgIngang() {
        return new int[]{ingangX, ingangY};
    }
}