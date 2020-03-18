package game.field.event;

/**
 * Created by MechAviv on 2/2/2020.
 */
public class Event {
    private final EventManager main;
    private final int eventSN;
    private final long eventTime;

    public Event(EventManager main, int eventSN, long eventTime) {
        this.main = main;
        this.eventSN = eventSN;
        this.eventTime = eventTime;
    }

    public EventManager getMain() {
        return main;
    }

    public int getEventSN() {
        return eventSN;
    }

    public long getEventTime() {
        return eventTime;
    }
}
