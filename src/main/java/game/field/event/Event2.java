package game.field.event;

/**
 * Created by MechAviv on 3/19/2020.
 */
public class Event2 {
    private final EventManager2 main;
    private final int eventSN;
    private final long eventTime;

    public Event2(EventManager2 main, int eventSN, long eventTime) {
        this.main = main;
        this.eventSN = eventSN;
        this.eventTime = eventTime;
    }

    public EventManager2 getMain() {
        return main;
    }

    public int getEventSN() {
        return eventSN;
    }

    public long getEventTime() {
        return eventTime;
    }
}
