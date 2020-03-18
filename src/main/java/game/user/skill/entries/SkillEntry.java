package game.user.skill.entries;

import game.user.skill.SkillRecord;
import game.user.skill.SkillType;
import game.user.skill.data.AdditionPsd;
import game.user.skill.data.SkillLevelData;
import game.user.skill.data.SkillLevelDataCommon;
import util.Logger;
import util.wz.WzProperty;

import java.util.*;

/**
 * Created by MechAviv on 1/30/2020.
 */
public class SkillEntry {
    private int skillID;
    private String name, description;
    private int skillType, psdSkill, attackElemAttr, weapon, subWeapon;
    private List<Integer> actions;
    private int specialAction, prepareAction, prepare, ballDelay;
    private boolean invisible, upButtonDisabled;
    private int defaultMasterLev;
    private boolean combatOrders;
    private int crc;
    private boolean timeLimited;
    private int mobCode;// probably crc
    private int delayFrame, holdFram;
    private List<List<Integer>> finalAttacks;
    private List<SkillRecord> reqSkills;
    private int maxLevel;
    private SkillLevelDataCommon common;
    private Map<Integer, AdditionPsd> additionPsdOffset;
    private SkillLevelData[] levelData;
    private WzProperty levelDataProp;

    public SkillEntry() {
        actions = new ArrayList<>();
        finalAttacks = new ArrayList<>();
        reqSkills = new ArrayList<>();
        additionPsdOffset = new HashMap<>();
        levelData = null;
        levelDataProp = null;
    }

    // todo: adjust damage dec rate
    // todo: isCorrectWeaponType
    public boolean isFinalAttack() {
        return skillType == SkillType.FINAL_ATTACK;
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSkillType() {
        return skillType;
    }

    public void setSkillType(int skillType) {
        this.skillType = skillType;
    }

    public int getPsdSkill() {
        return psdSkill;
    }

    public void setPsdSkill(int psdSkill) {
        this.psdSkill = psdSkill;
    }

    public int getAttackElemAttr() {
        return attackElemAttr;
    }

    public void setAttackElemAttr(int attackElemAttr) {
        this.attackElemAttr = attackElemAttr;
    }

    public int getWeapon() {
        return weapon;
    }

    public void setWeapon(int weapon) {
        this.weapon = weapon;
    }

    public int getSubWeapon() {
        return subWeapon;
    }

    public void setSubWeapon(int subWeapon) {
        this.subWeapon = subWeapon;
    }

    public List<Integer> getActions() {
        return actions;
    }

    public void setActions(List<Integer> actions) {
        this.actions = actions;
    }

    public int getSpecialAction() {
        return specialAction;
    }

    public void setSpecialAction(int specialAction) {
        this.specialAction = specialAction;
    }

    public int getPrepareAction() {
        return prepareAction;
    }

    public void setPrepareAction(int prepareAction) {
        this.prepareAction = prepareAction;
    }

    public int getPrepare() {
        return prepare;
    }

    public void setPrepare(int prepare) {
        this.prepare = prepare;
    }

    public int getBallDelay() {
        return ballDelay;
    }

    public void setBallDelay(int ballDelay) {
        this.ballDelay = ballDelay;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isUpButtonDisabled() {
        return upButtonDisabled;
    }

    public void setUpButtonDisabled(boolean upButtonDisabled) {
        this.upButtonDisabled = upButtonDisabled;
    }

    public int getDefaultMasterLev() {
        return defaultMasterLev;
    }

    public void setDefaultMasterLev(int defaultMasterLev) {
        this.defaultMasterLev = defaultMasterLev;
    }

    public boolean isCombatOrders() {
        return combatOrders;
    }

    public void setCombatOrders(boolean combatOrders) {
        this.combatOrders = combatOrders;
    }

    public int getCrc() {
        return crc;
    }

    public void setCrc(int crc) {
        this.crc = crc;
    }

    public boolean isTimeLimited() {
        return timeLimited;
    }

    public void setTimeLimited(boolean timeLimited) {
        this.timeLimited = timeLimited;
    }

    public int getMobCode() {
        return mobCode;
    }

    public void setMobCode(int mobCode) {
        this.mobCode = mobCode;
    }

    public int getDelayFrame() {
        return delayFrame;
    }

    public void setDelayFrame(int delayFrame) {
        this.delayFrame = delayFrame;
    }

    public int getHoldFram() {
        return holdFram;
    }

    public void setHoldFram(int holdFram) {
        this.holdFram = holdFram;
    }

    public List<List<Integer>> getFinalAttacks() {
        return finalAttacks;
    }

    public void setFinalAttacks(List<List<Integer>> finalAttacks) {
        this.finalAttacks = finalAttacks;
    }

    public List<SkillRecord> getReqSkills() {
        return reqSkills;
    }

    public void setReqSkills(List<SkillRecord> reqSkills) {
        this.reqSkills = reqSkills;
    }

    public int getMaxLevel() {
        if (levelDataProp == null) {
            return maxLevel <= 0 ? 0 : maxLevel;
        }
        if (maxLevel <= 0) {
            return levelDataProp.getChildNodes().size();
        }
        if (maxLevel > levelDataProp.getChildNodes().size()) {
            return levelDataProp.getChildNodes().size();
        }
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public SkillLevelDataCommon getCommon() {
        return common;
    }

    public void setCommon(SkillLevelDataCommon common) {
        this.common = common;
    }

    public Map<Integer, AdditionPsd> getAdditionPsdOffset() {
        return additionPsdOffset;
    }

    public void setAdditionPsdOffset(Map<Integer, AdditionPsd> additionPsdOffset) {
        this.additionPsdOffset = additionPsdOffset;
    }

    // only common works for now
    public SkillLevelData getLevelData(int level) {
        if (levelDataProp != null && levelData == null) {
            levelData = new SkillLevelData[levelDataProp.getChildNodes().size()];
            for (int i = 0; i < levelData.length; i++) {
                SkillLevelData sd = new SkillLevelData();
                levelData[i] = sd;
            }
        } else {
            int maxLevel = getMaxLevel();
            if (isCombatOrders()) {
                maxLevel += 2;
            }
            if (maxLevel > 0 && levelData == null) {
                levelData = new SkillLevelData[maxLevel];
                for (int i = 0; i < levelData.length; i++) {
                    SkillLevelData sd = new SkillLevelData();
                    levelData[i] = sd;
                }
            }
        }
        if (!levelData[level - 1].loaded) {
            if (levelDataProp != null) {
                levelData[level - 1].loadLevelData(getSkillID(), levelDataProp, getCommon(), level, null);
            } else {
                levelData[level - 1].loadLevelDataByCommon(getSkillID(), getCommon(), level, null);
            }
        }
        return levelData[level - 1];
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + skillID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SkillEntry))
            return false;
        SkillEntry other = (SkillEntry) obj;
        if (skillID != other.skillID)
            return false;
        return true;
    }

    public WzProperty getLevelDataProp() {
        return levelDataProp;
    }

    public void setLevelDataProp(WzProperty levelDataProp) {
        this.levelDataProp = levelDataProp;
    }
}
