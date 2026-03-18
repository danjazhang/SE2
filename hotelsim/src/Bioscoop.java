import java.util.List;
import java.util.ArrayList;

public class Bioscoop extends Ruimte {
    boolean filmBezig;
    int filmDuur;
    //arraylist gasten;
    List<Gast> gasten;

    //constructor
    public Bioscoop(){
        this.gasten = new ArrayList<>();
        this.filmBezig = false;
        this.filmDuur = 0;
    }

    public void startFilm(){}
    public void stopFilm(){}
    public void betreedBioscoop(){}

}
