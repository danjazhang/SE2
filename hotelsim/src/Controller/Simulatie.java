/* DEZE KLASSE HEBBEN WIJ NIET NODIG en komt in conflict met andere hotel klasses
package Controller;


import Model.Hotel;
import Model.ModelListener;

import hotelevents.HotelEvent;
import hotelevents.HotelEventListener;
import hotelevents.HotelEventManager;
import hotelevents.HotelEventType;

public class Simulatie implements ModelListener, HotelEventListener {

    private Hotel hotel;
    private HotelEventManager manager;

    public Simulatie(Hotel hotel, HotelEventManager manager) {
        this.hotel = hotel;
        this.manager = manager;

        hotel.voegListenerToe(this);

        // ✅ juiste methode!
        manager.register(this);
    }


    @Override
    public void modelGewijzigd() {
        System.out.println("Model gewijzigd");
    }

    // 🔥 HIER komen alle events binnen vanuit de library
    @Override
    public void notify(HotelEvent evt) {

        System.out.println("Event: " + evt.getEventType() + " tijd=" + evt.getTime());

        switch (evt.getEventType()) {

            case CHECK_IN:
                System.out.println("Gast ingecheckt: " + evt.getGuestId());
                break;

            case CHECK_OUT:
                System.out.println("Gast uitgecheckt: " + evt.getGuestId());
                break;

            case NEED_FOOD:
                System.out.println("Gast wil eten");
                break;

            case GOTO_FITNESS:
                System.out.println("Gast gaat fitness");
                break;

            case GOTO_CINEMA:
                System.out.println("Gast gaat bioscoop");
                break;

            case EVACUATE:
                System.out.println("🚨 EVACUATIE!");
                break;

            case GODZILLA:
                System.out.println("🦖 GODZILLA!");
                break;

            default:
                break;
        }
    }


} */