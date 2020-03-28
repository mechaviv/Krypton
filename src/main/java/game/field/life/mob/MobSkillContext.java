package game.field.life.mob;

/**
 * Created by MechAviv on 3/28/2020.
 */
public class MobSkillContext {
    private int skillID;
    private int slv;
    private long lastSkillUse;
    private int summoned;

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public int getSlv() {
        return slv;
    }

    public void setSlv(int slv) {
        this.slv = slv;
    }

    public long getLastSkillUse() {
        return lastSkillUse;
    }

    public void setLastSkillUse(long lastSkillUse) {
        this.lastSkillUse = lastSkillUse;
    }

    public int getSummoned() {
        return summoned;
    }

    public void setSummoned(int summoned) {
        this.summoned = summoned;
    }
}
