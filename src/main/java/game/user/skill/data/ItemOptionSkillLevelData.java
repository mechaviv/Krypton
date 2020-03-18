package game.user.skill.data;

import java.util.Objects;

/**
 * Created by MechAviv on 1/30/2020.
 */
public class ItemOptionSkillLevelData {
    private int prob;
    private int missProb;
    private int duration;
    private int damage;
    private int speed;
    private int pos;
    private int repeat;
    // string sMobVol

    public int getProb() {
        return prob;
    }

    public void setProb(int prob) {
        this.prob = prob;
    }

    public int getMissProb() {
        return missProb;
    }

    public void setMissProb(int missProb) {
        this.missProb = missProb;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemOptionSkillLevelData that = (ItemOptionSkillLevelData) o;
        return prob == that.prob &&
                missProb == that.missProb &&
                duration == that.duration &&
                damage == that.damage &&
                speed == that.speed &&
                pos == that.pos &&
                repeat == that.repeat;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prob, missProb, duration, damage, speed, pos, repeat);
    }
}
