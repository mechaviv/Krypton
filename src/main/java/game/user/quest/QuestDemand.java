package game.user.quest;

import game.user.quest.info.QuestItemInfo;
import game.user.quest.info.QuestMobInfo;
import game.user.quest.info.QuestRecord;
import game.user.quest.info.QuestSkillInfo;
import util.FileTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MechAviv on 1/20/2020.
 */
public class QuestDemand {
    private int worldMin;
    private int worldMax;
    private int tamingMobLevelMin;
    private int tamingMobLevelMax;
    private int petTamenessMin;
    private int petTamenessMax;
    private int npcTemplateID;
    private int levelMin;
    private int levelMax;
    private int pop;
    private int repeatInterval;
    private FileTime start;
    private FileTime end;
    private String info;
    private final List<Short> jobs;
    private final List<QuestRecord> precedeQuest;
    private final List<QuestItemInfo> demandItem;
    private final List<QuestMobInfo> demandMob;
    private final List<QuestSkillInfo> demandSkill;
    private final Map<Integer, Boolean> demandPet;
    private String startScript;
    private String endScript;
    private final List<Integer> equipAllNeed;
    private final List<Integer> equipSelectNeed;
    private final List<Integer> fieldEnter;
    private boolean repeatDayByDay;

    public QuestDemand() {
        this.jobs = new ArrayList<>();
        this.precedeQuest = new ArrayList<>();
        this.demandItem = new ArrayList<>();
        this.demandMob = new ArrayList<>();
        this.demandSkill = new ArrayList<>();
        this.equipAllNeed = new ArrayList<>();
        this.equipSelectNeed = new ArrayList<>();
        this.fieldEnter = new ArrayList<>();
        this.demandPet = new HashMap<>();
    }

    public int getWorldMin() {
        return worldMin;
    }

    public void setWorldMin(int worldMin) {
        this.worldMin = worldMin;
    }

    public int getWorldMax() {
        return worldMax;
    }

    public void setWorldMax(int worldMax) {
        this.worldMax = worldMax;
    }

    public int getTamingMobLevelMin() {
        return tamingMobLevelMin;
    }

    public void setTamingMobLevelMin(int tamingMobLevelMin) {
        this.tamingMobLevelMin = tamingMobLevelMin;
    }

    public int getTamingMobLevelMax() {
        return tamingMobLevelMax;
    }

    public void setTamingMobLevelMax(int tamingMobLevelMax) {
        this.tamingMobLevelMax = tamingMobLevelMax;
    }

    public int getPetTamenessMin() {
        return petTamenessMin;
    }

    public void setPetTamenessMin(int petTamenessMin) {
        this.petTamenessMin = petTamenessMin;
    }

    public int getPetTamenessMax() {
        return petTamenessMax;
    }

    public void setPetTamenessMax(int petTamenessMax) {
        this.petTamenessMax = petTamenessMax;
    }

    public int getNpcTemplateID() {
        return npcTemplateID;
    }

    public void setNpcTemplateID(int npcTemplateID) {
        this.npcTemplateID = npcTemplateID;
    }

    public int getLevelMin() {
        return levelMin;
    }

    public void setLevelMin(int levelMin) {
        this.levelMin = levelMin;
    }

    public int getLevelMax() {
        return levelMax;
    }

    public void setLevelMax(int levelMax) {
        this.levelMax = levelMax;
    }

    public int getPop() {
        return pop;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public FileTime getStart() {
        return start;
    }

    public void setStart(FileTime start) {
        this.start = start;
    }

    public FileTime getEnd() {
        return end;
    }

    public void setEnd(FileTime end) {
        this.end = end;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Short> getJobs() {
        return jobs;
    }

    public List<QuestRecord> getPrecedeQuest() {
        return precedeQuest;
    }

    public List<QuestItemInfo> getDemandItem() {
        return demandItem;
    }

    public List<QuestMobInfo> getDemandMob() {
        return demandMob;
    }

    public List<QuestSkillInfo> getDemandSkill() {
        return demandSkill;
    }

    public Map<Integer, Boolean> getDemandPet() {
        return demandPet;
    }

    public String getStartScript() {
        return startScript;
    }

    public void setStartScript(String startScript) {
        this.startScript = startScript;
    }

    public String getEndScript() {
        return endScript;
    }

    public void setEndScript(String endScript) {
        this.endScript = endScript;
    }

    public List<Integer> getEquipAllNeed() {
        return equipAllNeed;
    }

    public List<Integer> getEquipSelectNeed() {
        return equipSelectNeed;
    }

    public List<Integer> getFieldEnter() {
        return fieldEnter;
    }

    public boolean isRepeatDayByDay() {
        return repeatDayByDay;
    }

    public void setRepeatDayByDay(boolean repeatDayByDay) {
        this.repeatDayByDay = repeatDayByDay;
    }
}
