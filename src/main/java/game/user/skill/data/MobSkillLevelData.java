package game.user.skill.data;

import util.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by MechAviv on 1/30/2020.
 */
public class MobSkillLevelData {
    private int hpBelow, conMP, inerval, duration, prop, x, y;
    private Rect affectedArea;
    private int effect, limit, targetUserCount;
    private boolean targetUserRandom;
    private int direction, elemAttr;
    private List<Integer> templateIDs;

    public MobSkillLevelData() {
        affectedArea = new Rect();
        templateIDs = new ArrayList<>();
    }

    public int getHpBelow() {
        return hpBelow;
    }

    public void setHpBelow(int hpBelow) {
        this.hpBelow = hpBelow;
    }

    public int getConMP() {
        return conMP;
    }

    public void setConMP(int conMP) {
        this.conMP = conMP;
    }

    public int getInerval() {
        return inerval;
    }

    public void setInerval(int inerval) {
        this.inerval = inerval;
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

    public Rect getAffectedArea() {
        return affectedArea;
    }

    public void setAffectedArea(Rect affectedArea) {
        this.affectedArea = affectedArea;
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTargetUserCount() {
        return targetUserCount;
    }

    public void setTargetUserCount(int targetUserCount) {
        this.targetUserCount = targetUserCount;
    }

    public boolean isTargetUserRandom() {
        return targetUserRandom;
    }

    public void setTargetUserRandom(boolean targetUserRandom) {
        this.targetUserRandom = targetUserRandom;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getElemAttr() {
        return elemAttr;
    }

    public void setElemAttr(int elemAttr) {
        this.elemAttr = elemAttr;
    }

    public List<Integer> getTemplateIDs() {
        return templateIDs;
    }

    public void setTemplateIDs(List<Integer> templateIDs) {
        this.templateIDs = templateIDs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobSkillLevelData that = (MobSkillLevelData) o;
        return hpBelow == that.hpBelow &&
                conMP == that.conMP &&
                inerval == that.inerval &&
                duration == that.duration &&
                prop == that.prop &&
                x == that.x &&
                y == that.y &&
                effect == that.effect &&
                limit == that.limit &&
                targetUserCount == that.targetUserCount &&
                targetUserRandom == that.targetUserRandom &&
                direction == that.direction &&
                elemAttr == that.elemAttr &&
                Objects.equals(affectedArea, that.affectedArea) &&
                Objects.equals(templateIDs, that.templateIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hpBelow, conMP, inerval, duration, prop, x, y, affectedArea, effect, limit, targetUserCount, targetUserRandom, direction, elemAttr, templateIDs);
    }
}
