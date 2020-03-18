package game.user.skill.entries;

import java.util.Objects;

/**
 * Created by MechAviv on 1/30/2020.
 */
public class BFSkillEntry {
    private int mobSkillID;
    private int slv;
    private int userCount;
    private boolean allMap;
    private String desc;
    private int ownerTeam;
    private String effectPath;

    public int getMobSkillID() {
        return mobSkillID;
    }

    public void setMobSkillID(int mobSkillID) {
        this.mobSkillID = mobSkillID;
    }

    public int getSlv() {
        return slv;
    }

    public void setSlv(int slv) {
        this.slv = slv;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public boolean isAllMap() {
        return allMap;
    }

    public void setAllMap(boolean allMap) {
        this.allMap = allMap;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getOwnerTeam() {
        return ownerTeam;
    }

    public void setOwnerTeam(int ownerTeam) {
        this.ownerTeam = ownerTeam;
    }

    public String getEffectPath() {
        return effectPath;
    }

    public void setEffectPath(String effectPath) {
        this.effectPath = effectPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BFSkillEntry that = (BFSkillEntry) o;
        return mobSkillID == that.mobSkillID &&
                slv == that.slv &&
                userCount == that.userCount &&
                allMap == that.allMap &&
                ownerTeam == that.ownerTeam &&
                Objects.equals(desc, that.desc) &&
                Objects.equals(effectPath, that.effectPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mobSkillID, slv, userCount, allMap, desc, ownerTeam, effectPath);
    }
}
