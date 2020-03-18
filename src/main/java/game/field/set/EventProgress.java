package game.field.set;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MechAviv on 2/2/2020.
 */
public class EventProgress {
    private int time;
    private int actionOnField;
    private final List<String> args;

    public EventProgress() {
        args = new ArrayList<>();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getActionOnField() {
        return actionOnField;
    }

    public void setActionOnField(int actionOnField) {
        this.actionOnField = actionOnField;
    }

    public List<String> getArgs() {
        return args;
    }
}
