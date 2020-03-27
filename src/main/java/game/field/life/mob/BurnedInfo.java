package game.field.life.mob;

/**
 * Created by MechAviv on 3/27/2020.
 */
public class BurnedInfo {
    private int characterID;
    private int skillID;
    private int damage;
    private int interval;
    private int end;
    private long lastUpdate;
    private int attrRate;
    private int dotCount;

    public int getCharacterID() {
        return characterID;
    }

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getAttrRate() {
        return attrRate;
    }

    public void setAttrRate(int attrRate) {
        this.attrRate = attrRate;
    }

    public int getDotCount() {
        return dotCount;
    }

    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }
}
