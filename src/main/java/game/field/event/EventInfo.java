package game.field.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MechAviv on 2/2/2020.
 */
public class EventInfo {
    private int eventSN;
    private final List<Integer> args;

    public EventInfo() {
        this.args = new ArrayList<>();
    }

    public List<Integer> getArgs() {
        return args;
    }

    public int getEventSN() {
        return eventSN;
    }

    public void setEventSN(int eventSN) {
        this.eventSN = eventSN;
    }
}
