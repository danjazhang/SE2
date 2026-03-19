public class HotelEvent {

    private int time;
    private HotelEventType eventType;
    private int guestId;
    private int data;

    // constructor
    public HotelEvent(int time, HotelEventType eventType, int guestId, int data) {
        this.time = time;
        this.eventType = eventType;
        this.guestId = guestId;
        this.data = data;
    }

    // getters
    public HotelEventType getEventType() {
        return eventType;
    }

    public int getGuestId() {
        return guestId;
    }

    public int getTime() {
        return time;
    }

    public int getData() {
        return data;
    }
}