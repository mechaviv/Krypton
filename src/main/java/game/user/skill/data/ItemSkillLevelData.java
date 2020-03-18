package game.user.skill.data;

import java.util.Objects;

/**
 * Created by MechAviv on 1/30/2020.
 */
public class ItemSkillLevelData {
    private int conMP;
    private int interval;
    private int duration;
    private int prop;
    private int x;
    private int y;
    // string sMobVol

    public int getConMP() {
        return conMP;
    }

    public void setConMP(int conMP) {
        this.conMP = conMP;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getProp() {
        return prop;
    }

    public void setProp(int prop) {
        this.prop = prop;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSkillLevelData that = (ItemSkillLevelData) o;
        return conMP == that.conMP &&
                interval == that.interval &&
                duration == that.duration &&
                prop == that.prop &&
                x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(conMP, interval, duration, prop, x, y);
    }
}
