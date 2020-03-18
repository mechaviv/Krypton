package game.user.quest.info.act;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MechAviv on 1/21/2020.
 */
public class ActSkill {
    private int skillID;
    private int skillLevel;
    private int masterLevel;
    private boolean onlyMasterLevel;
    private final List<Integer> jobs;

    public ActSkill() {
        this.jobs = new ArrayList<>();
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public void setMasterLevel(int masterLevel) {
        this.masterLevel = masterLevel;
    }

    public boolean isOnlyMasterLevel() {
        return onlyMasterLevel;
    }

    public void setOnlyMasterLevel(boolean onlyMasterLevel) {
        this.onlyMasterLevel = onlyMasterLevel;
    }

    public List<Integer> getJobs() {
        return jobs;
    }
}
