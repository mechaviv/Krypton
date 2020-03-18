package game.user.skill;

import game.user.skill.entries.SkillEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MechAviv on 1/30/2020.
 */
public class SkillRoot {
    private int skillRootID;
    private String bookName;
    private List<SkillEntry> skills;

    public SkillRoot() {
        skills = new ArrayList<>();
    }

    public int getSkillRootID() {
        return skillRootID;
    }

    public void setSkillRootID(int skillRootID) {
        this.skillRootID = skillRootID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public List<SkillEntry> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillEntry> skills) {
        this.skills = skills;
    }
}
