package game.user.skill.entries;

import game.user.skill.data.MobSkillLevelData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MechAviv on 1/30/2020.
 */
public class MobSkillEntry {
    private int skillID;
    private final List<MobSkillLevelData> levelData;

    public MobSkillEntry(int skillID) {
        this.skillID = skillID;
        this.levelData = new ArrayList<>();
    }
    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public List<MobSkillLevelData> getLevelData() {
        return levelData;
    }
}
