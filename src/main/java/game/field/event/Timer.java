package game.field.event;

/**
 * Created by MechAviv on 2/2/2020.
 */
public class Timer {
    private int flag;
    private long start;

    public boolean isWaiting(long cur) {
        if (cur - start > flag << 10) {
            flag &= 0x7FFFFFFF;
        }
        return (flag >> 31) != 0;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }
}
