package Model;

public class HotelEvent {
    private HotelEventType eventType;
    private int time;
    private int guestId;

    public HotelEvent(HotelEventType eventType, int time, int guestId) {
        this.eventType = eventType;
        this.time = time;
        this.guestId = guestId;
    }

    public HotelEventType getEventType() { return eventType; }
    public int getTime() { return time; }
    public int getGuestId() { return guestId; }
}
