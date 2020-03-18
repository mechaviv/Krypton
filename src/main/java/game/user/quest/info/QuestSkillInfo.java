package game.user.quest.info;

/**
 * Created by MechAviv on 1/21/2020.
 */
public class QuestSkillInfo {
    private int skillID;
    private int level;
    private boolean acquire;

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public boolean getAcquire() {
        return acquire;
    }

    public void setAcquire(boolean acquire) {
        this.acquire = acquire;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
