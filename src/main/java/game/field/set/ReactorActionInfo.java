package game.field.set;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MechAviv on 2/2/2020.
 */
public class ReactorActionInfo {
    private int fieldIDx;
    private int type;
    private final List<ReactorInfo> reactorInfos;
    private final List<String> args;

    public ReactorActionInfo() {
        this.reactorInfos = new ArrayList<>();
        this.args = new ArrayList<>();
    }

    public int getFieldIDx() {
        return fieldIDx;
    }

    public void setFieldIDx(int fieldIDx) {
        this.fieldIDx = fieldIDx;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<ReactorInfo> getReactorInfos() {
        return reactorInfos;
    }

    public List<String> getArgs() {
        return args;
    }
}
